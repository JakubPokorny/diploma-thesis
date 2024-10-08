package cz.upce.fei.dt.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
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
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.Lumo;
import cz.upce.fei.dt.backend.exceptions.CustomErrorHandler;
import cz.upce.fei.dt.ui.components.AvatarMenuBar;

//@JsModule("./prefers-color-scheme.js")
public class MainLayout extends AppLayout implements RouterLayout {
    private final transient AuthenticationContext authContext;
    private static RouterLink pageTitle;

    public MainLayout(AuthenticationContext authContext) {
        this.authContext = authContext;
        if (!authContext.isAuthenticated())
            return;
        VaadinSession.getCurrent().setErrorHandler(new CustomErrorHandler());
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

    public static void setPageTitle(String title, Class<? extends Component> navigationTarget) {
        pageTitle.setText(title);
        pageTitle.setRoute(navigationTarget);
    }

    private Button createThemeSwitcher() {
        Button themeSwitcher = new Button();
        themeSwitcher.setIcon(VaadinIcon.ADJUST.create());
        themeSwitcher.setClassName("theme-switcher");

        themeSwitcher.addClickListener(_ -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
            } else {
                themeList.add(Lumo.DARK);
            }
        });
        return themeSwitcher;
    }

    private void createDrawer() {
        Image logo = new Image("./images/logo-blue.png", "logo 2024 | BoxEnergy");
        logo.getStyle().set("margin", "var(--lumo-space-m)");

        addToDrawer(logo, getDrawerNavigation());
        setPrimarySection(Section.DRAWER);
    }

    private Tabs getDrawerNavigation() {
        Tabs tabs = new Tabs(
                new Tab(VaadinIcon.DASHBOARD.create(), new RouterLink("Dashboard", DashboardView.class)),
                new Tab(VaadinIcon.PAPERCLIP.create(), new RouterLink("Zakázky", ContractsView.class)),
                new Tab(VaadinIcon.CART.create(), new RouterLink("Produkty", ProductsView.class)),
                new Tab(VaadinIcon.HOME.create(), new RouterLink("Komponenty", ComponentsView.class)),
                new Tab(VaadinIcon.NOTEBOOK.create(), new RouterLink("Kontakty", ContactsView.class)),
                new Tab(VaadinIcon.USERS.create(), new RouterLink("Uživatelé", UsersView.class)),
                new Tab(VaadinIcon.TABLE.create(), new RouterLink("Stavy", StatusView.class))
        );
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.setSelectedIndex(0);
        return tabs;
    }
}
