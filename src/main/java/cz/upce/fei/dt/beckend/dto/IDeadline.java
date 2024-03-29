package cz.upce.fei.dt.beckend.dto;

import cz.upce.fei.dt.beckend.entities.State;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface IDeadline {
    Long getId();

    State getState();

    LocalDate getDeadline();

    LocalDateTime getCreated();

    LocalDateTime getUpdated();

    IUser getUser();

    IContract getContract();
}
