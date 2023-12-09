package cz.upce.fei.dt.ui.components;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.theme.lumo.LumoUtility;

import javax.swing.*;

public class AvatarMenuBar extends Div{
    public AvatarMenuBar() {
        Avatar avatar = new Avatar("Jakub Pokorný");
        Span label = new Span(avatar.getName());
        label.getStyle()
                .set("margin", "0 8px");
        //todo add arrow icon

        Div login = new Div(avatar, label);
        login.getStyle().set("display", "flex")
                .set("align-items", "center");

        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        MenuItem menuItem = menuBar.addItem(login);
        SubMenu subMenu = menuItem.getSubMenu();
        subMenu.addItem("Profil");
        subMenu.addItem("Nastavení");
        subMenu.addItem("Nápověda");
        subMenu.addItem("Odhlásit");

        add(menuBar);
    }
}
