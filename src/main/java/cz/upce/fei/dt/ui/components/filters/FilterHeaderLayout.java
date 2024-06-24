package cz.upce.fei.dt.ui.components.filters;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class FilterHeaderLayout extends VerticalLayout {
    public FilterHeaderLayout(Component... children) {
        super(children);
        this.getThemeList().clear();
        this.getThemeList().add("spacing-xs");
    }
}
