package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import cz.upce.fei.dt.beckend.entities.Address;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressForm extends FormLayout implements IEditForm<Address>, HasValue<HasValue.ValueChangeEvent<Address>, Address> {
    private final Binder<Address> binder;
    private final TextField street;
    private final TextField houseNumber;
    private final TextField city;
    private final TextField zipCode;
    private final TextField state;
    private Address address;
    public AddressForm(String title) {
        addClassName("address-form");
        binder = new BeanValidationBinder<>(Address.class);

        street = new TextField("Ulice");
        binder.forField(street)
                .asRequired()
                .bind(Address::getStreet, Address::setStreet);

        houseNumber = new TextField("Číslo domu");
        binder.forField(houseNumber)
                .asRequired()
                .bind(Address::getHouseNumber, Address::setHouseNumber);

        city = new TextField("Město");
        binder.forField(city)
                .asRequired()
                .bind(Address::getCity, Address::setCity);

        zipCode = new TextField("PSČ");
        binder.forField(zipCode)
                .asRequired()
                .bind(Address::getZipCode, Address::setZipCode);

        state = new TextField("Stát");
        binder.forField(state)
                .asRequired()
                .bind(Address::getState, Address::setState);

        add(createDivider(title), street, houseNumber, city, zipCode, state);
    }

    private Span createDivider(String title) {
        Span divider = new Span(title + " ");
        divider.setClassName("address-form-divider");
        Icon icon = new Icon(VaadinIcon.ANGLE_DOWN);
        icon.setSize("0.7em");
        divider.add(icon);
        divider.addClickListener(click -> switchVisible());
        return divider;
    }

    private void switchVisible() {
        if (street.isVisible() || city.isVisible() || zipCode.isVisible() || state.isVisible()){
            setVisible(false);
        }else{
            setVisible(true);
        }
    }
    public void setVisible(boolean visible){
        street.setVisible(visible);
        houseNumber.setVisible(visible);
        city.setVisible(visible);
        zipCode.setVisible(visible);
        state.setVisible(visible);
    }

    @Override
    public void setValue(Address address) {
        this.address = address;
        binder.readBean(address);
    }

    @Override
    public Address getValue() {
        return address;
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<? super ValueChangeEvent<Address>> valueChangeListener) {
        return null;
    }

    @Override
    public void validate() throws ValidationException {
        binder.writeBean(address);
    }

    @Override
    public void expand(boolean expended) {

    }

    public boolean isEmpty(){
        return street.isEmpty() && houseNumber.isEmpty() && city.isEmpty() && zipCode.isEmpty() && state.isEmpty();
    }

    @Override
    public void setReadOnly(boolean b) {

    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public void setRequiredIndicatorVisible(boolean b) {

    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return false;
    }
}
