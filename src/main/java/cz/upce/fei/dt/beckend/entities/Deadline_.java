package cz.upce.fei.dt.beckend.entities;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

import java.time.LocalDate;
import java.time.LocalDateTime;

@StaticMetamodel(Deadline.class)
public class Deadline_ {
    public static volatile SingularAttribute<Deadline, Long> id;
    public static volatile SingularAttribute<Deadline, Status> status;
    public static volatile SingularAttribute<Deadline, LocalDate> deadline;
    public static volatile SingularAttribute<Deadline, LocalDateTime> created;
    public static volatile SingularAttribute<Deadline, LocalDateTime> updated;
    public static volatile SingularAttribute<Deadline, User> user;
    public static volatile SingularAttribute<Deadline, Contract> contract;

}
