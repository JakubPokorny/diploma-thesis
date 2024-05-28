package cz.upce.fei.dt.beckend.services.filters;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ComponentFilter {
    private String nameFilter;
    private String descriptionFilter;
    private Integer fromInStockFilter;
    private Integer toInStockFilter;
    private Integer fromMinInStockFilter;
    private Integer toMinInStockFilter;
    private Double fromPriceFilter;
    private Double toPriceFilter;
    private LocalDateTime fromUpdatedFilter;
    private LocalDateTime toUpdatedFilter;
    private Set<Long> productsFilter;
    private Set<Long> usersFilter;
    private Enum<ComponentTag> tagFilter = ComponentTag.ALL;
}