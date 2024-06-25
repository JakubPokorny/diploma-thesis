package cz.upce.fei.dt.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import cz.upce.fei.dt.beckend.entities.Address;
import cz.upce.fei.dt.beckend.entities.Contact;
import cz.upce.fei.dt.beckend.entities.Contact_;
import cz.upce.fei.dt.beckend.services.ContactService;
import cz.upce.fei.dt.beckend.services.filters.ContactFilter;
import cz.upce.fei.dt.ui.components.filters.FilterFields;
import cz.upce.fei.dt.ui.components.GridFormLayout;
import cz.upce.fei.dt.ui.components.filters.FromToLocalDateFilterFields;
import cz.upce.fei.dt.ui.components.forms.ContactForm;
import cz.upce.fei.dt.ui.components.forms.events.DeleteEvent;
import cz.upce.fei.dt.ui.components.forms.events.SaveEvent;
import jakarta.annotation.security.PermitAll;

@Route(value = "contacts", layout = MainLayout.class)
@RouteAlias(value = "kontakty", layout = MainLayout.class)
@PageTitle("Contacts")
@PermitAll
public class ContactsView extends VerticalLayout {
    private final ContactService contactService;
    private final GridFormLayout<ContactForm, Contact> gridFormLayout;
    private final Grid<Contact> grid;
    private final ContactFilter contactFilter = new ContactFilter();
    private ConfigurableFilterDataProvider<Contact, Void, ContactFilter> configurableFilterDataProvider;

    public ContactsView(ContactService contactService) {

        this.contactService = contactService;

        ContactForm form = new ContactForm();
        grid = new Grid<>(Contact.class, false);
        gridFormLayout = new GridFormLayout<>(form, grid);
        MainLayout.setPageTitle("Kontakty", ContactsView.class);
        setSizeFull();

        configureGrid();
        configureForm();
        configureActions();
        configureFilters();

        add(gridFormLayout);
    }

    //region configures: grid, form, actions, filters, events
    private void configureFilters() {
    }

    private void configureActions() {
        gridFormLayout.addButton.addClickListener(_ -> gridFormLayout.addNewValue(new Contact()));
    }

    private void configureForm() {
        ComponentUtil.addListener(gridFormLayout, SaveEvent.class, this::saveContact);
        ComponentUtil.addListener(gridFormLayout, DeleteEvent.class, this::deleteContact);
    }

