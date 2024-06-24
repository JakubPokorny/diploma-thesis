package cz.upce.fei.dt.beckend.services.filters;

import cz.upce.fei.dt.beckend.entities.Component;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

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
    private LocalDate fromCreatedFilter;
    private LocalDate toCreatedFilter;
    private LocalDate fromUpdatedFilter;
    private LocalDate toUpdatedFilter;
    private Set<Long> productsFilter;
    private Set<Long> usersFilter;
    private Enum<ComponentTag> tagFilter = ComponentTag.ALL;

    public boolean filter(Component component) {
        boolean filterProducts = productsFilter == null || productsFilter.isEmpty() || filterProducts(component);
        boolean filterUsers = usersFilter == null || usersFilter.isEmpty() || filterUsers(component);
        return filterProducts && filterUsers;
    }

    private boolean filterProducts(Component component) {
        Set<Long> products = component.getProductComponents().stream().map(productComponent -> productComponent.getId().getProductId()).collect(Collectors.toSet());
        return products.stream().anyMatch(productID -> productsFilter.contains(productID));
    }

    private boolean filterUsers(Component component) {
        return component.getUser() != null && usersFilter.stream().anyMatch(userID -> userID.equals(component.getUser().getId()));
    }
}