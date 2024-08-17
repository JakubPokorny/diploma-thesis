package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import cz.upce.fei.dt.backend.entities.Address;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressForm extends FormLayout implements IEditForm<Address>, HasValue<HasValue.ValueChangeEvent<Address>, Address> {
    private final Binder<Address> binder = new BeanValidationBinder<>(Address.class);
    private Address address;
    private final TextField street = new TextField("Ulice");
    private final TextField houseNumber = new TextField("Číslo domu");
    private final TextField city = new TextField("Město");
    private final TextField zipCode = new TextField("PSČ");
    private final TextField state = new TextField("Stát");

    public AddressForm() {
        addClassName("address-form");

        setupStreet();
        setupHouseNumber();
        setupCity();
        setupZipCode();
        setupState();

        this.setResponsiveSteps(
                new ResponsiveStep("0", 1),
                new ResponsiveStep("300px", 3)
        );

        this.setColspan(street, 2);
        this.setColspan(city, 2);
        this.setColspan(state, 3);

        add(street, houseNumber, city, zipCode, state);
    }

    //region Setups
    private void setupState() {
        binder.forField(state)
                .asRequired()
                .bind(Address::getState, Address::setState);
    }

    private void setupZipCode() {
        binder.forField(zipCode)
                .asRequired()
                .bind(Address::getZipCode, Address::setZipCode);
    }

    private void setupCity() {
        binder.forField(city)
                .asRequired()
                .bind(Address::getCity, Address::setCity);
    }

    private void setupHouseNumber() {
        binder.forField(houseNumber)
                .asRequired()
                .bind(Address::getHouseNumber, Address::setHouseNumber);
    }

    private void setupStreet() {
        binder.forField(street)
                .asRequired()
                .bind(Address::getStreet, Address::setStreet);
    }

    //endregion

    //region IEditForm, HasValue
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

    public boolean isEmpty() {
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

    //endregion
}
