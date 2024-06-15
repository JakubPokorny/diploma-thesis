package cz.upce.fei.dt.beckend.dto;

import cz.upce.fei.dt.beckend.entities.keys.ProductComponentKey;

public interface ICheckProduct {
    Long getProductID();
    Long getComponentID();
    ProductComponentKey getProductComponentKey();
    String getComponentName();
    int getComponentsPerProduct();
    int getComponentsInStock();
    Integer getMinComponentsInStock();
    String getEmail();
}
