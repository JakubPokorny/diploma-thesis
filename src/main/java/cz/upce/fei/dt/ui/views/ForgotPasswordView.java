package cz.upce.fei.dt.ui.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import cz.upce.fei.dt.beckend.services.UserService;
import cz.upce.fei.dt.ui.components.forms.ForgotPasswordForm;

@AnonymousAllowed
@Route(value = "forgotPassword")
@RouteAlias(value = "zapomenuteHeslo")
@PageTitle(value = "Zapomenuté heslo")
public class ForgotPasswordView extends VerticalLayout {

    public ForgotPasswordView(UserService userService) {
        setHeightFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(new H1("Zapomenuté heslo"), new ForgotPasswordForm(userService));
    }
}
