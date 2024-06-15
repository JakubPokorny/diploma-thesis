package cz.upce.fei.dt.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import cz.upce.fei.dt.beckend.entities.Contract;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.Status;
import cz.upce.fei.dt.beckend.services.*;
import cz.upce.fei.dt.beckend.services.filters.ContractFilter;
import cz.upce.fei.dt.beckend.services.filters.DeadlineFilterTag;
import cz.upce.fei.dt.ui.components.FilterFields;
import cz.upce.fei.dt.ui.components.GridFormLayout;
import cz.upce.fei.dt.ui.components.TabWithBadge;
import cz.upce.fei.dt.ui.components.forms.ContractForm;
import cz.upce.fei.dt.ui.components.forms.events.DeleteEvent;
import cz.upce.fei.dt.ui.components.forms.events.SaveEvent;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "contracts", layout = MainLayout.class)
@RouteAlias(value = "Zakázky", layout = MainLayout.class)
@PageTitle("Zakázky")
@PermitAll
public class ContractsView extends VerticalLayout {
    private final ContractService contractService;
    private final ProductService productService;
    private final ContactService contactService;
    private final DeadlineService deadlineService;
    private final StatusService statusService;
    private final Grid<Contract> grid;
    private final GridFormLayout<ContractForm, Contract> gridFormLayout;
    private final ContractFilter contractFilter = new ContractFilter();
    private final TabWithBadge all = createTabWithBadge("Všechny", "contrast", DeadlineFilterTag.ALL);
    private final TabWithBadge withoutDeadline = createTabWithBadge("Bez termínu", "contrast", DeadlineFilterTag.WITHOUT_DEADLINE);
    private final TabWithBadge beforeDeadline = createTabWithBadge("Před termínem", "", DeadlineFilterTag.BEFORE_DEADLINE);
    private final TabWithBadge afterDeadline = createTabWithBadge("Po termínu", "error", DeadlineFilterTag.AFTER_DEADLINE);
    private DataProvider<Contract, ContractFilter> dataProvider;
    private ConfigurableFilterDataProvider<Contract, Void, ContractFilter> configurableFilterDataProvider;

    public ContractsView(
            ContractService contractService,
            ContactService contactService,
            ProductService productService,
            NoteService noteService,
            FileService fileService,
            DeadlineService deadlineService,
            StatusService statusService) {
        this.contractService = contractService;
        this.productService = productService;
        this.contactService = contactService;
        this.deadlineService = deadlineService;
        this.statusService = statusService;

        ContractForm form = new ContractForm(contactService, productService, noteService, fileService, deadlineService, statusService);
        grid = new Grid<>(Contract.class, false);
        gridFormLayout = new GridFormLayout<>(form, grid);
        MainLayout.setPageTitle("Zakázky", ContractsView.class);
        setSizeFull();

        configureGrid();
        configureForm();
        configureActions();
        configureFilters();

        add(gridFormLayout);
    }

    //region configures
    private void configureFilters() {
        Tabs tabs = new Tabs(all, withoutDeadline, beforeDeadline, afterDeadline);
        tabs.setClassName("tabs");
        tabs.setMaxWidth("100%");
        tabs.setSelectedIndex(0);

        gridFormLayout.getFiltersLayout().add(tabs);

    }

    private void configureActions() {
        Button addContract = new Button("Přidat Zakázku");
        addContract.addClickListener(event -> gridFormLayout.addNewValue(new Contract()));
        gridFormLayout.getActionsLayout().add(addContract);
    }

    private void configureForm() {
        ComponentUtil.addListener(gridFormLayout, SaveEvent.class, this::saveContract);
        ComponentUtil.addListener(gridFormLayout, DeleteEvent.class, this::deleteContract);
    }

