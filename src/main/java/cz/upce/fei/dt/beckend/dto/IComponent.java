package cz.upce.fei.dt.beckend.dto;

import cz.upce.fei.dt.beckend.entities.keys.ProductComponentKey;

import java.time.LocalDateTime;

//todo repair
public interface IComponent {
    Long getId();

    String getName();

    String getDescription();

    int getInStock();

    Integer getMinInStock();

    Double getPrice();

    LocalDateTime getUpdated();

    IUser getUser();

    IProductComponent getProductComponents();

    Long getUserId();

    String getFirstName();

    String getLastName();

    ProductComponentKey getProductComponentId();

    int getProductComponentAmount();

    Long getProductId();

    String getProductName();
}
