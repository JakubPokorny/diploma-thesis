package cz.upce.fei.dt.beckend.services.filters;

import cz.upce.fei.dt.beckend.entities.Product;
import lombok.Data;

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

    public boolean filter(Product product) {
        return componentsFilter == null || componentsFilter.isEmpty() ||  filterComponents(product);
    }

    private boolean filterComponents(Product product){
        Set<Long> components = product.getProductComponents().stream().map(productComponent -> productComponent.getId().getComponentId()).collect(Collectors.toSet());
        return components.stream().anyMatch(componentID -> componentsFilter.contains(componentID));
    }
}
