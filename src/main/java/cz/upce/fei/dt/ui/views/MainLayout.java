package cz.upce.fei.dt.ui.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
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
import cz.upce.fei.dt.ui.components.AvatarMenuBar;
import cz.upce.fei.dt.ui.views.contracts.ContractsView;
import cz.upce.fei.dt.ui.views.dashboard.DashboardView;
import cz.upce.fei.dt.ui.views.dials.DialsView;
import cz.upce.fei.dt.ui.views.users.UsersView;

@JsModule("prefers-color-scheme.js")
public class MainLayout extends AppLayout implements RouterLayout{
    private final transient AuthenticationContext authContext;
    public static RouterLink pageTitle;
    public MainLayout(AuthenticationContext authContext) {
        this.authContext = authContext;
        if (!authContext.isAuthenticated())
            return;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        pageTitle = new RouterLink("Dashboard", DashboardView.class);
        pageTitle.setClassName("page-title");
        Span spacer = new Span("");
        HorizontalLayout header = new HorizontalLayout(
                new DrawerToggle(),
                pageTitle,
                spacer,
                new AvatarMenuBar(authContext),
                createThemeSwitcher());
        header.setClassName("header");
        header.setSpacing(true);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(spacer);
        header.setWidthFull();

        VerticalLayout navbar = new VerticalLayout(header);
        navbar.setPadding(false);
        navbar.setSpacing(false);
        addToNavbar(header);
    }

    private Button createThemeSwitcher() {
        Button themeSwitcher = new Button();
        themeSwitcher.setClassName("theme-switcher");
        if (UI.getCurrent().getElement().getThemeList().contains(Lumo.DARK)){
            themeSwitcher.setIcon(VaadinIcon.SUN_RISE.create());
        }else {
            themeSwitcher.setIcon(VaadinIcon.MOON_O.create());
        }

        themeSwitcher.addClickListener(click -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
                themeSwitcher.setIcon(VaadinIcon.MOON_O.create());
            } else {
                themeList.add(Lumo.DARK);
                themeSwitcher.setIcon(VaadinIcon.SUN_RISE.create());
            }
        });
        return themeSwitcher;
    }

    private void createDrawer() {
        H1 appTitle = new H1("IS | DT CRM");
        appTitle.addClassName("app-title");
        addToDrawer(appTitle, getDrawerNavigation());
        setPrimarySection(Section.DRAWER);
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
