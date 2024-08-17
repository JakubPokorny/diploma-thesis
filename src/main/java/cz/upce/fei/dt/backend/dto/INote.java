package cz.upce.fei.dt.backend.dto;

import java.time.LocalDateTime;

public interface INote {
    String getNote();

    LocalDateTime getCreated();

    String getFirstName();

    String getLastName();
}
