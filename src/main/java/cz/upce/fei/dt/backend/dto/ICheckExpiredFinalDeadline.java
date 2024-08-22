package cz.upce.fei.dt.backend.dto;

import java.time.LocalDate;

public interface ICheckExpiredFinalDeadline {
    Long getId();

    Double getPrice();

    LocalDate getFinalDeadline();

    String getOrderedProducts();

    String getEmail();
}
