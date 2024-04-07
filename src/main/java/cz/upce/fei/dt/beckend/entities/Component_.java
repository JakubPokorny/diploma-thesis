package cz.upce.fei.dt.beckend.entities;

import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

import java.time.LocalDateTime;

@StaticMetamodel(Component.class)
public class Component_ {
    public static volatile SingularAttribute<Component, Long> id;
    public static volatile SingularAttribute<Component, String> name;
    public static volatile SingularAttribute<Component, String> description;
    public static volatile SingularAttribute<Component, Integer> inStock;
    public static volatile SingularAttribute<Component, Integer> minInStock;
    public static volatile SingularAttribute<Component, LocalDateTime> created;
    public static volatile SingularAttribute<Component, LocalDateTime> updated;
    public static volatile SetAttribute<Component, ProductComponent> productComponents;
    public static volatile SingularAttribute<Component, User> user;
}
