package cz.upce.fei.dt.beckend.dto;

import cz.upce.fei.dt.beckend.entities.keys.ProductComponentKey;

public interface IProductComponent {
    ProductComponentKey getId();
    int getAmount();

}
