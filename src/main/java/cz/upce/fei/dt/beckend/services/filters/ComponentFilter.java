package cz.upce.fei.dt.beckend.services.filters;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ComponentFilter {
    private String nameFilter;
    private String descriptionFilter;
    private Integer fromAmountFilter;
    private Integer toAmountFilter;
    private Integer fromMinAmountFilter;
    private Integer toMinAmountFilter;
    private LocalDateTime fromUpdatedFilter;
    private LocalDateTime toUpdatedFilter;
    private Set<Long> productsFilter;
    private Set<Long> usersFilter;
}
