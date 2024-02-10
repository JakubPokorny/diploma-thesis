package cz.upce.fei.dt.ui.views.errors;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.HttpStatusCode;
import cz.upce.fei.dt.ui.views.MainLayout;
import cz.upce.fei.dt.ui.views.dashboard.DashboardView;

import java.util.Collection;

@ParentLayout(MainLayout.class)
@Tag(Tag.DIV)
public class CustomRouteNotFoundError extends RouteNotFoundError implements HasErrorParameter<NotFoundException>{
    public CustomRouteNotFoundError() {
        addClassName("error-view");
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        getElement().removeAllChildren();
        getElement().appendChild(
                new Element("h1").setText("Stránka \"" + event.getLocation().getPath() + "\" nenalezena, 404."),
                new Element("a").setText("Přejít domů").setAttribute("href", "/").setAttribute("class", "router-link")
        );
        return HttpStatusCode.NOT_FOUND.getCode();
    }
}
