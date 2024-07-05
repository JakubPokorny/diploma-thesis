package cz.upce.fei.dt.ui.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.function.Consumer;

public class BoardCell extends HorizontalLayout {
    public VerticalLayout leftLayout = new VerticalLayout();
    public VerticalLayout rightLayout = new VerticalLayout();
    public HorizontalLayout valueLayout = new HorizontalLayout();
    public Span title;
    public Icon valueIcon;
    public Span value;
    public Span linkValue = new Span("Zobrazit");
    public Icon linkIcon = new Icon(VaadinIcon.ANGLE_DOUBLE_RIGHT);
    public Consumer<? super UI> navigationTarget;

    public BoardCell(String titleText, String valueText, String theme, Icon icon, Consumer<? super UI> navigationTarget) {
        title = new Span(titleText);
        value = new Span(valueText);
        valueIcon = icon;
        valueIcon.getElement().getThemeList().add(theme);
        this.navigationTarget = navigationTarget;

        this.setAlignItems(FlexComponent.Alignment.CENTER);
        this.setClassName("bord-cell-main-layout");

        leftLayout.setClassName("board-cell-left-layout");
        rightLayout.setClassName("board-cell-right-layout");
        title.setClassName("board-cell-title");
        valueIcon.setClassName("board-cell-value-icon");
        value.setClassName("board-cell-value");
        linkIcon.setClassName("board-cell-link-icon");
        linkValue.setClassName("board-cell-link-value");
        valueLayout.setClassName("board-cell-value-layout");

        valueLayout.add(valueIcon, value);
        leftLayout.add(title, valueLayout);
        rightLayout.add(linkIcon, linkValue);
        rightLayout.setSpacing(false);
        rightLayout.setAlignItems(Alignment.CENTER);

        this.add(leftLayout, rightLayout);

        this.addClickListener(_ -> this.getUI().ifPresent(navigationTarget));
    }
}
