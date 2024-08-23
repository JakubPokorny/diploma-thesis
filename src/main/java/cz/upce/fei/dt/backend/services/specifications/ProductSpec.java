package cz.upce.fei.dt.backend.services.specifications;

import cz.upce.fei.dt.backend.entities.Product;
import cz.upce.fei.dt.backend.entities.Product_;
import cz.upce.fei.dt.backend.services.filters.ProductFilter;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpec {
    private static final FilterUtil<Product> FILTER_UTIL = new FilterUtil<>();

    public static Specification<Product> filterBy(ProductFilter productFilter) {
        return Specification
                .where(FILTER_UTIL.findAllStringLikeIgnoreCase(productFilter.getNameFilter(), Product_.NAME))
                .and(FILTER_UTIL.findAllDoubleGreaterThanOrEqualTo(productFilter.getFromProductionPriceFilter(), Product_.PRODUCTION_PRICE))
                .and(FILTER_UTIL.findAllDoubleLessThanOrEqualTo(productFilter.getToProductionPriceFilter(), Product_.PRODUCTION_PRICE))

                .and(FILTER_UTIL.findAllDoubleGreaterThanOrEqualTo(productFilter.getFromProfitFilter(), Product_.PROFIT))
                .and(FILTER_UTIL.findAllDoubleLessThanOrEqualTo(productFilter.getToProfitFilter(), Product_.PROFIT))

                .and(FILTER_UTIL.findAllDoubleGreaterThanOrEqualTo(productFilter.getFromSellingPriceFilter(), Product_.SELLING_PRICE))
                .and(FILTER_UTIL.findAllDoubleLessThanOrEqualTo(productFilter.getToSellingPriceFilter(), Product_.SELLING_PRICE))

                .and(FILTER_UTIL.findAllLocalDateLessThanOrEqualTo(productFilter.getToCreatedFilter(), Product_.CREATED))
                .and(FILTER_UTIL.findAllLocalDateGreaterThanOrEqualTo(productFilter.getFromCreatedFilter(), Product_.CREATED))

                .and(FILTER_UTIL.findAllLocalDateLessThanOrEqualTo(productFilter.getToUpdatedFilter(), Product_.UPDATED))
                .and(FILTER_UTIL.findAllLocalDateGreaterThanOrEqualTo(productFilter.getFromUpdatedFilter(), Product_.UPDATED));
    }
}
