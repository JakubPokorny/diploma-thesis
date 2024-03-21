package cz.upce.fei.dt.ui.views;

import com.vaadin.flow.component.Component;
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
import cz.upce.fei.dt.ui.views.customerContacts.ContactsView;
import cz.upce.fei.dt.ui.views.contracts.ContractsView;
import cz.upce.fei.dt.ui.views.dashboard.DashboardView;
import cz.upce.fei.dt.ui.views.users.UsersView;

@JsModule("prefers-color-scheme.js")
public class MainLayout extends AppLayout implements RouterLayout{
    private final transient AuthenticationContext authContext;
    private static RouterLink pageTitle;
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

    public static void setPageTitle(String title, Class<? extends Component> navigationTarget){
        pageTitle.setText(title);
        pageTitle.setRoute(navigationTarget);
    }

    private Button createThemeSwitcher() {
        Button themeSwitcher = new Button();
        themeSwitcher.setIcon(VaadinIcon.ADJUST.create());
        themeSwitcher.setClassName("theme-switcher");

        themeSwitcher.addClickListener(click -> {
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
        H1 appTitle = new H1("IS | DT CRM");
        appTitle.addClassName("app-title");
        addToDrawer(appTitle, getDrawerNavigation());
        setPrimarySection(Section.DRAWER);
    }

    private Tabs getDrawerNavigation() {
        Tabs tabs = new Tabs(
                new Tab(VaadinIcon.DASHBOARD.create(), new RouterLink("Dashboard", DashboardView.class)),
                new Tab(VaadinIcon.USERS.create(), new RouterLink("Uživatelé", UsersView.class)),
                new Tab(VaadinIcon.NOTEBOOK.create(), new RouterLink("Kontakty", ContactsView.class)),
                new Tab(VaadinIcon.CART.create(), new RouterLink("Zakázky", ContractsView.class)),
                new Tab(VaadinIcon.HOME.create(), new RouterLink("Komponenty", ComponentsView.class)),
                new Tab(VaadinIcon.CART.create(), new RouterLink("Produkty", ProductView.class))
        );
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.setSelectedIndex(0);
        return tabs;
    }
}
