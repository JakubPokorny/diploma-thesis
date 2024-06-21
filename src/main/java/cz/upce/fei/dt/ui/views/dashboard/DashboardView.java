package cz.upce.fei.dt.ui.views.dashboard;

import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import cz.upce.fei.dt.beckend.entities.Deadline;
import cz.upce.fei.dt.beckend.entities.Status;
import cz.upce.fei.dt.beckend.services.ComponentService;
import cz.upce.fei.dt.beckend.services.DeadlineService;
import cz.upce.fei.dt.ui.components.Badge;
import cz.upce.fei.dt.ui.views.MainLayout;
import jakarta.annotation.security.PermitAll;

import java.util.List;

@Route(value = "dashboard", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle("Dashboard")
@PermitAll
public class DashboardView extends VerticalLayout {
    private final ComponentService componentService;
    private final DeadlineService deadlineService;

    public DashboardView(
            ComponentService componentService,
            DeadlineService deadlineService) {
        this.componentService = componentService;
        this.deadlineService = deadlineService;

        MainLayout.setPageTitle("Dashboard", DashboardView.class);

        add(new H2("Skladové komponenty"),
                createStockBoard(),
                new H2("Statusy zakázek"),
                createStatusesBoard(),
                new H2("Termíny zakázek"),
                createDeadlinesBoard()
        );
    }

    private Board createDeadlinesBoard() {
        Board board = new Board();
        board.setSizeFull();
        DeadlinesCounter counter = getDeadlinesCounter(deadlineService.findAllCurrentDeadlines());

        board.addRow(
                createCell("Bez termínu", String.valueOf(counter.withoutTerm()), "contrast"),
                createCell("Před termínem", String.valueOf(counter.beforeTerm()), "pending"),
                createCell("Po termínu", String.valueOf(counter.afterTerm()), "error")
        );
        return board;
    }



    private Board createStockBoard() {

        Board board = new Board();
        board.setSizeFull();
        board.addRow(
                createCell("Bez minimální nastavené hranice", String.valueOf(componentService.getCountWithoutMinInStock()), "pending"),
                createCell("Skladem", String.valueOf(componentService.getCountInStock()), "success"),
                createCell("Doplnit", String.valueOf(componentService.getCountInStockSupply()), "warning"),
                createCell("Chybí", String.valueOf(componentService.getCountInStockMissing()), "error")
        );
        return board;
    }

    private Board createStatusesBoard() {
        Board board = new Board();
        board.setSizeFull();
        board.addRow(
                createContractCell(Status.Theme.SUCCESS),
                createContractCell(Status.Theme.PENDING),
                createContractCell(Status.Theme.CONTRAST)
        );
        board.addRow(
                createContractCell(Status.Theme.WARNING),
                createContractCell(Status.Theme.ERROR)
        );
        return board;
    }

    private HorizontalLayout createContractCell(Status.Theme theme) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(false);
        layout.setAlignItems(Alignment.CENTER);
        layout.setClassName("cell");
        layout.getElement().getThemeList().add(theme.name().toLowerCase());

        List<Deadline> deadlines = deadlineService.findAllCurrentDeadlinesByStatus(theme);
        DeadlinesCounter counter = getDeadlinesCounter(deadlines);

        VerticalLayout rightLayout = new VerticalLayout(
                new Badge(counter.withoutTerm() + " bez termínu", "small contrast"),
                new Badge(counter.beforeTerm() + " před termínem", "small"),
                new Badge(counter.afterTerm() + " po termínu", "small error")
        );
        rightLayout.setClassName("cell");

        VerticalLayout leftLayout = createCell(theme.getMeaning(), String.valueOf(deadlines.size()), theme.name().toLowerCase());
        leftLayout.removeClassName("cell");
        leftLayout.getElement().getThemeList().remove(theme.getTheme().toLowerCase());
        leftLayout.setAlignItems(Alignment.END);
        layout.add(leftLayout, rightLayout);
        return layout;
    }

    private VerticalLayout createCell(String titleText, String valueText, String theme) {
        VerticalLayout cell = new VerticalLayout();
        cell.addClassName("cell");
        cell.getElement().getThemeList().add(" " + theme);
        Span title = new Span(titleText);
        title.addClassName("cell-title");
        Span value = new Span(valueText);
        value.addClassName("cell-value");
        cell.add(title, value);
        return cell;
    }

    private static DeadlinesCounter getDeadlinesCounter(List<Deadline> deadlines) {
        int withoutTerm = 0;
        int beforeTerm = 0;
        int afterTerm = 0;
        for (Deadline deadline : deadlines) {
            Boolean term = deadline.isBeforeOrNowDeadline();
            if (term == null) {
                withoutTerm++;
                continue;
            }
            if (term)
                beforeTerm++;
            else
                afterTerm++;
        }
        return new DeadlinesCounter(withoutTerm, beforeTerm, afterTerm);
    }

    private record DeadlinesCounter(int withoutTerm, int beforeTerm, int afterTerm) {
    }

}
