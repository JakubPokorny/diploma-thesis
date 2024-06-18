package cz.upce.fei.dt.beckend.services.filters;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ContactFilter {
    private String icoFilter;
    private String clientFilter;
    private String emailFilter;
    private String phoneFilter;
    private String invoiceAddressFilter;
    private String deliveryAddressFilter;
    private LocalDate fromUpdatedFilter;
    private LocalDate toUpdatedFilter;
}
