package cz.upce.fei.dt.ui.views.dials;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.upce.fei.dt.ui.views.MainLayout;
import jakarta.annotation.security.PermitAll;

@PermitAll
@PageTitle("Dials")
@Route(value = "dials", layout = MainLayout.class)
public class DialsView extends HorizontalLayout {
    public DialsView() {
        add(new H1("/dials"));
    }
}
