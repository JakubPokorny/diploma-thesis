package cz.upce.fei.dt.beckend.services;

import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.ProductComponent;
import cz.upce.fei.dt.beckend.repositories.ProductComponentRepository;
import cz.upce.fei.dt.beckend.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductComponentRepository productComponentRepository;

    public List<Product> findAllProductsIdAndName(){
        //todo add padding
        List<Product> products = new ArrayList<>();
        productRepository.findAllProductsIdAndName().forEach(
                projection -> products.add(
                        Product.builder()
                                .id(projection.getId())
                                .name(projection.getName())
                                .build())
        );
        return products;
    }
    public Stream<Product> findAll(int page, int pageSize){
        return productRepository.findAll(PageRequest.of(page,pageSize)).stream();
    }

    @Transactional
    public void saveProduct(Product product){
        product.setUpdated(LocalDateTime.now());
        if (product.getId() == null){
            List<ProductComponent> productComponents = product.getProductComponents();
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
    public void deleteProduct(Product product) throws Exception{
        Product wantToDelete = productRepository.findById(product.getId())
                .orElseThrow(() -> new Exception("Produkt "+ product.getName() +" nenalezan."));
        productRepository.delete(wantToDelete);
    }
}
