package cz.upce.fei.dt.ui.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.upce.fei.dt.beckend.services.UserService;
import cz.upce.fei.dt.ui.components.forms.ChangePasswordForm;

@AnonymousAllowed
@Route(value = "changePassword")
@RouteAlias(value = "zmenaHesla")
@PageTitle("Nastavení hesla")
public class ChangePasswordView extends VerticalLayout implements HasUrlParameter<String> {
    private final UserService userService;
    private final AuthenticationContext authenticationContext;

    public ChangePasswordView(UserService userService, AuthenticationContext authenticationContext) {
        this.userService = userService;
        this.authenticationContext = authenticationContext;

        setHeightFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        H1 title = new H1("Nastavení hesla");
        add(title);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String token) {
        add(new ChangePasswordForm(userService.findByResetToken(token), userService, authenticationContext));
    }
}
