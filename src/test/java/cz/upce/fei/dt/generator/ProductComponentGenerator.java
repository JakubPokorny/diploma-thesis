package cz.upce.fei.dt.generator;

import cz.upce.fei.dt.backend.entities.Component;
import cz.upce.fei.dt.backend.entities.Product;
import cz.upce.fei.dt.backend.entities.ProductComponent;
import cz.upce.fei.dt.backend.entities.keys.ProductComponentKey;

public class ProductComponentGenerator {
    public static ProductComponent generateProductComponent(Component component, Product product) {
        return  new ProductComponent(
                new ProductComponentKey(product.getId(), component.getId()),
                1,
                component,
                product
        );
    }
}
