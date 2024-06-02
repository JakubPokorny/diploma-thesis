package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.ProductComponent;
import cz.upce.fei.dt.beckend.repositories.ProductComponentRepository;
import cz.upce.fei.dt.beckend.repositories.ProductRepository;
import cz.upce.fei.dt.beckend.services.filters.ProductFilter;
import cz.upce.fei.dt.beckend.services.specifications.ProductSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductComponentRepository productComponentRepository;

    public Stream<Product> findAllByName(int page, int pageSize, String searchTerm) {
        return productRepository.findAllByName(PageRequest.of(page, pageSize), searchTerm)
                .stream()
                .map(iProduct -> Product.builder()
                        .id(iProduct.getId())
                        .name(iProduct.getName())
                        .build()
                );
    }

    public Stream<Product> findAll(Query<Product, ProductFilter> query) {
        Specification<Product> spec = ProductSpec.filterBy(query.getFilter().orElse(new ProductFilter()));
        return productRepository.findAll(spec, VaadinSpringDataHelpers.toSpringPageRequest(query)).stream();
    }

    public int getCount(Query<Product, ProductFilter> query){
        Specification<Product> spec = ProductSpec.filterBy(query.getFilter().orElse(new ProductFilter()));
        return (int) productRepository.findAll(spec, VaadinSpringDataHelpers.toSpringPageRequest(query)).stream().count();
    }

    @Transactional
    public void saveProduct(Product product) {
        product.setUpdated(LocalDateTime.now());
        if (product.getId() == null) {
            Set<ProductComponent> productComponents = product.getProductComponents();
            product.setProductComponents(null);
            Product savedProduct = productRepository.save(product);
            for (ProductComponent productComponent : productComponents) {
                productComponent.setProduct(savedProduct);
                productComponentRepository.save(productComponent);
            }
        } else {
            productComponentRepository.deleteAll(
                    product.getDifference(
                            productComponentRepository.findAllByProductId(product.getId())
                    )
            );
            productRepository.save(product);
        }
    }

    public void deleteProduct(Product product) throws Exception {
        Product wantToDelete = productRepository.findById(product.getId())
                .orElseThrow(() -> new Exception("Produkt " + product.getName() + " nenalezen."));
        productRepository.delete(wantToDelete);
    }
}
