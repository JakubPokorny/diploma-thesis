package cz.upce.fei.dt.backend.dto;

import java.time.LocalDate;

public interface ICheckExpiredPartialDeadline {
    String getEmail();

    Long getId();

    Double getPrice();

    String getStatus();

    LocalDate getPartialDeadline();

    String getOrderedProducts();
}
