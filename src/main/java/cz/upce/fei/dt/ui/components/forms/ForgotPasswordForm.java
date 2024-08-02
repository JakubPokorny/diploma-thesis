package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.server.VaadinSession;
import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.exceptions.CustomErrorHandler;
import cz.upce.fei.dt.beckend.services.UserService;
import cz.upce.fei.dt.ui.views.DashboardView;

public class ForgotPasswordForm extends FormLayout {
    private final Binder<User> binder = new BeanValidationBinder<>(User.class);
    private final User user;

    public ForgotPasswordForm(UserService userService) {
        setClassName("password-form");
        setResponsiveSteps(new ResponsiveStep("0", 1));
        VaadinSession.getCurrent().setErrorHandler(new CustomErrorHandler());

        user = new User();
        binder.readBean(user);
        final EmailField emailField = new EmailField("Email");
        binder.forField(emailField)
                .asRequired()
                .bind(User::getEmail, User::setEmail);

        final Button sendBTN = new Button("Odeslat", event -> {
            try {
                binder.writeBean(user);
                userService.generateResetToken(user);
                event.getSource().getElement().setEnabled(false);
            } catch (ValidationException validationException) {
                Notification.show("Neplatný email.").addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        final Button backBTN = new Button(
                "Zpět",
                event -> event.getSource().getUI().ifPresent(ui -> ui.navigate(DashboardView.class))
        );
        backBTN.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        add(emailField, sendBTN, backBTN);
    }
}
