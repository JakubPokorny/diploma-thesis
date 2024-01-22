package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import cz.upce.fei.dt.beckend.entities.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class SecurityService {
    private final String LOGOUT_SUCCESS_URL = "/";
    public User getAuthenticatedUser(){
        if (SecurityContextHolder.getContext().getAuthentication() != null){
            var user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (user instanceof UserDetails){
                return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            }
        }
        return null;
    }
    public void logout(){
        UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null,null);
    }
}
