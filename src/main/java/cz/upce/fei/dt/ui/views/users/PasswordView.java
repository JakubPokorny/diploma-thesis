package cz.upce.fei.dt.ui.views.users;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.repositories.UserRepository;
import cz.upce.fei.dt.beckend.services.UserService;
import cz.upce.fei.dt.ui.views.dashboard.DashboardView;
import cz.upce.fei.dt.ui.views.errors.CustomRouteNotFoundError;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.AccessDeniedException;
import java.text.Normalizer;

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
