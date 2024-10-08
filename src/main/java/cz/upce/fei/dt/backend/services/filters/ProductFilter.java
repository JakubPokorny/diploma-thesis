package cz.upce.fei.dt.backend.services.filters;

import cz.upce.fei.dt.backend.entities.Product;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ProductFilter {
    private String nameFilter;
    private Double fromProductionPriceFilter;
    private Double toProductionPriceFilter;
    private Double fromProfitFilter;
    private Double toProfitFilter;
    private Double fromSellingPriceFilter;
    private Double toSellingPriceFilter;
    private Boolean ownPriceFilter;
    private Set<Long> componentsFilter;
    private LocalDate fromCreatedFilter;
    private LocalDate toCreatedFilter;
    private LocalDate fromUpdatedFilter;
    private LocalDate toUpdatedFilter;

    public boolean filter(Product product) {
        return componentsFilter == null || componentsFilter.isEmpty() ||  filterComponents(product);
    }

    private boolean filterComponents(Product product){
        Set<Long> components = product.getProductComponents().stream().map(productComponent -> productComponent.getId().getComponentId()).collect(Collectors.toSet());
        return components.stream().anyMatch(componentID -> componentsFilter.contains(componentID));
    }
}
