package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.fei.dt.beckend.dto.CheckStockDto;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.ProductComponent;
import cz.upce.fei.dt.beckend.repositories.ProductRepository;
import cz.upce.fei.dt.beckend.services.filters.ProductFilter;
import cz.upce.fei.dt.beckend.services.specifications.ProductSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    public Stream<Product> findAllByName(int page, int pageSize, String searchTerm) {
        return productRepository.findAllByName(PageRequest.of(page, pageSize), searchTerm)
                .stream()
                .map(iProduct -> Product.builder()
                        .id(iProduct.getId())
                        .name(iProduct.getName())
                        .sellingPrice(iProduct.getSellingPrice())
                        .build()
                );
    }

    public List<CheckStockDto> findAllByID(Iterable<Long> productsID) {
        return productRepository.findAllByID(productsID)
                .stream()
                .map(CheckStockDto::toCheckStockDTO)
                .toList();
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
        productComponentService.deleteAll(orphans);

        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Product product) throws Exception {
        Product wantToDelete = productRepository.findById(product.getId())
                .orElseThrow(() -> new Exception("Produkt " + product.getName() + " nenalezen."));
        productRepository.delete(wantToDelete);
    }

    @Transactional
    public void updatePrices(Iterable<Long> productIDs) {
        List<Product> products = productRepository.findAllById(productIDs);
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
}
