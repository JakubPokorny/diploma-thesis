package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import cz.upce.fei.dt.beckend.entities.Address;
import cz.upce.fei.dt.beckend.entities.Contact;
import cz.upce.fei.dt.beckend.dto.AresResponse;
import cz.upce.fei.dt.beckend.services.AresService;

public class ContactForm extends FormLayout implements IEditForm<Contact> {
    private final Binder<Contact> customerBinder = new BeanValidationBinder<>(Contact.class);
    private Contact contact;
    private final AddressForm invoiceAddressForm = new AddressForm();
    Details invoiceAddressDetail = new Details("Fakturační adresa", invoiceAddressForm);
    private final AddressForm deliveryAddressForm = new AddressForm();
    Details deliveryAddressDetail = new Details("Doručovací adresa", deliveryAddressForm);
    private final TextField ico = new TextField("IČO");
    private final TextField dic = new TextField("DIČ");
    private final TextField client = new TextField("Společnost");
    private final EmailField email = new EmailField("Email");
    private final TextField phone = new TextField("Telefon");

    public ContactForm() {
        setClassName("edit-form");

        setupICO();
        setupDIC();
        setupClient();
        setupEmail();
        setupPhone();
        setupInvoiceAddressForm();
        setupDeliveryAddressForm();

        add(ico, dic, client, email, phone, invoiceAddressDetail, deliveryAddressDetail);
    }

    //region Setups

    private void setupDeliveryAddressForm() {
        customerBinder.forField(deliveryAddressForm)
                .bind(Contact::getDeliveryAddress, Contact::setDeliveryAddress);
    }

    private void setupInvoiceAddressForm() {
        invoiceAddressDetail.setOpened(true);
        customerBinder.forField(invoiceAddressForm)
                .asRequired()
                .bind(Contact::getInvoiceAddress, Contact::setInvoiceAddress);
    }

    private void setupPhone() {
        phone.setMinLength(9);
        phone.setPattern("([+][0-9]{2,3})?[0-9]{9}");
        this.setColspan(phone, 2);
        customerBinder.forField(phone)
                .asRequired()
                .bind(Contact::getPhone, Contact::setPhone);
    }

    private void setupEmail() {
        customerBinder.forField(email)
                .withValidator(new EmailValidator("Nevalidní email."))
                .asRequired()
                .bind(Contact::getEmail, Contact::setEmail);
    }

    private void setupClient() {
        customerBinder.forField(client)
                .asRequired()
                .bind(Contact::getClient, Contact::setClient);
    }

    private void setupDIC() {
        dic.setHelperText("Příklad DIČ: CZ27767580");
        customerBinder.forField(dic)
                .bind(Contact::getDIC, Contact::setDIC);
    }

    private void setupICO() {
        ico.setSuffixComponent(VaadinIcon.SEARCH.create());
        ico.getSuffixComponent().getElement().addEventListener("click", _ -> searchAres(ico.getValue()));
        ico.getSuffixComponent().setId("pointer");
        ico.setHelperText("IČO má 8 číslic bez mezer.");
        ico.setMinLength(8);
        ico.setMaxLength(8);
        ico.setAllowedCharPattern("[0-9]");
        customerBinder.forField(ico)
                .bind(Contact::getICO, Contact::setICO);
    }


    //endregion

    private void searchAres(String value) {
        if (value.length() == 8) {
            try {
                AresResponse aresResponse = AresService.searchByICO(value);

                contact.setICO(aresResponse.getIco());

                if (aresResponse.getDic() != null) {
                    contact.setDIC(aresResponse.getDic());
                }
                contact.setClient(aresResponse.getObchodniJmeno());

                invoiceAddressForm.setVisible(true);

                Address address = new Address();
                address.setStreet(aresResponse.getSidlo().getNazevUlice());
                address.setHouseNumber(aresResponse.getSidlo().getCisloDomovni() + "");
                address.setCity(aresResponse.getSidlo().getNazevObce());
                address.setZipCode(aresResponse.getSidlo().getPsc() + "");
                address.setState(aresResponse.getSidlo().getNazevStatu());

                contact.setInvoiceAddress(address);
                customerBinder.readBean(contact);
                Notification.show(aresResponse.toString()).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception exception) {
                Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
    }

    //region IEditForm
    @Override
    public Contact getValue() {
        return contact;
    }

    @Override
    public void setValue(Contact contact) {
        this.contact = contact;

        if (contact == null) {
            invoiceAddressForm.setValue(null);
            deliveryAddressForm.setValue(null);
        } else {
            if (contact.hasDeliveryAddress()) {
                deliveryAddressDetail.setOpened(true);
            } else {
                contact.setDeliveryAddress(new Address());
                deliveryAddressDetail.setOpened(false);
            }
        }
        customerBinder.readBean(contact);
    }

    @Override
    public void validate() throws ValidationException {
        invoiceAddressForm.validate();
        if (!deliveryAddressForm.isEmpty()) {
            deliveryAddressForm.validate();
        } else {
            deliveryAddressForm.setAddress(new Address());
        }
        customerBinder.writeBean(contact);
    }

    @Override
    public void expand(boolean expended) {
        if (expended) {
            invoiceAddressForm.setVisible(true);
            deliveryAddressForm.setVisible(true);
        } else {
            invoiceAddressForm.setVisible(true);
            deliveryAddressForm.setVisible(false);
        }
    }
    //endregion
}
