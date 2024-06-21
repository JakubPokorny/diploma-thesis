package cz.upce.fei.dt.ui.views.users;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.exceptions.AuthenticationException;
import cz.upce.fei.dt.beckend.services.UserService;
import cz.upce.fei.dt.ui.views.MainLayout;
import jakarta.annotation.security.PermitAll;

@Route(value = "profile", layout = MainLayout.class)
@RouteAlias(value = "profil", layout = MainLayout.class)
@PageTitle("Profil")
@PermitAll
public class ProfileView extends VerticalLayout {

    public ProfileView(AuthenticationContext authenticationContext, UserService userService) {
        User user = authenticationContext.getAuthenticatedUser(User.class)
                .orElseThrow(() -> new AuthenticationException("Neznámý uživatel. Přihlašte se prosím."));

        final UserForm userForm = setupUserForm(user, userService);
        final PasswordForm passwordForm = setupPasswordForm(user, userService);

        add(new H2("Profil"), userForm, new H2("Změna hesla"), passwordForm);
    }

    private PasswordForm setupPasswordForm(User user, UserService userService){
        PasswordForm passwordForm = new PasswordForm(user, userService);
        passwordForm.removeClassName("password-form");
        passwordForm.back.setVisible(false);
        return passwordForm;
    }

    private UserForm setupUserForm(User user, UserService userService) {
        UserForm userForm = new UserForm();
        userForm.setUser(user);
        userForm.emailField.setReadOnly(true);

        Button saveButton = new Button("Uložit");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        userForm.setColspan(saveButton, 2);
        saveButton.addClickListener(_ -> {
            try {
                userForm.validate();
                User toSave = userForm.getValue();
                userService.saveUser(toSave);
                Notification.show("Uživatel "+ toSave.getFullName()+" uložen.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }catch (ValidationException exception){
                Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        userForm.add(saveButton);
        return userForm;
    }
}
