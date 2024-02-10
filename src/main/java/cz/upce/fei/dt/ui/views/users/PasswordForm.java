package cz.upce.fei.dt.ui.views.users;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.services.UserService;
import cz.upce.fei.dt.ui.views.LoginView;
import cz.upce.fei.dt.ui.views.dashboard.DashboardView;
import org.springframework.beans.factory.annotation.Autowired;

public class PasswordForm extends FormLayout {
    private final Binder<User> binder = new Binder<>(User.class);
    private final UserService userService;
    public PasswordForm(User user, UserService userService) {
        this.userService = userService;

        setClassName("password-form");
        setResponsiveSteps(new ResponsiveStep("0", 1));

        binder.readBean(user);
        final PasswordField password = new PasswordField("Heslo");
        binder.forField(password)
                .asRequired()
                .withValidator(pass -> pass.length() >= 8, "Minimálně 8 znaků.")
                .withValidator(pass -> pass.chars().anyMatch(Character::isDigit), "Chybí alespoň 1 číslo.")
                .withValidator(pass -> pass.chars().anyMatch(Character::isUpperCase), "Chybí alespoň 1 velké písmeno.")
                .withValidator(pass -> pass.chars().anyMatch(Character::isLowerCase), "Chybí alespoň 1 malé písmeno.")
                .bind(User::getPassword, User::setPassword);

        final PasswordField repeat = new PasswordField("Znovu heslo");
        binder.forField(repeat)
                .asRequired()
                .withValidator(pass -> pass.equals(password.getValue()), "Hesla se neshodují.")
                .bind(User::getPassword, User::setPassword);

        final Button save =  new Button("Uložit");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> validateAndSave(user));
        save.addClickShortcut(Key.ENTER);

        final Button back =  new Button("Zpět");
        back.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        back.addClickListener(e -> {
            back.getUI().ifPresent(ui -> ui.navigate(DashboardView.class));
        });

        add(password, repeat, save, back);

    }

    private void validateAndSave(User user) {
        try {
            binder.writeBean(user);
            userService.changePassword(user);
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }
}
