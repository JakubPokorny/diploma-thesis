package cz.upce.fei.dt.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.Lumo;
import cz.upce.fei.dt.beckend.services.SecurityService;
import cz.upce.fei.dt.ui.components.AvatarMenuBar;

public class MainLayout extends AppLayout {
    private final SecurityService securityService;
    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        Tabs subViews = getSecondaryViews();
        DrawerToggle toggle = new DrawerToggle();
        Span spacer = new Span("");
        AvatarMenuBar avatarMenuBar = new AvatarMenuBar(securityService);

        Checkbox themeSwitcher = new Checkbox("Dark Mode");
        themeSwitcher.addValueChangeListener(e -> changeTheme());


        HorizontalLayout mainHeader = new HorizontalLayout(toggle, spacer, avatarMenuBar, themeSwitcher);
        mainHeader.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        mainHeader.expand(spacer);
        mainHeader.setWidthFull();
        mainHeader.setSpacing(false);

        VerticalLayout sideHeader = new VerticalLayout(mainHeader, subViews);

        sideHeader.setPadding(false);
        sideHeader.setSpacing(false);
        addToNavbar(sideHeader);
    }

    private void createDrawer() {
        H1 appTitle = new H1("IS | DT CRM");
        //todo - move to css.
        appTitle.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("line-height", "var(--lumo-size-l)")
                .set("margin", "0 var(--lumo-space-m)");
        Tabs views = getPrimaryNavigation();
        addToDrawer(appTitle, views);
        setPrimarySection(Section.DRAWER);
    }

    private void changeTheme() {
        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        if (themeList.contains(Lumo.DARK)) {
            themeList.remove(Lumo.DARK);
        } else {
            themeList.add(Lumo.DARK);
        }

    }

    private Tabs getSecondaryViews() {
        Tabs tabs = new Tabs();
        tabs.add(
                new Tab("Všichni"),
                new Tab("Správci"),
                new Tab("Konstruktéři")
        );
        return tabs;
    }

    private Tabs getPrimaryNavigation() {
        Tabs tabs = new Tabs();
        tabs.add(
                createTab(VaadinIcon.USERS, "Uživatelé"),
                createTab(VaadinIcon.CART, "Zakázky"),
                createTab(VaadinIcon.DATABASE, "Číselníky")
        );
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.setSelectedIndex(1);
        return tabs;
    }

    private Component createTab(VaadinIcon viewIcon, String label) {
        Icon icon = viewIcon.create();
        icon.getStyle().set("box-sizing", "border-box")
                .set("margin-inline-end", "var(--lumo-space-m)")
                .set("padding", "var(--lumo-space-xs)");

        Span showAbleLabel = new Span(label);
        showAbleLabel.setId("sideTab");
        //TODO hide just label;

        RouterLink link = new RouterLink();

        link.add(icon, showAbleLabel);
        //link.setRoute();
        link.setTabIndex(-1);
        return new Tab(link);
    }
}
