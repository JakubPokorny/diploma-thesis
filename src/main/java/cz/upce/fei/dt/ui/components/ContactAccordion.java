package cz.upce.fei.dt.ui.components;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import cz.upce.fei.dt.backend.entities.Address;
import cz.upce.fei.dt.backend.entities.Contact;
import lombok.Getter;

public class ContactAccordion extends Div {

    private static class AddressLayout extends VerticalLayout {
        @Getter
        private Address address;
        private final Span street = new Span();
        private final Span city = new Span();
        private final Span state = new Span();

        public AddressLayout() {
            this.setSpacing(false);
            this.setPadding(false);
            this.add(street, city, state);
        }

        public void setAddress(Address address) {
            if (address == null)
                return;
            this.address = address;

            street.setText(address.getStreet() + " " + address.getHouseNumber());
            city.setText(address.getZipCode() + " " + address.getCity());
            state.setText(address.getState());
        }
    }

    @Getter
    private Contact contact;
    private final AddressLayout invoiceAddress = new AddressLayout();
    private final AddressLayout deliverAddress = new AddressLayout();
    private final AccordionPanel invoiceAddressPanel;
    private final AccordionPanel deliveryAddressPanel;
    private final AccordionPanel detailContactPanel;
    private final Span client = new Span();
    private final Span email = new Span();
    private final Span phone = new Span();
    private final Span ICO = new Span();
    private final Span DIC = new Span();


    public ContactAccordion() {
        Accordion accordion = new Accordion();

        invoiceAddressPanel = accordion.add("Fakturační adresa", invoiceAddress);
        invoiceAddressPanel.addThemeVariants(DetailsVariant.FILLED);
        invoiceAddressPanel.setVisible(false);

        deliveryAddressPanel = accordion.add("Doručovací adresa", deliverAddress);
        deliveryAddressPanel.addThemeVariants(DetailsVariant.FILLED);
        deliveryAddressPanel.setVisible(false);

        detailContactPanel = accordion.add("Kontaktní informace", createDetailContactLayout());
        detailContactPanel.addThemeVariants(DetailsVariant.FILLED);
        detailContactPanel.setVisible(false);

        add(accordion);
    }

    public void setContact(Contact contact) {
        this.contact = contact;

        if (contact == null) {
            invoiceAddressPanel.setVisible(false);
            deliveryAddressPanel.setVisible(false);
            detailContactPanel.setVisible(false);
            return;
        }

        invoiceAddressPanel.setVisible(true);
        invoiceAddress.setAddress(contact.getInvoiceAddress());

        deliveryAddressPanel.setVisible(contact.hasDeliveryAddress());
        deliverAddress.setAddress(contact.getDeliveryAddress());

        detailContactPanel.setVisible(true);
        setDetailContactPanel();
    }

    private VerticalLayout createDetailContactLayout() {
        VerticalLayout detailContactLayout = new VerticalLayout();
        detailContactLayout.setSpacing(false);
        detailContactLayout.setPadding(false);
        detailContactLayout.add(client, ICO, DIC, email, phone);
        return detailContactLayout;
    }

    private void setDetailContactPanel() {
        client.setText(contact.getClient());
        ICO.setText("IČO: " + contact.getICO());
        DIC.setVisible(contact.getDIC() != null && !contact.getDIC().isEmpty());
        DIC.setText("DIČ: " + contact.getDIC());
        email.setText(contact.getEmail());
        phone.setText(contact.getPhone());
    }

}
