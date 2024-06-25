package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import cz.upce.fei.dt.beckend.entities.Role;
import cz.upce.fei.dt.beckend.entities.User;

import java.util.EnumSet;

public class UserForm extends FormLayout implements IEditForm<User> {
    private final Binder<User> binder;
    public final EmailField emailField = new EmailField("Email");
    private User user;

    public UserForm() {
        addClassName("user-form");
        binder = new BeanValidationBinder<>(User.class);

        final TextField firstName = new TextField("Křestní jméno");
        binder.forField(firstName)
                .asRequired()
                .bind(User::getFirstName, User::setFirstName);

        final TextField lastName = new TextField("Příjmení");
        binder.forField(lastName)
                .asRequired()
                .bind(User::getLastName, User::setLastName);

        binder.forField(emailField)
                .withValidator(new EmailValidator("Toto není validní email."))
                .asRequired()
                .bind(User::getEmail, User::setEmail);

        final ComboBox<Role> role = new ComboBox<>("Oprávnění");
        binder.forField(role)
                .asRequired()
                .bind(User::getRole, User::setRole);
        role.setItems(EnumSet.allOf(Role.class));
        role.setItemLabelGenerator(Role::name);

        add(firstName, lastName, emailField, role);
    }

    public void setUser(User user) {
        setValue(user);
    }

    @Override
    public User getValue() {
        return user;
    }

    @Override
    public void setValue(User user) {
        this.user = user;
        binder.readBean(user);
    }

    @Override
    public void validate() throws ValidationException {
        binder.writeBean(user);
    }

    @Override
    public void expand(boolean expended) {

    }

}
