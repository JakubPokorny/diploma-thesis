package cz.upce.fei.dt.ui.views.users;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import cz.upce.fei.dt.beckend.repositories.UserRepository;
import cz.upce.fei.dt.beckend.services.UserService;

@AnonymousAllowed
@Route(value = "password")
@PageTitle("Password | DT CRM")
public class PasswordView  extends VerticalLayout implements HasUrlParameter<String>{
    private final UserRepository userRepository;
    private final UserService userService;
    public PasswordView(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;

        setHeightFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        H1 title  = new H1("Nastavení Hesla");
        add(title);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String token) {
        var user = userRepository.findByResetToken(token).orElseThrow(NotFoundException::new);
        add(new PasswordForm(user, userService));
    }
}
