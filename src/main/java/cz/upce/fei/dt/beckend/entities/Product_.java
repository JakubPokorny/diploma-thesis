package cz.upce.fei.dt.beckend.entities;

import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

import java.time.LocalDateTime;

@StaticMetamodel(Product.class)
public class Product_ {
    public static volatile SingularAttribute<Product, Long> id;
    public static volatile SingularAttribute<Product, String> name;
    public static volatile SingularAttribute<Product, Double> productionPrice;
    public static volatile SingularAttribute<Product, Double> profit;
    public static volatile SingularAttribute<Product, Double> sellingPrice;
    public static volatile SingularAttribute<Product, Boolean> ownSellingPrice;
    public static volatile SetAttribute<Component, ProductComponent> productComponents;
    public static volatile SingularAttribute<Product, LocalDateTime> created;
    public static volatile SingularAttribute<Product, LocalDateTime> updated;

}
