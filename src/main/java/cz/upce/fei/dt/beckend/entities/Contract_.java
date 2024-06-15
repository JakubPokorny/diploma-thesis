package cz.upce.fei.dt.beckend.entities;

import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

import java.time.LocalDateTime;

@StaticMetamodel(Component.class)
public class Contract_ {
    public static volatile SingularAttribute<Contract, Long> id;
    public static volatile SingularAttribute<Contract, Double> price;
    public static volatile SingularAttribute<Contract, Boolean> ownPrice;
    public static volatile SingularAttribute<Contract, LocalDateTime> created;
    public static volatile SingularAttribute<Contract, LocalDateTime> updated;
    public static volatile SingularAttribute<Contract, Contact> contact;
    public static volatile SetAttribute<Contract, ContractProduct> contractProducts;
    public static volatile SetAttribute<Contract, Deadline> deadlines;
    public static volatile SetAttribute<Contract, Note> notes;
    public static volatile SetAttribute<Contract, File> files;

}