    //region events: save, delete
    private void deleteContract(DeleteEvent deleteEvent) {
        try {
            Contract contract = (Contract) deleteEvent.getValue();
            contractService.deleteContract(contract);
            updateGrid();
            gridFormLayout.closeFormLayout();
            Notification.show("Zakázka " + contract.getId() + " odstraněna.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception exception) {
            Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void saveContract(SaveEvent saveEvent) {
        try {
            Contract contract = (Contract) saveEvent.getValue();
            contractService.saveContract(contract);
            updateGrid();
            gridFormLayout.closeFormLayout();
            Notification.show("Zakázka " + contract.getId() + " uložena.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception exception) {
            Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    //endregion

    private void configureGrid() {
        grid.setClassName("grid-content");
        grid.setSizeFull();

        dataProvider = DataProvider.fromFilteringCallbacks(
                contractService::fetchFromBackEnd,
                contractService::sizeInBackEnd
        );

        configurableFilterDataProvider = dataProvider.withConfigurableFilter();
        configurableFilterDataProvider.setFilter(contractFilter);

        Grid.Column<Contract> idColumn = grid.addColumn(Contract::getId).setHeader("ID").setKey("id").setWidth("50px");
        Grid.Column<Contract> clientColumn = grid.addComponentColumn(this::getClient).setHeader("Klient").setWidth("150px");
        Grid.Column<Contract> stateColumn = grid.addComponentColumn(this::getState).setHeader("Stav").setWidth("150px");
        Grid.Column<Contract> deadlineColumn = grid.addComponentColumn(this::getDeadline).setHeader("Termín").setWidth("150px");
        Grid.Column<Contract> priceColumn = grid.addColumn(Contract::getPrice).setHeader("Cena s marží").setKey("price").setWidth("150px");
        Grid.Column<Contract> productsColumn = grid.addComponentColumn(this::getProducts).setHeader("Objednané produkty").setWidth("150px");
        Grid.Column<Contract> createdColumn = grid.addColumn(new LocalDateTimeRenderer<>(Contract::getCreated, "H:mm d. M. yyyy")).setHeader("Vytvořeno").setKey("created").setWidth("150px");
        Grid.Column<Contract> updatedColumn = grid.addColumn(new LocalDateTimeRenderer<>(Contract::getUpdated, "H:mm d. M. yyyy")).setHeader("Upraveno").setKey("updated").setWidth("150px");

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(clientColumn).setComponent(FilterFields.createContactMultiSelectComboBoxFilter("klienti", contractFilter::setClientsFilter, configurableFilterDataProvider, contactService));
        headerRow.getCell(stateColumn).setComponent(FilterFields.createStatusMultiSelectComboBoxFilter("stavy", contractFilter::setStatusFilter, configurableFilterDataProvider, statusService));
        headerRow.getCell(deadlineColumn).setComponent(FilterFields.createFromToDatePickerFilter(contractFilter::setFromDeadlineFilter, contractFilter::setToDeadlineFilter, configurableFilterDataProvider));
        headerRow.getCell(priceColumn).setComponent(FilterFields.createFromToNumberFilter(contractFilter::setFromPriceFilter, contractFilter::setToPriceFilter, configurableFilterDataProvider));
        headerRow.getCell(productsColumn).setComponent(FilterFields.createProductMultiSelectComboBoxFilter("produkty", contractFilter::setProductsFilter, configurableFilterDataProvider, productService));
        headerRow.getCell(createdColumn).setComponent(FilterFields.createFromToDatePickerFilter(contractFilter::setFromCreatedFilter, contractFilter::setToCreatedFilter, configurableFilterDataProvider));
        headerRow.getCell(updatedColumn).setComponent(FilterFields.createFromToDatePickerFilter(contractFilter::setFromUpdatedFilter, contractFilter::setToUpdatedFilter, configurableFilterDataProvider));


        grid.asSingleSelect().addValueChangeListener(e -> gridFormLayout.showFormLayout(e.getValue()));
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        grid.setSortableColumns("id", "price", "created", "updated");
        grid.sort(List.of(new GridSortOrder<>(createdColumn, SortDirection.DESCENDING)));

        updateGrid();
    }

    private Component getState(Contract contract) {
        Status status = contract.getCurrentDeadline().getStatus();

        Span badge = new Span(status.getStatus());
        badge.getElement().getThemeList().add("badge pill " + status.getTheme());

        return badge;
    }

    private Component getDeadline(Contract contract) {
        Span badge = new Span();

        LocalDate deadline = contract.getCurrentDeadline().getDeadline();
        if (deadline != null) {
            badge.setText(deadline.format(DateTimeFormatter.ofPattern("d. M. yyyy")));
            badge.getElement().getThemeList().add("badge pill");

            if (LocalDate.now().isAfter(deadline))
                badge.getElement().getThemeList().add(" error");

        } else {
            badge.setText("bez termínu");
            badge.getElement().getThemeList().add("badge pill contrast");
        }

        return badge;
    }

    private Component getProducts(Contract contract) {
        MultiSelectComboBox<Product> comboBox = new MultiSelectComboBox<>();
        comboBox.setItemLabelGenerator(Product::getName);
        comboBox.setReadOnly(true);
        comboBox.setItems(query -> productService.findAllByName(query.getPage(), query.getPageSize(), query.getFilter().orElse("")));
        comboBox.setValue(contract.getSelectedProducts());
        comboBox.setSizeFull();
        return comboBox;
    }

    private Span getClient(Contract contract) {
        return new Span(contract.getContact().getClient());
    }
    //endregion

    private TabWithBadge createTabWithBadge(String labelText, String style, DeadlineFilterTag tag) {
        TabWithBadge tabWithBadge = new TabWithBadge(labelText, "", style);

        tabWithBadge.getElement().addEventListener("click", event -> {
            contractFilter.setTagsFilter(tag);
            dataProvider.refreshAll();
        });

        return tabWithBadge;
    }

    private void updateGrid() {
        grid.setItems(configurableFilterDataProvider);

        all.badge.setText(String.valueOf(contractService.getCountAll()));
        withoutDeadline.badge.setText(String.valueOf(deadlineService.countWithoutDeadline()));
        beforeDeadline.badge.setText(String.valueOf(deadlineService.countBeforeDeadline()));
        afterDeadline.badge.setText(String.valueOf(deadlineService.countAfterDeadline()));
    }
}
