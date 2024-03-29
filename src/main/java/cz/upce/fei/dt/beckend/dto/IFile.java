package cz.upce.fei.dt.beckend.dto;

import java.time.LocalDateTime;

public interface IFile {
    Long getId();
    String getName();
    String getPath();
    String getType();
    Long getSize();
    LocalDateTime getCreated();

}
