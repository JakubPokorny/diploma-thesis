package cz.upce.fei.dt.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.upce.fei.dt.backend.entities.Role;
import cz.upce.fei.dt.backend.entities.User;
import cz.upce.fei.dt.backend.exceptions.AuthenticationException;
import cz.upce.fei.dt.backend.services.UserService;
import cz.upce.fei.dt.ui.components.forms.ChangePasswordForm;
import cz.upce.fei.dt.ui.components.forms.UserForm;
import jakarta.annotation.security.PermitAll;
import org.apache.commons.lang3.SerializationUtils;

@Route(value = "profile", layout = MainLayout.class)
@RouteAlias(value = "profil", layout = MainLayout.class)
@PageTitle("Profil")
@PermitAll
public class ProfileView extends VerticalLayout {
    private final AuthenticationContext authenticationContext;
    private final UserService userService;

    public ProfileView(AuthenticationContext authenticationContext, UserService userService) {
        this.authenticationContext = authenticationContext;
        this.userService = userService;
        User authUser = authenticationContext.getAuthenticatedUser(User.class)
                .orElseThrow(() -> new AuthenticationException("Neznámý uživatel. Přihlašte se prosím."));
        User user = SerializationUtils.clone(authUser);

        this.setClassName("profile-view");

        final UserForm userForm = setupUserForm(user, userService);
        final ChangePasswordForm changePasswordForm = setupPasswordForm(user);

        add(new H2("Profil"), userForm, new H2("Změna hesla"), changePasswordForm);
    }

    private ChangePasswordForm setupPasswordForm(User user) {
        ChangePasswordForm changePasswordForm = new ChangePasswordForm(user, userService, authenticationContext);
        changePasswordForm.removeClassName("password-form");
        changePasswordForm.back.setVisible(false);
        return changePasswordForm;
    }

    private UserForm setupUserForm(User user, UserService userService) {
        UserForm userForm = new UserForm();
        userForm.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        userForm.setUser(user);
        userForm.emailField.setReadOnly(true);
        userForm.role.setReadOnly(!user.hasRole(Role.ADMIN));

        Button saveButton = new Button("Uložit a odhlásit");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(_ -> {
            try {
                userForm.validate();
                User toSave = userForm.getValue();
                userService.saveUser(toSave);
                authenticationContext.logout();
            } catch (ValidationException exception) {
                Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        userForm.add(saveButton);
        return userForm;
    }
}
