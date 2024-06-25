package cz.upce.fei.dt.ui.components;

import com.vaadin.flow.component.html.Span;

public class Badge extends Span {
    public String labelText;
    public String theme = "badge pill ";

    public Badge(String labelText) {
        this.labelText = labelText;

        this.getElement().getThemeList().add(theme);
        this.setText(labelText);
    }

    public Badge(String labelText, String theme) {
        this.theme += theme;

        this.getElement().getThemeList().add(this.theme);
        this.setText(labelText);
    }


}
