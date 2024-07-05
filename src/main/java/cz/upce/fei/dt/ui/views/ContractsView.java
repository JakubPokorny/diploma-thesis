package cz.upce.fei.dt.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.*;
import cz.upce.fei.dt.beckend.entities.Contract;
import cz.upce.fei.dt.beckend.entities.Deadline;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.Status;
import cz.upce.fei.dt.beckend.services.*;
import cz.upce.fei.dt.beckend.services.filters.ContractFilter;
import cz.upce.fei.dt.beckend.services.filters.ContractFilter.ContractFilterTag;
import cz.upce.fei.dt.ui.components.Badge;
import cz.upce.fei.dt.ui.components.GridFormLayout;
import cz.upce.fei.dt.ui.components.TabWithBadge;
import cz.upce.fei.dt.ui.components.filters.FilterFields;
import cz.upce.fei.dt.ui.components.filters.FromToLocalDateFilterFields;
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
public class ContractsView extends VerticalLayout implements HasUrlParameter<String> {
    private final ContractService contractService;
    private final ProductService productService;
    private final ContactService contactService;
    private final DeadlineService deadlineService;
    private final StatusService statusService;
    private final Grid<Contract> grid;
    private final GridFormLayout<ContractForm, Contract> gridFormLayout;
    private final ContractFilter contractFilter = new ContractFilter();
    private final TabWithBadge all = createContractTabWithBadge("Všechny", "contrast", ContractFilterTag.ALL);
    private final TabWithBadge success = createContractTabWithBadge("Hotové", "success", ContractFilterTag.SUCCESS);
    private final TabWithBadge contrast = createContractTabWithBadge("Čeká na akci", "contrast", ContractFilterTag.CONTRAST);
    private final TabWithBadge pending = createContractTabWithBadge("V procesu", "", ContractFilterTag.PENDING);
    private final TabWithBadge warning = createContractTabWithBadge("Pozor", "warning", ContractFilterTag.WARNING);
    private final TabWithBadge error = createContractTabWithBadge("Chyba", "error", ContractFilterTag.ERROR);
    private final TabWithBadge withoutDeadline = createContractTabWithBadge("Bez termínu", "contrast", ContractFilterTag.WITHOUT_DEADLINE);
    private final TabWithBadge beforeDeadline = createContractTabWithBadge("Před termínem", "", ContractFilterTag.BEFORE_DEADLINE);
    private final TabWithBadge afterDeadline = createContractTabWithBadge("Po termínu", "error", ContractFilterTag.AFTER_DEADLINE);
    private DataProvider<Contract, ContractFilter> dataProvider;
    private ConfigurableFilterDataProvider<Contract, Void, ContractFilter> configurableFilterDataProvider;

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (parameter == null)
            return;
        switch (parameter) {
            case "done" -> {
                contractFilter.setContractFilterTag(ContractFilterTag.SUCCESS);
                gridFormLayout.filterTabs.setSelectedTab(success);
            }
            case "in_progress" -> {
                contractFilter.setContractFilterTag(ContractFilterTag.PENDING);
                gridFormLayout.filterTabs.setSelectedTab(pending);
            }
            case "waiting_for_action" -> {
                contractFilter.setContractFilterTag(ContractFilterTag.CONTRAST);
                gridFormLayout.filterTabs.setSelectedTab(contrast);
            }
            case "warning" -> {
                contractFilter.setContractFilterTag(ContractFilterTag.WARNING);
                gridFormLayout.filterTabs.setSelectedTab(warning);
            }
            case "error" -> {
                contractFilter.setContractFilterTag(ContractFilterTag.ERROR);
                gridFormLayout.filterTabs.setSelectedTab(error);
            }
            case "without_deadline" -> {
                contractFilter.setContractFilterTag(ContractFilterTag.WITHOUT_DEADLINE);
                gridFormLayout.filterTabs.setSelectedTab(withoutDeadline);
            }
            case "before_deadline" -> {
                contractFilter.setContractFilterTag(ContractFilterTag.BEFORE_DEADLINE);
                gridFormLayout.filterTabs.setSelectedTab(beforeDeadline);
            }
            case "after_deadline" -> {
                contractFilter.setContractFilterTag(ContractFilterTag.AFTER_DEADLINE);
                gridFormLayout.filterTabs.setSelectedTab(afterDeadline);
            }
        }
    }

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
        gridFormLayout.filterTabs.add(all, success, contrast, pending, warning, error);
        gridFormLayout.filterTabs.add(withoutDeadline, beforeDeadline, afterDeadline);
        gridFormLayout.filterTabs.setSelectedIndex(0);
    }

    private void configureActions() {
        gridFormLayout.addButton.addClickListener(_ -> gridFormLayout.addNewValue(new Contract()));
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
//        contractFilter.setFromCreatedFilter(LocalDate.of(LocalDate.now().getYear(), 1,1));
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
        headerRow.getCell(deadlineColumn).setComponent(new FromToLocalDateFilterFields(contractFilter::setFromDeadlineFilter, contractFilter::setToDeadlineFilter, configurableFilterDataProvider).getFilterHeaderLayout());
        headerRow.getCell(priceColumn).setComponent(FilterFields.createFromToNumberFilter(contractFilter::setFromPriceFilter, contractFilter::setToPriceFilter, configurableFilterDataProvider));
        headerRow.getCell(productsColumn).setComponent(FilterFields.createProductMultiSelectComboBoxFilter("produkty", contractFilter::setProductsFilter, configurableFilterDataProvider, productService));

        FromToLocalDateFilterFields createdFromToDatePicker = new FromToLocalDateFilterFields(contractFilter::setFromCreatedFilter, contractFilter::setToCreatedFilter, configurableFilterDataProvider);
        createdFromToDatePicker.fromDatePicker.setValue(LocalDate.of(LocalDate.now().getYear(), 1, 1));
        headerRow.getCell(createdColumn).setComponent(createdFromToDatePicker.getFilterHeaderLayout());

        headerRow.getCell(updatedColumn).setComponent(new FromToLocalDateFilterFields(contractFilter::setFromUpdatedFilter, contractFilter::setToUpdatedFilter, configurableFilterDataProvider).getFilterHeaderLayout());

        grid.asSingleSelect().addValueChangeListener(e -> gridFormLayout.showFormLayout(e.getValue()));
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        grid.setSortableColumns("id", "price", "created", "updated");
        grid.sort(List.of(new GridSortOrder<>(createdColumn, SortDirection.DESCENDING)));

        idColumn.setVisible(false);
        updatedColumn.setVisible(false);
        gridFormLayout.showHideMenu.addColumnToggleItem("ID", idColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Klient", clientColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Stav", stateColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Termín", deadlineColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Cena s marží", priceColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Objednané produkty", productsColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Vytvořeno", createdColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Upraveno", updatedColumn);

        updateGrid();
    }

    private Component getState(Contract contract) {
        Status status = contract.getCurrentDeadline().getStatus();
        return new Badge(status.getStatus(), status.getTheme().getTheme());
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
        comboBox.setItems(productService::findAllByName);
        comboBox.setValue(contract.getSelectedProducts());
        comboBox.setSizeFull();
        return comboBox;
    }

    private Span getClient(Contract contract) {
        return new Span(contract.getContact().getClient());
    }
    //endregion

    private TabWithBadge createContractTabWithBadge(String labelText, String style, ContractFilterTag tag) {
        TabWithBadge tabWithBadge = new TabWithBadge(labelText, new Badge("", style));

        tabWithBadge.getElement().addEventListener("click", _ -> {
            contractFilter.setContractFilterTag(tag);
            dataProvider.refreshAll();
        });

        return tabWithBadge;
    }

    private void updateGrid() {
        grid.setItems(configurableFilterDataProvider);

        int success = 0, contrast = 0, pending = 0, warning = 0, error = 0;
        int withoutDeadline = 0, beforeDeadline = 0, afterDeadline = 0;
        for (Deadline deadline : deadlineService.findAllCurrentDeadlines()) {
            switch (deadline.getStatus().getTheme()) {
                case SUCCESS -> success++;
                case CONTRAST -> contrast++;
                case PENDING -> pending++;
                case WARNING -> warning++;
                case ERROR -> error++;
            }

            if (deadline.getDeadline() == null){
                withoutDeadline++;
            } else if (deadline.isBeforeOrNowDeadline()){
                beforeDeadline++;
            } else {
                afterDeadline++;
            }
        }

        this.all.badge.setText(String.valueOf(contractService.getCountAll()));
        this.withoutDeadline.setBadgeValue(withoutDeadline);
        this.beforeDeadline.setBadgeValue(beforeDeadline);
        this.afterDeadline.setBadgeValue(afterDeadline);
        this.success.setBadgeValue(success);
        this.contrast.setBadgeValue(contrast);
        this.pending.setBadgeValue(pending);
        this.warning.setBadgeValue(warning);
        this.error.setBadgeValue(error);
    }
}
