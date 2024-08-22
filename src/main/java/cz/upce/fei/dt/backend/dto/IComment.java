package cz.upce.fei.dt.backend.dto;

import java.time.LocalDateTime;

public interface IComment {
    String getComment();

    LocalDateTime getCreated();

    String getFirstName();

    String getLastName();
}
