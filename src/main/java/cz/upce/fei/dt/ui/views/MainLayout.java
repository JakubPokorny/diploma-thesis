package cz.upce.fei.dt.ui.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.Lumo;
import cz.upce.fei.dt.beckend.services.SecurityService;
import cz.upce.fei.dt.ui.components.AvatarMenuBar;
import cz.upce.fei.dt.ui.views.contracts.ContractsView;
import cz.upce.fei.dt.ui.views.dashboard.DashboardView;
import cz.upce.fei.dt.ui.views.dials.DialsView;
import cz.upce.fei.dt.ui.views.users.UsersView;
import org.springframework.beans.factory.annotation.Autowired;

public class MainLayout extends AppLayout implements RouterLayout {
    private final transient AuthenticationContext authContext;
    public MainLayout(AuthenticationContext authContext) {
        this.authContext = authContext;
        if (!authContext.isAuthenticated())
            return;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        DrawerToggle drawer = new DrawerToggle();

        Span spacer = new Span("");
        AvatarMenuBar avatarMenuBar = new AvatarMenuBar(authContext);

        Checkbox themeSwitcher = new Checkbox("Dark Mode");
        themeSwitcher.addValueChangeListener(e -> changeTheme());


        HorizontalLayout mainHeader = new HorizontalLayout(drawer, spacer, avatarMenuBar, themeSwitcher);
        mainHeader.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        mainHeader.expand(spacer);
        mainHeader.setWidthFull();
        mainHeader.setSpacing(false);

        VerticalLayout sideHeader = new VerticalLayout(mainHeader);

        sideHeader.setPadding(false);
        sideHeader.setSpacing(false);
        addToNavbar(mainHeader);
    }

    private void createDrawer() {
        H1 appTitle = new H1("IS | DT CRM");
        appTitle.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("line-height", "var(--lumo-size-l)")
                .set("margin", "0 var(--lumo-space-m)");
        addToDrawer(appTitle, getDrawerNavigation());
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

    private Tabs getDrawerNavigation() {
        Tab dashboard = new Tab(VaadinIcon.DASHBOARD.create(), new RouterLink("Dashboard", DashboardView.class));
        Tab users = new Tab(VaadinIcon.USERS.create(), new RouterLink("Uživatelé", UsersView.class));
        Tab contracts = new Tab(VaadinIcon.CART.create(), new RouterLink("Zakázky", ContractsView.class));
        Tab dials = new Tab(VaadinIcon.DATABASE.create(), new RouterLink("Číselniky", DialsView.class));

        Tabs tabs = new Tabs(dashboard,users,contracts,dials);
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.setSelectedIndex(0);
        return tabs;
    }
}
