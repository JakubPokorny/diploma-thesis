package cz.upce.fei.dt.backend.dto;

import cz.upce.fei.dt.backend.entities.keys.ProductComponentKey;

public interface ICheckProduct {
    Long getProductID();

    Long getComponentID();

    ProductComponentKey getProductComponentKey();

    String getComponentName();

    int getComponentsPerProduct();

    int getComponentsInStock();

    int getMinComponentsInStock();

    String getEmail();
}
