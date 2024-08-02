package cz.upce.fei.dt.generator;

import cz.upce.fei.dt.beckend.entities.Component;

public class ComponentGenerator {

    public static Component generateComponent(Long id) {
        return Component.builder()
                .id(id)
                .name("name" + id)
                .description("desc" + id)
                .inStock(100)
                .minInStock(0)
                .price(100.0)
                .build();
    }
}
