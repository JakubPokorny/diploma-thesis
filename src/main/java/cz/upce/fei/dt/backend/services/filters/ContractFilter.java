package cz.upce.fei.dt.backend.services.filters;

import cz.upce.fei.dt.backend.entities.Contract;
import cz.upce.fei.dt.backend.entities.Deadline;
import cz.upce.fei.dt.backend.entities.Status;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ContractFilter {
    private Long idFilter;
    private String noteFilter;
    private Set<Long> clientsFilter;
    private LocalDate fromFinalDeadlineFilter;
    private LocalDate toFinalDeadlineFilter;
    private LocalDate fromDeadlineFilter;
    private LocalDate toDeadlineFilter;
    private Set<Status> statusFilter;
    private Double fromInvoicePriceFilter;
    private Double toInvoicePriceFilter;
    private Double fromTotalCostFilter;
    private Double toTotalCostFilter;
    private Double fromTotalProfitFilter;
    private Double toTotalProfitFilter;
    private Set<Long> productsFilter;
    private LocalDate fromCreatedFilter;
    private LocalDate toCreatedFilter;
    private LocalDate fromUpdatedFilter;
    private LocalDate toUpdatedFilter;
    private Enum<ContractFilterTag> contractFilterTag = ContractFilterTag.ALL;

    public enum ContractFilterTag {
        ALL,
        BEFORE_DEADLINE,
        WITHOUT_DEADLINE,
        AFTER_DEADLINE,
        SUCCESS,
        CONTRAST,
        PENDING,
        WARNING,
        ERROR
    }

    public boolean filter(Contract contract) {
        Deadline currentDeadline = contract.getCurrentDeadline();
        boolean filterStates = filterStates(currentDeadline.getStatus());
        boolean filterFromDeadline = filterFromDeadline(currentDeadline.getDeadline());
        boolean filterToDeadline = filterToDeadline(currentDeadline.getDeadline());
        boolean filterContractTags = filterContractTags(currentDeadline);

        boolean filterClients = clientsFilter == null || clientsFilter.isEmpty() || filterClients(contract);
        boolean filterProducts = productsFilter == null || productsFilter.isEmpty() || filterProducts(contract);

        return filterStates && filterFromDeadline && filterToDeadline && filterClients && filterProducts && filterContractTags;
    }

    private boolean filterClients(Contract contract) {
        return clientsFilter.stream().anyMatch(clientId -> clientId.equals(contract.getContact().getId()));

    }

    private boolean filterContractTags(Deadline deadline) {
        Status.Theme theme = deadline.getStatus().getTheme();
        return switch (contractFilterTag) {
            case null -> true;
            case ContractFilterTag.ALL -> true;
            case ContractFilterTag.WITHOUT_DEADLINE -> deadline.isWithoutDeadline();
            case ContractFilterTag.BEFORE_DEADLINE -> deadline.isBeforeOrNowDeadline();
            case ContractFilterTag.AFTER_DEADLINE -> deadline.isAfterDeadline();
            case ContractFilterTag.SUCCESS -> theme == Status.Theme.SUCCESS;
            case ContractFilterTag.CONTRAST -> theme == Status.Theme.CONTRAST;
            case ContractFilterTag.PENDING -> theme == Status.Theme.PENDING;
            case ContractFilterTag.WARNING -> theme == Status.Theme.WARNING;
            case ContractFilterTag.ERROR -> theme == Status.Theme.ERROR;
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

    private boolean filterProducts(Contract contract) {
        Set<Long> products = contract.getContractProducts().stream().map(contractProduct -> contractProduct.getId().getProductId()).collect(Collectors.toSet());
        return products.stream().anyMatch(productID -> productsFilter.contains(productID));
    }
}
