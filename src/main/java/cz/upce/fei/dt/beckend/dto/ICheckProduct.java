package cz.upce.fei.dt.beckend.dto;

public interface ICheckProduct {

    int getComponentPerProduct();
    Long getComponentId();
    String getComponentName();
    int getComponentsInStock();
    Integer getMinComponentsInStock();
    String getEmail();
}
