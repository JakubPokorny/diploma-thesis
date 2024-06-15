package cz.upce.fei.dt.ui.components;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.tabs.Tab;

public class TabWithBadge extends Tab {
    public Span label;
    public Span badge;
    public TabWithBadge(String labelText, String badgeText, String badgeStyle) {
        label = new Span(labelText);
        badge = new Span(badgeText);

        badge.getElement().getThemeList().add("badge pill small " + badgeStyle);
        badge.getStyle().set("margin-inline-start", "var(--lumo-space-s)");

        add(label, badge);
    }

}
