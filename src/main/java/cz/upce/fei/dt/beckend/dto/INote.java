package cz.upce.fei.dt.beckend.dto;

import java.time.LocalDateTime;

public interface INote {
    IUser getUser();
    String getNote();
    LocalDateTime getCreated();

}
