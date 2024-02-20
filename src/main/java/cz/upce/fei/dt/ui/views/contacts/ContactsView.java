package cz.upce.fei.dt.ui.views.contacts;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import cz.upce.fei.dt.ui.views.MainLayout;
import jakarta.annotation.security.PermitAll;

@Route(value = "contacts", layout = MainLayout.class)
@RouteAlias(value = "kontakty", layout = MainLayout.class)
@PageTitle("Contacts")
@PermitAll
public class ContactsView extends VerticalLayout {
    public ContactsView() {
        MainLayout.setPageTitle("Kontakty", ContactsView.class);
    }
}
