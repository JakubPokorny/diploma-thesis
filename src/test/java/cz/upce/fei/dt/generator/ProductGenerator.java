package cz.upce.fei.dt.generator;

import cz.upce.fei.dt.beckend.entities.Product;

public class ProductGenerator {
    public static Product generateProduct(Long id){
        return Product.builder()
                .id(id)
                .name("Product" + id)
                .profit(10.0)
                .build();
    }
}
