package cz.upce.fei.dt.ui.views.customerContacts;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import cz.upce.fei.dt.beckend.entities.Address;
import cz.upce.fei.dt.beckend.entities.Contact;
import cz.upce.fei.dt.beckend.services.AddressService;
import cz.upce.fei.dt.beckend.services.ContactService;
import cz.upce.fei.dt.ui.components.GridFormLayout;
import cz.upce.fei.dt.ui.components.forms.ContactForm;
import cz.upce.fei.dt.ui.components.forms.events.DeleteEvent;
import cz.upce.fei.dt.ui.components.forms.events.SaveEvent;
import cz.upce.fei.dt.ui.views.MainLayout;
import jakarta.annotation.security.PermitAll;

@Route(value = "contacts", layout = MainLayout.class)
@RouteAlias(value = "kontakty", layout = MainLayout.class)
@PageTitle("Contacts")
@PermitAll
public class ContactsView extends VerticalLayout {
    private final ContactService contactService;
    private final AddressService addressService;
    private final GridFormLayout<ContactForm, Contact> gridFormLayout;
    private final ContactForm form;
    private final Grid<Contact> grid;
    public ContactsView(ContactService contactService, AddressService addressService) {
        this.contactService = contactService;
        this.addressService = addressService;
        MainLayout.setPageTitle("Kontakty", ContactsView.class);
        setSizeFull();

        //form = new ContactForm();
        form = new ContactForm();
        grid = new Grid<>(Contact.class, false);
        gridFormLayout = new GridFormLayout<>(form, grid);

        configureGrid();
        configureForm();

        Button addContact = new Button("Přidat Kontakt");
        addContact.addClickListener(event -> gridFormLayout.addNewValue(new Contact()));
        gridFormLayout.getActionsLayout().add(addContact);

        add(gridFormLayout);
    }
    private void configureForm() {
        gridFormLayout.addSaveListener(this::saveContact);
        gridFormLayout.addDeleteListener(this::deleteContact);
    }

    private void saveContact(SaveEvent saveEvent) {
        contactService.saveContact((Contact) saveEvent.getValue());
        grid.setItems(contactService.getAll());
        gridFormLayout.closeFormLayout();
    }

    private void deleteContact(DeleteEvent deleteEvent) {
        contactService.deleteContact((Contact) deleteEvent.getValue());
        grid.setItems(contactService.getAll());
        gridFormLayout.closeFormLayout();
    }

    private void configureGrid() {
        grid.setClassName("grid-content");
        grid.setSizeFull();

        grid.addColumn(Contact::getICO).setHeader("IČO");
        grid.addColumn(Contact::getName).setHeader("Název");
        grid.addColumn(Contact::getEmail).setHeader("Email");
        grid.addColumn(Contact::getPhone).setHeader("Telefon");
        grid.addColumn(contact -> ContactsView.formatAddress(contact.getInvoiceAddress())).setHeader("Fakturační adresa");
        grid.addColumn(contact -> ContactsView.formatAddress(contact.getDeliveryAddress())).setHeader("Doručovácí adresa");
        grid.addColumn(new LocalDateTimeRenderer<>(Contact::getUpdated, "H:m d. M. yyyy")).setHeader("Poslední úprava");

        grid.asSingleSelect().addValueChangeListener(e-> gridFormLayout.showFormLayout(e.getValue()));
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        grid.setItems(contactService.getAll());
    }

    private static String formatAddress(Address address) {
        return address != null ? String.format("%s %s, %s %s, %s", address.getStreet(), address.getHouseNumber(),
                address.getCity(),address.getZipCode(), address.getState()) : "";
    }
}
