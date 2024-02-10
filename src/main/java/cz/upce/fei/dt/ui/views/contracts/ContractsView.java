package cz.upce.fei.dt.ui.views.contracts;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.upce.fei.dt.ui.views.MainLayout;
import jakarta.annotation.security.PermitAll;

@PermitAll
@PageTitle("Contracts")
@Route(value = "contracts", layout = MainLayout.class)
public class ContractsView extends HorizontalLayout {
    public ContractsView() {
        add(new H1("/contracts"));
    }
}
