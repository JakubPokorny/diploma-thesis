package cz.upce.fei.dt.beckend.services.filters;

import cz.upce.fei.dt.beckend.entities.Contract;
import cz.upce.fei.dt.beckend.entities.Deadline;
import cz.upce.fei.dt.beckend.entities.Status;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ContractFilter {
    private Set<Long> clientsFilter;
    private LocalDate fromDeadlineFilter;
    private LocalDate toDeadlineFilter;
    private Set<Status> statusFilter;
    private Double fromPriceFilter;
    private Double toPriceFilter;
    private Set<Long> productsFilter;
    private LocalDate fromCreatedFilter;
    private LocalDate toCreatedFilter;
    private LocalDate fromUpdatedFilter;
    private LocalDate toUpdatedFilter;
    private Enum<DeadlineFilterTag> tagsFilter = DeadlineFilterTag.ALL;

    public boolean filter(Contract contract){
        Deadline currentDeadline = contract.getCurrentDeadline();
        boolean filterStates = filterStates(currentDeadline.getStatus());
        boolean filterFromDeadline = filterFromDeadline(currentDeadline.getDeadline());
        boolean filterToDeadline = filterToDeadline(currentDeadline.getDeadline());
        boolean filterTag = filterTag(currentDeadline);

        boolean filterProducts = productsFilter == null || productsFilter.isEmpty() || filterProducts(contract);

        return filterStates && filterFromDeadline && filterToDeadline && filterProducts && filterTag;
    }

    private boolean filterTag(Deadline deadline) {
        return switch (tagsFilter) {
            case null -> true;
            case DeadlineFilterTag.ALL -> true;
            case DeadlineFilterTag.WITHOUT_DEADLINE -> deadline.getDeadline() == null;
            case DeadlineFilterTag.BEFORE_DEADLINE -> deadline.getDeadline() != null && (LocalDate.now().isBefore(deadline.getDeadline()) || LocalDate.now().isEqual(deadline.getDeadline()));
            case DeadlineFilterTag.AFTER_DEADLINE -> deadline.getDeadline() != null && LocalDate.now().isAfter(deadline.getDeadline());
            default -> false;
        };
    }

    private boolean filterStates(Status status) {
        return statusFilter == null || statusFilter.isEmpty() || statusFilter.contains(status);
    }

    private boolean filterFromDeadline(LocalDate deadline) {
        if (fromDeadlineFilter == null)
            return true;
        if (deadline == null)
            return false;
        return deadline.isAfter(fromDeadlineFilter) || deadline.isEqual(fromDeadlineFilter);
    }

    private boolean filterToDeadline(LocalDate deadline) {
        if (toDeadlineFilter == null)
            return true;
        if (deadline == null)
            return false;
        return deadline.isBefore(toDeadlineFilter) || deadline.isEqual(toDeadlineFilter);
    }

    private boolean filterProducts(Contract contract){
        Set<Long> products = contract.getContractProducts().stream().map(contractProduct -> contractProduct.getId().getProductId()).collect(Collectors.toSet());
        return products.stream().anyMatch(productID -> productsFilter.contains(productID));
    }
}
