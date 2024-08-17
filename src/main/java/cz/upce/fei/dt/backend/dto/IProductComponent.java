package cz.upce.fei.dt.backend.dto;

import cz.upce.fei.dt.backend.entities.keys.ProductComponentKey;

public interface IProductComponent {
    ProductComponentKey getId();
    int getComponentsPerProduct();
    IProduct getProduct();
    IComponent getComponent();

}
