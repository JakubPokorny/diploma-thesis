package cz.upce.fei.dt.ui.components;

import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.html.Span;

public class DashboardItem extends Board {
    public Span title = new Span();

    public DashboardItem(String titleText) {
        setSizeFull();
        setClassName("dashboard-item");

        title.setText(titleText);
        title.setClassName("board-title");

        add(title);
    }
}
