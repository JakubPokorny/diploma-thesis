package cz.upce.fei.dt.beckend.services.specifications;

import cz.upce.fei.dt.beckend.entities.Contract;
import cz.upce.fei.dt.beckend.entities.Contract_;
import cz.upce.fei.dt.beckend.services.filters.ContractFilter;
import org.springframework.data.jpa.domain.Specification;

public class ContractSpec {
    private static final FilterUtil<Contract> FILTER_UTIL = new FilterUtil<>();

    public static Specification<Contract> filterBy(ContractFilter contractFilter) {
        return Specification
                .where(FILTER_UTIL.findAllDoubleGreaterThanOrEqualTo(contractFilter.getFromPriceFilter(), Contract_.price.getName()))
                .and(FILTER_UTIL.findAllLongEqual(contractFilter.getIdFilter(), Contract_.ID))
                .and(FILTER_UTIL.findAllDoubleLessThanOrEqualTo(contractFilter.getToPriceFilter(), Contract_.price.getName()))
                .and(FILTER_UTIL.findAllLocalDateGreaterThanOrEqualTo(contractFilter.getFromCreatedFilter(), Contract_.created.getName()))
                .and(FILTER_UTIL.findAllLocalDateLessThanOrEqualTo(contractFilter.getToCreatedFilter(), Contract_.created.getName()))
                .and(FILTER_UTIL.findAllLocalDateGreaterThanOrEqualTo(contractFilter.getFromUpdatedFilter(), Contract_.updated.getName()))
                .and(FILTER_UTIL.findAllLocalDateLessThanOrEqualTo(contractFilter.getToUpdatedFilter(), Contract_.updated.getName()));
    }
}
