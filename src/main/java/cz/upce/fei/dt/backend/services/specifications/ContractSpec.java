package cz.upce.fei.dt.backend.services.specifications;

import cz.upce.fei.dt.backend.entities.Contract;
import cz.upce.fei.dt.backend.entities.Contract_;
import cz.upce.fei.dt.backend.services.filters.ContractFilter;
import org.springframework.data.jpa.domain.Specification;

public class ContractSpec {
    private static final FilterUtil<Contract> FILTER_UTIL = new FilterUtil<>();

    public static Specification<Contract> filterBy(ContractFilter contractFilter) {
        return Specification
                .where(FILTER_UTIL.findAllDoubleGreaterThanOrEqualTo(contractFilter.getFromInvoicePriceFilter(), Contract_.INVOICE_PRICE))
                .and(FILTER_UTIL.findAllLongEqual(contractFilter.getIdFilter(), Contract_.ID))
                .and(FILTER_UTIL.findAllStringLikeIgnoreCase(contractFilter.getNoteFilter(), Contract_.NOTE))

                .and(FILTER_UTIL.findAllDoubleGreaterThanOrEqualTo(contractFilter.getFromInvoicePriceFilter(), Contract_.INVOICE_PRICE))
                .and(FILTER_UTIL.findAllDoubleLessThanOrEqualTo(contractFilter.getToInvoicePriceFilter(), Contract_.INVOICE_PRICE))

                .and(FILTER_UTIL.findAllDoubleGreaterThanOrEqualTo(contractFilter.getFromTotalCostFilter(), Contract_.TOTAL_COST))
                .and(FILTER_UTIL.findAllDoubleLessThanOrEqualTo(contractFilter.getToTotalCostFilter(), Contract_.TOTAL_COST))

                .and(FILTER_UTIL.findAllDoubleGreaterThanOrEqualTo(contractFilter.getFromTotalProfitFilter(), Contract_.TOTAL_PROFIT))
                .and(FILTER_UTIL.findAllDoubleLessThanOrEqualTo(contractFilter.getToTotalProfitFilter(), Contract_.TOTAL_PROFIT))

                .and(FILTER_UTIL.findAllLocalDateGreaterThanOrEqualTo(contractFilter.getFromFinalDeadlineFilter(), Contract_.FINAL_DEADLINE))
                .and(FILTER_UTIL.findAllLocalDateLessThanOrEqualTo(contractFilter.getToFinalDeadlineFilter(), Contract_.FINAL_DEADLINE))

                .and(FILTER_UTIL.findAllLocalDateGreaterThanOrEqualTo(contractFilter.getFromCreatedFilter(), Contract_.CREATED))
                .and(FILTER_UTIL.findAllLocalDateLessThanOrEqualTo(contractFilter.getToCreatedFilter(), Contract_.CREATED))

                .and(FILTER_UTIL.findAllLocalDateGreaterThanOrEqualTo(contractFilter.getFromUpdatedFilter(), Contract_.UPDATED))
                .and(FILTER_UTIL.findAllLocalDateLessThanOrEqualTo(contractFilter.getToUpdatedFilter(), Contract_.UPDATED));
    }
}
