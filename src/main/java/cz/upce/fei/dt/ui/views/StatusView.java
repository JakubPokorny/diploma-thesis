package cz.upce.fei.dt.ui.views;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import cz.upce.fei.dt.beckend.entities.Status;
import cz.upce.fei.dt.beckend.entities.Status_;
import cz.upce.fei.dt.beckend.services.StatusService;
import cz.upce.fei.dt.beckend.services.filters.StatusFilter;
import cz.upce.fei.dt.ui.components.Badge;
import cz.upce.fei.dt.ui.components.FilterFields;
import cz.upce.fei.dt.ui.components.GridFormLayout;
import cz.upce.fei.dt.ui.components.forms.StatusForm;
import cz.upce.fei.dt.ui.components.forms.events.DeleteEvent;
import cz.upce.fei.dt.ui.components.forms.events.SaveEvent;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "statuses", layout = MainLayout.class)
@RouteAlias(value = "statusy", layout = MainLayout.class)
@PageTitle("Statusy")
@RolesAllowed("ADMIN")
public class StatusView extends VerticalLayout {
    private final StatusService statusService;
    private final GridFormLayout<StatusForm, Status> gridFormLayout;
    private final Grid<Status> grid;
    private final StatusFilter statusFilter = new StatusFilter();
    private ConfigurableFilterDataProvider<Status, Void, StatusFilter> configurableFilterDataProvider;

    public StatusView(StatusService statusService) {
        this.statusService = statusService;

        StatusForm form = new StatusForm();
        grid = new Grid<>(Status.class, false);
        gridFormLayout = new GridFormLayout<>(form, grid);
        MainLayout.setPageTitle("Statusy", StatusView.class);
        setSizeFull();

        configureGrid();
        configureForm();
        configureActions();
        configureFilters();

        add(gridFormLayout);
    }

    //region configures: grid, form, actions, filters, events
    private void configureFilters() {
    }

    private void configureActions() {
        Button addStatus = new Button("Přidat status");
        addStatus.addClickListener(event -> gridFormLayout.addNewValue(new Status()));
        gridFormLayout.getActionsLayout().add(addStatus);
    }

    private void configureForm() {
        ComponentUtil.addListener(gridFormLayout, SaveEvent.class, this::saveStatus);
        ComponentUtil.addListener(gridFormLayout, DeleteEvent.class, this::deleteStatus);
    }

    private void saveStatus(SaveEvent saveEvent) {
        try {
            Status status = (Status) saveEvent.getValue();
            statusService.saveStatus(status);
            updateGrid();
            gridFormLayout.closeFormLayout();
            Notification.show("Status " + status.getStatus() + " uložen.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception exception) {
            Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteStatus(DeleteEvent deleteEvent) {
        try {
            Status status = (Status) deleteEvent.getValue();
            statusService.deleteStatus(status);
            updateGrid();
            gridFormLayout.closeFormLayout();
            Notification.show("Status " + status.getStatus() + " odstraněn.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception exception) {
            Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void configureGrid() {
        grid.setClassName("grid-content");
        grid.setSizeFull();

        DataProvider<Status, StatusFilter> dataProvider = DataProvider.fromFilteringCallbacks(statusService::fetchFromBackEnd, statusService::sizeInBackEnd);

        configurableFilterDataProvider = dataProvider.withConfigurableFilter();
        configurableFilterDataProvider.setFilter(statusFilter);

        Grid.Column<Status> statusColumn = grid.addComponentColumn(status -> new Badge(status.getStatus(), status.getTheme().getTheme())).setHeader("Stav").setKey(Status_.STATUS).setWidth("150px");
        Grid.Column<Status> themeColumn = grid.addComponentColumn(status -> new Badge(status.getTheme() + ", " + status.getTheme().getMeaning(), status.getTheme().getTheme())).setHeader("Motiv").setKey(Status_.THEME).setWidth("150px");

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(statusColumn).setComponent(FilterFields.createTextFieldFilter("stav", statusFilter::setStatusFilter, configurableFilterDataProvider));
        headerRow.getCell(themeColumn).setComponent(FilterFields.createTextFieldFilter("motiv", statusFilter::setThemeFilter, configurableFilterDataProvider));

        grid.asSingleSelect().addValueChangeListener(e -> gridFormLayout.showFormLayout(e.getValue()));
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        grid.setSortableColumns(Status_.STATUS, Status_.THEME);

        updateGrid();
    }

    //endregion
    private void updateGrid() {
        grid.setItems(configurableFilterDataProvider);
    }
}
