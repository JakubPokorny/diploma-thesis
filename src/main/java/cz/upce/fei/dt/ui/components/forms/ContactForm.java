package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.shared.Registration;
import cz.upce.fei.dt.beckend.entities.Address;
import cz.upce.fei.dt.beckend.entities.Contact;
import cz.upce.fei.dt.beckend.orms.AresResponse;
import cz.upce.fei.dt.beckend.services.AresService;

public class ContactForm extends FormLayout implements IEditForm<Contact> {
    private final Binder<Contact> customerBinder;
    //private final Binder<Address> invoiceAddressBinder;
    //private final Binder<Address> deliveryAddressBinder;
    private final AddressForm invoiceAddressForm;
    private final AddressForm deliveryAddressForm;
    private Contact contact;
    private Address invoiceAddress;
    private Address deliveryAddress;

    private final TextField ico;
    private final TextField dic;
    private final TextField company;
    public ContactForm() {
        setClassName("edit-form");
        customerBinder = new BeanValidationBinder<>(Contact.class);




        ico = new TextField("IČO");
        ico.setSuffixComponent(VaadinIcon.SEARCH.create());
        ico.getSuffixComponent().getElement().addEventListener("click", event->searchAres(ico.getValue()));
        ico.getSuffixComponent().setId("pointer");
        ico.setHelperText("IČO má 8 číslic bez mezer.");
        ico.setMinLength(8);
        ico.setMaxLength(8);
        ico.setAllowedCharPattern("[0-9]");
        //ico.addValueChangeListener(event -> searchAres(event.getValue()));
        customerBinder.forField(ico)
                .bind(Contact::getICO, Contact::setICO);

        dic = new TextField("DIČ");
        customerBinder.forField(dic)
                .bind(Contact::getDIC, Contact::setDIC);

        company = new TextField("Společnost");
        customerBinder.forField(company)
                .asRequired()
                .bind(Contact::getName, Contact::setName);

        final EmailField email = new EmailField("Email");
        customerBinder.forField(email)
                .withValidator(new EmailValidator("Toto není validní email."))
                .asRequired()
                .bind(Contact::getEmail, Contact::setEmail);

        final TextField phone = new TextField("Telefon");
        phone.setMinLength(9);
        phone.setPattern("([+][0-9]{2,3})?[0-9]{9}");
        this.setColspan(phone, 2);
        customerBinder.forField(phone)
                .asRequired()
                .bind(Contact::getPhone, Contact::setPhone);


        invoiceAddressForm = new AddressForm("Fakturační adresa");
        //invoiceAddressBinder = invoiceAddressForm.getBinder();

        customerBinder.forField(invoiceAddressForm)
                .asRequired()
                .bind(Contact::getInvoiceAddress, Contact::setInvoiceAddress);

        deliveryAddressForm = new AddressForm("Doručovací adresa");
        //deliveryAddressBinder = deliveryAddressForm.getBinder();
        deliveryAddressForm.setVisible(false);

        customerBinder.forField(deliveryAddressForm)
                .bind(Contact::getDeliveryAddress, Contact::setDeliveryAddress);

        add(ico, dic, company, email, phone, invoiceAddressForm, deliveryAddressForm);

    }

    private void searchAres(String value) {
        if (value.length() == 8){
            try {
                AresResponse  aresResponse = AresService.searchByICO(value);

                contact.setICO(aresResponse.getIco());

                if(aresResponse.getDic()!=null){
                    contact.setDIC(aresResponse.getDic());
                }
                contact.setName(aresResponse.getObchodniJmeno());

                invoiceAddressForm.setVisible(true);

                Address address = new Address();
                address.setStreet(aresResponse.getSidlo().getNazevUlice());
                address.setHouseNumber(aresResponse.getSidlo().getCisloDomovni()+"");
                address.setCity(aresResponse.getSidlo().getNazevObce());
                address.setZipCode(aresResponse.getSidlo().getPsc()+"");
                address.setState(aresResponse.getSidlo().getNazevStatu());

                contact.setInvoiceAddress(address);
                customerBinder.readBean(contact);
                Notification.show(aresResponse.toString()).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }catch (Exception exception){
                Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
    }

    @Override
    public Contact getValue() {
        return contact;
    }

    @Override
    public void setValue(Contact contact) {
        this.contact = contact;

        if (contact == null){
            invoiceAddressForm.setValue(null);
            deliveryAddressForm.setValue(null);
        }else{
            if (contact.hasDeliveryAddress()){
                deliveryAddressForm.setVisible(true);
            }else{
                contact.setDeliveryAddress(new Address());
            }
        }
        customerBinder.readBean(contact);
    }

    @Override
    public void validate() throws ValidationException {
        invoiceAddressForm.validate();
        if (!deliveryAddressForm.isEmpty()){
            deliveryAddressForm.validate();
        }else{
            deliveryAddressForm.setAddress(new Address());
            //customerBinder.readBean(contact);
        }
        customerBinder.writeBean(contact);
    }

    @Override
    public void expand(boolean expended) {
        deliveryAddressForm.setVisible(expended);
    }
}
