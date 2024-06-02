package cz.upce.fei.dt.beckend.services.filters;

import lombok.Data;

import java.util.Set;

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
}
