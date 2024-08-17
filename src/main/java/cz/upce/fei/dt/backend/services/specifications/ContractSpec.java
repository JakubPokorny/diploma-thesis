package cz.upce.fei.dt.backend.services.specifications;

import cz.upce.fei.dt.backend.entities.Contract;
import cz.upce.fei.dt.backend.entities.Contract_;
import cz.upce.fei.dt.backend.services.filters.ContractFilter;
import org.springframework.data.jpa.domain.Specification;

public class ContractSpec {
    private static final FilterUtil<Contract> FILTER_UTIL = new FilterUtil<>();

    public static Specification<Contract> filterBy(ContractFilter contractFilter) {
        return Specification
                .where(FILTER_UTIL.findAllDoubleGreaterThanOrEqualTo(contractFilter.getFromPriceFilter(), Contract_.PRICE))
                .and(FILTER_UTIL.findAllLongEqual(contractFilter.getIdFilter(), Contract_.ID))
                .and(FILTER_UTIL.findAllDoubleLessThanOrEqualTo(contractFilter.getToPriceFilter(), Contract_.PRICE))
                .and(FILTER_UTIL.findAllLocalDateGreaterThanOrEqualTo(contractFilter.getFromCreatedFilter(), Contract_.CREATED))
                .and(FILTER_UTIL.findAllLocalDateLessThanOrEqualTo(contractFilter.getToCreatedFilter(), Contract_.CREATED))
                .and(FILTER_UTIL.findAllLocalDateGreaterThanOrEqualTo(contractFilter.getFromUpdatedFilter(), Contract_.UPDATED))
                .and(FILTER_UTIL.findAllLocalDateLessThanOrEqualTo(contractFilter.getToUpdatedFilter(), Contract_.UPDATED));
    }
}
