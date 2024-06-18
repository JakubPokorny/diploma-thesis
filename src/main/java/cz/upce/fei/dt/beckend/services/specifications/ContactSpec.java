package cz.upce.fei.dt.beckend.services.specifications;

import cz.upce.fei.dt.beckend.entities.Address_;
import cz.upce.fei.dt.beckend.entities.Contact;
import cz.upce.fei.dt.beckend.entities.Contact_;
import cz.upce.fei.dt.beckend.services.filters.ContactFilter;
import org.springframework.data.jpa.domain.Specification;

public class ContactSpec {
    private static final FilterUtil<Contact> FILTER_UTIL = new FilterUtil<>();

    public static Specification<Contact> filterBy(ContactFilter contactFilter){
        return Specification
                .where(FILTER_UTIL.findAllStringLikeIgnoreCase(contactFilter.getIcoFilter(), Contact_.ICO.getName()))
                .and(FILTER_UTIL.findAllStringLikeIgnoreCase(contactFilter.getClientFilter(), Contact_.CLIENT))
                .and(FILTER_UTIL.findAllStringLikeIgnoreCase(contactFilter.getEmailFilter(), Contact_.EMAIL))
                .and(FILTER_UTIL.findAllStringLikeIgnoreCase(contactFilter.getPhoneFilter(), Contact_.PHONE))
                .and(findAllAddressLikeIgnoreCase(contactFilter.getInvoiceAddressFilter(), Contact_.INVOICE_ADDRESS))
                .and(findAllAddressLikeIgnoreCase(contactFilter.getDeliveryAddressFilter(), Contact_.DELIVERY_ADDRESS))
                .and(FILTER_UTIL.findAllLocalDateLessThanOrEqualTo(contactFilter.getToUpdatedFilter(), Contact_.UPDATED))
                .and(FILTER_UTIL.findAllLocalDateGreaterThanOrEqualTo(contactFilter.getFromUpdatedFilter(), Contact_.UPDATED));
    }

    private static  Specification<Contact> findAllAddressLikeIgnoreCase(String filter, String address){
        return (root, query, builder) -> filter == null || filter.isEmpty()
                ? null
                : builder.or(
                    builder.like(builder.lower(root.get(address).get(Address_.STREET)), "%" + filter.toLowerCase() + "%"),
                    builder.like(builder.lower(root.get(address).get(Address_.HOUSE_NUMBER)), "%" + filter.toLowerCase() + "%"),
                    builder.like(builder.lower(root.get(address).get(Address_.CITY)), "%" + filter.toLowerCase() + "%"),
                    builder.like(builder.lower(root.get(address).get(Address_.ZIP_CODE)), "%" + filter.toLowerCase() + "%"),
                    builder.like(builder.lower(root.get(address).get(Address_.STATE)), "%" + filter.toLowerCase() + "%")
                );
    }
}
