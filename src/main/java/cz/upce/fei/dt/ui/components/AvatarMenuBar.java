package cz.upce.fei.dt.ui.components;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.upce.fei.dt.beckend.entities.User;

public class AvatarMenuBar extends HorizontalLayout{
    public AvatarMenuBar(AuthenticationContext authContext) {
        if (authContext.getAuthenticatedUser(User.class).isEmpty())
            return;
        User user = authContext.getAuthenticatedUser(User.class).get();

        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

        HorizontalLayout userMenuItem = createUserMenuItem(user);
        MenuItem menuItem = menuBar.addItem(userMenuItem);
        SubMenu subMenu = menuItem.getSubMenu();
        subMenu.addItem("Profil");
        subMenu.addItem("Odhlásit", click -> authContext.logout());

        add(menuBar);
    }

    private static HorizontalLayout createUserMenuItem(User user) {
        Avatar avatar = new Avatar(user.getFirstName() + " " + user.getLastName());
        Span label = new Span(avatar.getName());
        Icon angleDown = new Icon(VaadinIcon.ANGLE_DOWN);
        angleDown.setClassName("angle-down");

        HorizontalLayout userMenuItem = new HorizontalLayout(avatar, label, angleDown);
        userMenuItem.setSpacing(true);
        userMenuItem.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        return userMenuItem;
    }
}
