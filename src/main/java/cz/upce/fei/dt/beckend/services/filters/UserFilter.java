package cz.upce.fei.dt.beckend.services.filters;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserFilter {
    private String firstNameFilter;
    private String lastNameFilter;
    private String emailFilter;
    private String roleFilter;
    private String tokenFilter;
    private LocalDate fromCreatedFilter;
    private LocalDate toCreatedFilter;
    private LocalDate fromUpdatedFilter;
    private LocalDate toUpdatedFilter;
}
