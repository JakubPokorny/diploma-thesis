package cz.upce.fei.dt.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import cz.upce.fei.dt.beckend.entities.Contract;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.services.*;
import cz.upce.fei.dt.ui.components.GridFormLayout;
import cz.upce.fei.dt.ui.components.forms.ContractForm;
import cz.upce.fei.dt.ui.components.forms.events.DeleteEvent;
import cz.upce.fei.dt.ui.components.forms.events.SaveEvent;
import jakarta.annotation.security.PermitAll;

@Route(value = "contracts", layout = MainLayout.class)
@RouteAlias(value = "Zakázky", layout = MainLayout.class)
@PageTitle("Zakázky")
@PermitAll
public class ContractsView extends VerticalLayout {
    private final ContractService contractService;
    private final ProductService productService;
    private final Grid<Contract> grid;
    private final GridFormLayout<ContractForm, Contract> gridFormLayout;

    public ContractsView(
            ContractService contractService,
            ContactService contactService,
            ProductService productService,
            NoteService noteService,
            FileService fileService) {
        this.contractService = contractService;
        this.productService = productService;

        ContractForm form = new ContractForm(contactService, productService, noteService, fileService);
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
    }

    private void configureActions() {
        Button addContract = new Button("Přidat Zakázku");
        addContract.addClickListener(event -> gridFormLayout.addNewValue(new Contract()));
        gridFormLayout.getActionsLayout().add(addContract);
    }

    private void configureForm() {
        gridFormLayout.addSaveListener(this::saveContract);
        gridFormLayout.addDeleteListener(this::deleteContract);
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

        grid.addColumn(Contract::getId).setHeader("ID");
        grid.addComponentColumn(this::getClient).setHeader("Klient");
        grid.addComponentColumn(this::getProducts).setHeader("Objednané produkty").setWidth("150px");
        //grid.addComponentColumn(this::getDeadlines).setHeader("Stav");
        //grid.addComponentColumn(this::getNotes).setHeader("Poznámky");
        //grid.addComponentColumn(this::getFiles).setHeader("Soubory");

        grid.asSingleSelect().addValueChangeListener(e -> gridFormLayout.showFormLayout(e.getValue()));
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        updateGrid();
    }

    private Component getFiles(Contract contract) {
        return new Span(contract.getFiles().toString());
    }

    private Component getNotes(Contract contract) {
        return new Span(contract.getNotes().toString());
    }

    private Component getDeadlines(Contract contract) {
        return new Span(contract.getDeadlines().toString());
    }

    private Component getProducts(Contract contract) {
        MultiSelectComboBox<Product> comboBox = new MultiSelectComboBox<>();
        comboBox.setItemLabelGenerator(Product::getName);
        comboBox.setReadOnly(true);
        comboBox.setItems(query -> productService.findAllProductsIdAndName(query.getPage(), query.getPageSize(), query.getFilter().orElse("")));
        comboBox.setValue(contract.getSelectedProducts());
        comboBox.setSizeFull();
        return comboBox;
    }

    private Span getClient(Contract contract) {
        return new Span(contract.getContact().getName());
    }
    //endregion

    private void updateGrid() {
        grid.setItems(query -> contractService.findAll(query.getPage(), query.getPageSize()));
    }
}
