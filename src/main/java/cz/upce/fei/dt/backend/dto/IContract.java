package cz.upce.fei.dt.backend.dto;

import java.time.LocalDateTime;
import java.util.Set;

public interface IContract {
    Long getId();
    LocalDateTime getCreated();
    LocalDateTime getUpdated();
    IContact getContact();
    Set<IContractProduct> getContractProducts();
    Set<IDeadline> getDeadlines();
    Set<INote> getNotes();
    Set<IFile> getFiles();



}
