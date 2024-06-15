package cz.upce.fei.dt.beckend.services.specifications;

import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.Product_;
import cz.upce.fei.dt.beckend.services.filters.ProductFilter;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpec {
    private static final FilterUtil<Product> FILTER_UTIL = new FilterUtil<>();

    public static Specification<Product> filterBy(ProductFilter productFilter) {
        return Specification
                .where(FILTER_UTIL.findAllStringLikeIgnoreCase(productFilter.getNameFilter(), Product_.name.getName()))
                .and(FILTER_UTIL.findAllDoubleGreaterThanOrEqualTo(productFilter.getFromProductionPriceFilter(), Product_.productionPrice.getName()))
                .and(FILTER_UTIL.findAllDoubleLessThanOrEqualTo(productFilter.getToProductionPriceFilter(), Product_.productionPrice.getName()))
                .and(FILTER_UTIL.findAllDoubleGreaterThanOrEqualTo(productFilter.getFromProfitFilter(), Product_.profit.getName()))
                .and(FILTER_UTIL.findAllDoubleLessThanOrEqualTo(productFilter.getToProfitFilter(), Product_.profit.getName()))
                .and(FILTER_UTIL.findAllDoubleGreaterThanOrEqualTo(productFilter.getFromSellingPriceFilter(), Product_.sellingPrice.getName()))
                .and(FILTER_UTIL.findAllDoubleLessThanOrEqualTo(productFilter.getToSellingPriceFilter(), Product_.sellingPrice.getName()));
    }
}