    //region events: save, delete
    private void saveContact(SaveEvent saveEvent) {
        try {
            Contact contact = (Contact) saveEvent.getValue();
            contactService.saveContact(contact);
            updateGrid();
            gridFormLayout.closeFormLayout();
            Notification.show("Kontakt " + contact.getClient() + " uložen.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception exception) {
            Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteContact(DeleteEvent deleteEvent) {
        try {
            Contact contact = (Contact) deleteEvent.getValue();
            contactService.deleteContact(contact);
            updateGrid();
            gridFormLayout.closeFormLayout();
            Notification.show("Kontakt " + contact.getClient() + " odstraněn.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception exception) {
            Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    //endregion
    private void configureGrid() {
        grid.setClassName("grid-content");
        grid.setSizeFull();

        DataProvider<Contact, ContactFilter> dataProvider = DataProvider.fromFilteringCallbacks(
                contactService::fetchFromBackEnd,
                contactService::sizeInBackEnd
        );

        configurableFilterDataProvider = dataProvider.withConfigurableFilter();
        configurableFilterDataProvider.setFilter(contactFilter);

        Grid.Column<Contact> icoColumn = grid.addColumn(Contact::getICO).setHeader("IČO").setKey(Contact_.ICO.getName()).setWidth("150px");
        Grid.Column<Contact> dicColumn = grid.addColumn(Contact::getDIC).setHeader("DIČ").setKey(Contact_.DIC.getName()).setWidth("150px");
        Grid.Column<Contact> clientColumn = grid.addColumn(Contact::getClient).setHeader("Klient").setKey(Contact_.CLIENT).setWidth("150px");
        Grid.Column<Contact> emailColumn = grid.addColumn(Contact::getEmail).setHeader("Email").setKey(Contact_.EMAIL).setWidth("150px");
        Grid.Column<Contact> phoneColumn = grid.addColumn(Contact::getPhone).setHeader("Telefon").setKey(Contact_.PHONE).setWidth("150px");
        Grid.Column<Contact> invoiceAddressColumn = grid.addComponentColumn(contact -> createAddressColumn(contact.getInvoiceAddress())).setHeader("Fakturační adresa").setWidth("150px");
        Grid.Column<Contact> deliveryAddressColumn = grid.addComponentColumn(contact -> createAddressColumn(contact.getDeliveryAddress())).setHeader("Doručovací adresa").setWidth("150px");
        Grid.Column<Contact> createdColumn = grid.addColumn(new LocalDateTimeRenderer<>(Contact::getCreated, "H:mm d. M. yyyy")).setHeader("Vytvořeno").setKey(Contact_.CREATED).setWidth("150px");
        Grid.Column<Contact> updatedColumn = grid.addColumn(new LocalDateTimeRenderer<>(Contact::getUpdated, "H:mm d. M. yyyy")).setHeader("Upraveno").setKey(Contact_.UPDATED).setWidth("150px");

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(icoColumn).setComponent(FilterFields.createTextFieldFilter("ičo", contactFilter::setIcoFilter, configurableFilterDataProvider));
        headerRow.getCell(dicColumn).setComponent(FilterFields.createTextFieldFilter("ičo", contactFilter::setDicFilter, configurableFilterDataProvider));
        headerRow.getCell(clientColumn).setComponent(FilterFields.createTextFieldFilter("klient", contactFilter::setClientFilter, configurableFilterDataProvider));
        headerRow.getCell(emailColumn).setComponent(FilterFields.createTextFieldFilter("email", contactFilter::setEmailFilter, configurableFilterDataProvider));
        headerRow.getCell(phoneColumn).setComponent(FilterFields.createTextFieldFilter("tel. číslo", contactFilter::setPhoneFilter, configurableFilterDataProvider));
        headerRow.getCell(invoiceAddressColumn).setComponent(FilterFields.createTextFieldFilter("fakturační adresa", contactFilter::setInvoiceAddressFilter, configurableFilterDataProvider));
        headerRow.getCell(deliveryAddressColumn).setComponent(FilterFields.createTextFieldFilter("doručovací adresa", contactFilter::setDeliveryAddressFilter, configurableFilterDataProvider));
        headerRow.getCell(createdColumn).setComponent(new FromToLocalDateFilterFields(contactFilter::setFromCreatedFilter, contactFilter::setToCreatedFilter, configurableFilterDataProvider).getFilterHeaderLayout());
        headerRow.getCell(updatedColumn).setComponent(new FromToLocalDateFilterFields(contactFilter::setFromUpdatedFilter, contactFilter::setToUpdatedFilter, configurableFilterDataProvider).getFilterHeaderLayout());

        grid.asSingleSelect().addValueChangeListener(e -> gridFormLayout.showFormLayout(e.getValue()));
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        grid.setSortableColumns(Contact_.ICO.getName(), Contact_.DIC.getName(), Contact_.CLIENT, Contact_.EMAIL, Contact_.PHONE, Contact_.CREATED, Contact_.UPDATED);

        dicColumn.setVisible(false);
        createdColumn.setVisible(false);
        updatedColumn.setVisible(false);
        gridFormLayout.showHideMenu.addColumnToggleItem("IČO", icoColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("DIČ", dicColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Klient", clientColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Email", emailColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Telefon", phoneColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Fakturační adresa", invoiceAddressColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Doručovací adresa", deliveryAddressColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Vytvořeno", createdColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Upraveno", updatedColumn);

        updateGrid();
    }

    private Component createAddressColumn(Address address) {
        if (address == null)
            return null;
        Span streetAndHouseNumber = new Span(String.format("%s %s,", address.getStreet(), address.getHouseNumber()));
        Span cityAndZipCode = new Span(String.format("%s %s,", address.getCity(), address.getZipCode()));
        Span state = new Span(String.format("%s", address.getState()));
        VerticalLayout layout = new VerticalLayout(streetAndHouseNumber, cityAndZipCode, state);
        layout.setSpacing(false);
        layout.setPadding(false);
        return layout;
    }

    //endregion
    private void updateGrid() {
        grid.setItems(configurableFilterDataProvider);
    }
}
