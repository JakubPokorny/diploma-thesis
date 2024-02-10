package cz.upce.fei.dt.ui.views.errors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.HttpStatusCode;
import cz.upce.fei.dt.ui.views.MainLayout;

import java.util.Collection;

@ParentLayout(MainLayout.class)
@Tag(Tag.DIV)
public class CustomAccessDeniedException extends Component implements HasErrorParameter<AccessDeniedException> {

    public CustomAccessDeniedException() {
        addClassName("error-view");
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent beforeEnterEvent, ErrorParameter<AccessDeniedException> errorParameter) {
        getElement().appendChild(
                new Element("h1").setText("Nepovolený přístup, 403"),
                new Element("a").setText("Přejít domů").setAttribute("href", "/").setAttribute("class", "router-link")
        );
        return HttpStatusCode.FORBIDDEN.getCode();
    }
}
