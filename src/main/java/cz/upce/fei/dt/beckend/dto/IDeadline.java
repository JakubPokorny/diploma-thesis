package cz.upce.fei.dt.beckend.dto;

import cz.upce.fei.dt.beckend.entities.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface IDeadline {
    Long getId();

    Status getStatus();

    LocalDate getDeadline();

    LocalDateTime getCreated();

    String getFirstName();

    String getLastName();
}
