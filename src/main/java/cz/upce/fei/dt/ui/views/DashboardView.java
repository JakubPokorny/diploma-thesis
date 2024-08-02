package cz.upce.fei.dt.ui.views;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import cz.upce.fei.dt.beckend.dto.ComponentMetrics;
import cz.upce.fei.dt.beckend.entities.Deadline;
import cz.upce.fei.dt.beckend.entities.Status;
import cz.upce.fei.dt.beckend.services.ComponentService;
import cz.upce.fei.dt.beckend.services.DeadlineService;
import cz.upce.fei.dt.beckend.services.ProductService;
import cz.upce.fei.dt.ui.components.Badge;
import cz.upce.fei.dt.ui.components.BoardCell;
import cz.upce.fei.dt.ui.components.DashboardItem;
import cz.upce.fei.dt.ui.components.charts.ProductsPieChart;
import jakarta.annotation.security.PermitAll;

@Route(value = "dashboard", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle("Dashboard")
@PermitAll
public class DashboardView extends VerticalLayout {
    private final ComponentService componentService;
    private final DeadlineService deadlineService;
    private final ProductService productService;

    public DashboardView(
            ComponentService componentService,
            DeadlineService deadlineService,
            ProductService productService) {
        this.componentService = componentService;
        this.deadlineService = deadlineService;
        this.productService = productService;

        MainLayout.setPageTitle("Dashboard", DashboardView.class);

        add(createStockBoard(), createContractBoard(), createProductBoard());
    }

    private DashboardItem createStockBoard() {
        DashboardItem stockBoard = new DashboardItem("Skladové komponenty");
        ComponentMetrics componentMetrics = componentService.countMetrics();
        stockBoard.addRow(
                new BoardCell("Doplnit", String.valueOf(componentMetrics.supply()), "warning", VaadinIcon.WARNING.create(), ui -> ui.navigate(ComponentsView.class, "supply")),
                new BoardCell("Chybí", String.valueOf(componentMetrics.missing()), "error", VaadinIcon.EXCLAMATION.create(), ui -> ui.navigate(ComponentsView.class, "missing"))
        );
        return stockBoard;
    }

    private DashboardItem createContractBoard() {
        DashboardItem contractBoard = new DashboardItem("Statusy zakázek");
        int success = 0, pending = 0, warning = 0, error = 0;
        int pendingAfterDeadline = 0, warningAfterDeadline = 0;
        for (Deadline deadline : deadlineService.findAllCurrentDeadlines()) {
            Status.Theme theme = deadline.getStatus().getTheme();
            switch (theme) {
                case SUCCESS -> success++;
                case PENDING -> {
                    pending++;
                    if (deadline.isAfterDeadline())
                        pendingAfterDeadline++;
                }
                case WARNING -> {
                    warning++;
                    if (deadline.isAfterDeadline())
                        warningAfterDeadline++;
                }
                case ERROR -> error++;
            }
        }

        BoardCell pendingCell = new BoardCell("V Procesu", String.valueOf(pending), "pending", VaadinIcon.CLOCK.create(), ui -> ui.navigate(ContractsView.class, "in_progress"));
        pendingCell.leftLayout.add(new Badge(pendingAfterDeadline + " po termínu", "small error"));
        BoardCell warningCell = new BoardCell("Pozor", String.valueOf(warning), "warning", VaadinIcon.WARNING.create(), ui -> ui.navigate(ContractsView.class, "warning"));
        warningCell.leftLayout.add(new Badge(warningAfterDeadline + " po termínu", "small error"));

        contractBoard.addRow(
                new BoardCell("Hotovo", String.valueOf(success), "success", VaadinIcon.CHECK.create(), ui -> ui.navigate(ContractsView.class, "done")),
                pendingCell,
                warningCell,
                new BoardCell("Chyba", String.valueOf(error), "error", VaadinIcon.EXCLAMATION.create(), ui -> ui.navigate(ContractsView.class, "error"))
        );
        return contractBoard;
    }

    private DashboardItem createProductBoard() {
        DashboardItem productBoard = new DashboardItem("Nejprodávanější produkty");
        productBoard.addRow(new ProductsPieChart(productService));
        return productBoard;
    }
}
