package cz.upce.fei.dt.generator;

import cz.upce.fei.dt.beckend.entities.Component;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.ProductComponent;
import cz.upce.fei.dt.beckend.entities.keys.ProductComponentKey;

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
