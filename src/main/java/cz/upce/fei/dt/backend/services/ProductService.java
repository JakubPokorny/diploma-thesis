package cz.upce.fei.dt.backend.services;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.fei.dt.backend.dto.CheckStockDto;
import cz.upce.fei.dt.backend.dto.IMostSaleableProducts;
import cz.upce.fei.dt.backend.entities.Component;
import cz.upce.fei.dt.backend.entities.Product;
import cz.upce.fei.dt.backend.entities.ProductComponent;
import cz.upce.fei.dt.backend.repositories.ProductRepository;
import cz.upce.fei.dt.backend.services.filters.DashboardFilter;
import cz.upce.fei.dt.backend.services.filters.ProductFilter;
import cz.upce.fei.dt.backend.services.specifications.ProductSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProductService extends AbstractBackEndDataProvider<Product, ProductFilter> {
    private final ProductRepository productRepository;
    private final ProductComponentService productComponentService;

    @Override
    public Stream<Product> fetchFromBackEnd(Query<Product, ProductFilter> query) {
        Specification<Product> spec = ProductSpec.filterBy(query.getFilter().orElse(new ProductFilter()));
        Stream<Product> stream = productRepository.findAll(spec, VaadinSpringDataHelpers.toSpringDataSort(query)).stream();

        if (query.getFilter().isPresent()) {
            stream = stream.filter(product -> query.getFilter().get().filter(product));
        }

        return stream.skip(query.getOffset()).limit(query.getLimit());
    }

    @Override
    public int sizeInBackEnd(Query<Product, ProductFilter> query) {
        return (int) fetchFromBackEnd(query).count();
    }

    public Stream<Product> findAllByName(Query<Product, String> query) {
        return productRepository.findAllByName(PageRequest.of(query.getPage(), query.getPageSize()), query.getFilter().orElse(""))
                .stream()
                .map(iProduct -> Product.builder()
                        .id(iProduct.getId())
                        .name(iProduct.getName())
                        .sellingPrice(iProduct.getSellingPrice())
                        .productionPrice(iProduct.getProductionPrice())
                        .build()
                );
    }

    public List<CheckStockDto> findAllByID(Iterable<Long> productIDs) {
        return productRepository.findAllByID(productIDs)
                .stream()
                .map(CheckStockDto::toCheckStockDTO)
                .toList();
    }

    public List<Product> findAllByComponent(Component component) {
        return component.getId() == null
                ? Collections.emptyList()
                : productRepository.findAllByComponentId(component.getId());
    }

    @Transactional
    public void saveProduct(Product product) {
        product.setUpdated(LocalDateTime.now());
        if (product.getId() == null) {
            Set<ProductComponent> productComponents = product.getProductComponents();
            product.setProductComponents(null);
            product = productRepository.save(product);
            for (ProductComponent productComponent : productComponents) {
                productComponent.setProduct(product);
            }
            product.setProductComponents(productComponents);
        }
        List<ProductComponent> orphans = productComponentService.findAllByProductId(product.getId());
        orphans.removeAll(product.getProductComponents());
        productComponentService.deleteAll(orphans);

        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Product product) {
        productRepository.delete(product);
    }

    @Transactional
    public void updatePrices(Iterable<Long> productIDs) {
        List<Product> products = productRepository.findAllById(productIDs);
        updatePrices(products);
    }

    public void updatePrices(List<Product> products) {
        for (Product product : products) {
            double productionPrice = 0;
            for (ProductComponent pc : product.getProductComponents()) {
                productionPrice += pc.getComponentsPerProduct() * pc.getComponent().getPrice();
            }
            product.setProductionPrice(productionPrice);
            if (!product.getOwnSellingPrice()) {
                double sellingPrice = product.getProductionPrice() * (1 + (product.getProfit() / 100));
                product.setSellingPrice(sellingPrice);
            }
        }
        productRepository.saveAll(products);
    }

    public List<IMostSaleableProducts> getMostSaleableProducts(DashboardFilter dashboardFilter) {
        return productRepository.getMostSaleableProducts(
                dashboardFilter.getProductIDs(),
                dashboardFilter.getStatusFilter(),
                dashboardFilter.getFromDateTime(),
                dashboardFilter.getToDateTime());
    }

}
