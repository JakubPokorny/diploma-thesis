package cz.upce.fei.dt.beckend.dto;

import java.time.LocalDateTime;

public interface IComponent {
    Long getId();

    String getName();

    String getDescription();

    Double getPrice();

    LocalDateTime getUpdated();

    IUser getUser();
}
