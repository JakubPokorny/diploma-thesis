package cz.upce.fei.dt.ui.components;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.tabs.Tab;

public class TabWithBadge extends Tab {
    public Span label;
    public Badge badge;
    public TabWithBadge(String labelText, Badge badge) {
        label = new Span(labelText);
        this.badge = badge;
        this.badge.getElement().getThemeList().add(" small");
        this.badge.getStyle().set("margin-inline-start", "var(--lumo-space-s)");
        add(label, this.badge);
    }
}
