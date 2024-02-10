package cz.upce.fei.dt.ui.views.users;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.shared.Registration;
import cz.upce.fei.dt.beckend.entities.Role;
import cz.upce.fei.dt.beckend.entities.User;

import java.util.EnumSet;

public class UserForm extends FormLayout {
    private final Binder<User> binder = new BeanValidationBinder<>(User.class);
    private User user;

    public UserForm() {
        addClassName("user-form");

        final TextField firstName = new TextField("Křestní jméno");
        binder.forField(firstName)
                .asRequired()
                .bind(User::getFirstName, User::setFirstName);

        final TextField lastName = new TextField("Příjmení");
        binder.forField(lastName)
                .asRequired()
                .bind(User::getLastName, User::setLastName);

        final EmailField email = new EmailField("Email");
        binder.forField(email)
                .withValidator(new EmailValidator("Not an email"))
                .asRequired()
                .bind(User::getEmail, User::setEmail);

        final ComboBox<Role> role = new ComboBox<>("Oprávnění");
        binder.forField(role)
                .asRequired()
                .bind(User::getRoles, User::setRoles);
        role.setItems(EnumSet.allOf(Role.class));
        role.setItemLabelGenerator(Role::name);

//        binder.bindInstanceFields(this);


        add(firstName,lastName, email, role, createButtonLayout());
    }

    private Component createButtonLayout() {
        final Button save =  new Button("Uložit");
        final Button delete =  new Button("Odstranit");
        final Button cancel =  new Button("Zrušit");

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, user)));
        cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));

        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        return new HorizontalLayout(save, delete, cancel);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(user);
            fireEvent(new SaveEvent(this, user));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    public void setUser(User user){
        this.user = user;
        binder.readBean(user);
    }

    //Event
    public static abstract class UserFormEvent extends ComponentEvent<UserForm> {
        private final User user;

        protected UserFormEvent(UserForm source, User user) {
            super(source, false);
            this.user = user;
        }
        public User getUser() {
            return user;
        }
    }
    public static class SaveEvent extends UserFormEvent {
        SaveEvent(UserForm source, User user) {
            super(source, user);
        }
    }
    public static class DeleteEvent extends UserFormEvent {
        DeleteEvent(UserForm source, User user) {
            super(source, user);
        }
    }
    public static class CloseEvent extends UserFormEvent {
        CloseEvent(UserForm source) {
            super(source, null);
        }
    }
    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }
    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }
    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}
