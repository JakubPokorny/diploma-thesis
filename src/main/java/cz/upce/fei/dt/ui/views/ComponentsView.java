package cz.upce.fei.dt.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import cz.upce.fei.dt.beckend.entities.Component;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.services.ComponentService;
import cz.upce.fei.dt.beckend.services.ProductService;
import cz.upce.fei.dt.beckend.services.UserService;
import cz.upce.fei.dt.beckend.services.filters.ComponentFilter;
import cz.upce.fei.dt.beckend.services.filters.ComponentTag;
import cz.upce.fei.dt.ui.components.FilterFields;
import cz.upce.fei.dt.ui.components.GridFormLayout;
import cz.upce.fei.dt.ui.components.TabWithBadge;
import cz.upce.fei.dt.ui.components.forms.ComponentForm;
import cz.upce.fei.dt.ui.components.forms.events.DeleteEvent;
import cz.upce.fei.dt.ui.components.forms.events.SaveEvent;
import jakarta.annotation.security.PermitAll;

@Route(value = "components", layout = MainLayout.class)
@RouteAlias(value = "komponenty", layout = MainLayout.class)
@PageTitle("Komponenty")
@PermitAll
public class ComponentsView extends VerticalLayout {
    private final ComponentService componentService;
    private final ProductService productService;
    private final UserService userService;
    private final Grid<Component> grid;
    private final GridFormLayout<ComponentForm, Component> gridFormLayout;

    private final ComponentFilter componentFilter = new ComponentFilter();
    private final TabWithBadge all = createTabWithBadge("Všechny", "contrast", ComponentTag.ALL);
    private final TabWithBadge inStock = createTabWithBadge("Skladem", "success", ComponentTag.IN_STOCK);
    private final TabWithBadge supply = createTabWithBadge("Doplnit", "", ComponentTag.SUPPLY);
    private final TabWithBadge missing = createTabWithBadge("Chybí", "error", ComponentTag.MISSING);
    private DataProvider<Component, ComponentFilter> dataProvider;
    private ConfigurableFilterDataProvider<Component, Void, ComponentFilter> configurableFilterDataProvider;


    public ComponentsView(ComponentService componentService,
                          UserService userService,
                          ProductService productService) {

        this.componentService = componentService;
        this.productService = productService;
        this.userService = userService;

        ComponentForm form = new ComponentForm(productService, userService);
        grid = new Grid<>(Component.class, false);
        gridFormLayout = new GridFormLayout<>(form, grid);
        MainLayout.setPageTitle("Komponenty", ComponentsView.class);
        setSizeFull();

        configureGrid();
        configureForm();
        configureFilters();
        configureActions();

        add(gridFormLayout);
    }

    //region configures: grid, form, actions, filters, events
    private void configureFilters() {
//        all = createTabWithBadge("Všechny", "contrast", ComponentTag.ALL);
//        inStock = createTabWithBadge("Skladem", componentService.getCountInStock(), "success", ComponentTag.IN_STOCK);
//        supply = createTabWithBadge("Doplnit", componentService.getCountInStockSupply(), "", ComponentTag.SUPPLY);
//        missing = createTabWithBadge("Chybí", componentService.getCountInStockMissing(), "error", ComponentTag.MISSING);

        Tabs tabs = new Tabs(all, inStock, supply, missing);
        tabs.setClassName("tabs");
        tabs.setMaxWidth("100%");
        //tabs.setWidth("350px");
        tabs.setSelectedIndex(0);

        gridFormLayout.getFiltersLayout().add(tabs);
    }

    private void configureActions() {
        Button addContact = new Button("Přidat komponentu");
        addContact.addClickListener(event -> gridFormLayout.addNewValue(new Component()));
        gridFormLayout.getActionsLayout().add(addContact);

    }

    private void configureForm() {
        gridFormLayout.addSaveListener(this::saveComponent);
        gridFormLayout.addDeleteListener(this::deleteComponent);
    }


    //region events: save, delete
    private void deleteComponent(DeleteEvent deleteEvent) {
        try {
            Component component = (Component) deleteEvent.getValue();
            componentService.deleteComponent(component);
            updateGrid();
            gridFormLayout.closeFormLayout();
            Notification.show("Komponenta " + component.getName() + " odstraněna.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception exception) {
            Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void saveComponent(SaveEvent saveEvent) {
        try {
            Component component = (Component) saveEvent.getValue();
            componentService.saveComponent(component);
            updateGrid();
            gridFormLayout.closeFormLayout();
            Notification.show("Komponenta " + component.getName() + " uložena.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception exception){
            Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    //endregion
    private void configureGrid() {
        grid.setClassName("grid-content");
        grid.setSizeFull();

       dataProvider = DataProvider.fromFilteringCallbacks(
                componentService::findAll,
                componentService::getCount
        );

        configurableFilterDataProvider = dataProvider.withConfigurableFilter();
        configurableFilterDataProvider.setFilter(componentFilter);

        Grid.Column<Component> nameColumn = grid.addColumn(Component::getName).setHeader("Název").setKey("name").setWidth("150px");
        Grid.Column<Component> descriptionColumn = grid.addColumn(Component::getDescription).setHeader("Popis").setKey("description").setWidth("150px");
        Grid.Column<Component> inStockColumn = grid.addComponentColumn(this::createInStockBadge).setHeader("Skladem").setKey("inStock").setWidth("150px");
        Grid.Column<Component> minInStockColumn = grid.addColumn(Component::getMinInStock).setHeader("Minimum pro notifikaci").setKey("minInStock").setWidth("150px");
        Grid.Column<Component> userColumn = grid.addColumn(this::getFullName).setHeader("Notifikovat").setWidth("150px");
        Grid.Column<Component> productsColumn = grid.addComponentColumn(this::createProductsComponent).setHeader("Produkty").setWidth("150px");
        Grid.Column<Component> updatedColumn = grid.addColumn(new LocalDateTimeRenderer<>(Component::getUpdated, "H:mm d. M. yyyy")).setHeader("Naposledy upraveno").setKey("updated").setWidth("200px");


        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(nameColumn).setComponent(FilterFields.createTextFieldFilter( "název", componentFilter::setNameFilter,configurableFilterDataProvider));
        headerRow.getCell(descriptionColumn).setComponent(FilterFields.createTextFieldFilter( "popis", componentFilter::setDescriptionFilter,configurableFilterDataProvider));
        headerRow.getCell(inStockColumn).setComponent(FilterFields.createFromToIntegerFilter(componentFilter::setFromInStockFilter, componentFilter::setToInStockFilter, configurableFilterDataProvider));
        headerRow.getCell(minInStockColumn).setComponent(FilterFields.createFromToIntegerFilter(componentFilter::setFromMinInStockFilter, componentFilter::setToMinInStockFilter, configurableFilterDataProvider));
        headerRow.getCell(productsColumn).setComponent(FilterFields.createProductMultiSelectComboBoxFilter("produkty", componentFilter::setProductsFilter, configurableFilterDataProvider, productService));
        headerRow.getCell(userColumn).setComponent(FilterFields.createUserMultiSelectComboBoxFilter("uživatelé", componentFilter::setUsersFilter, configurableFilterDataProvider, userService));
        headerRow.getCell(updatedColumn).setComponent(FilterFields.createFromToDateTimePickerFilter(componentFilter::setFromUpdatedFilter, componentFilter::setToUpdatedFilter, configurableFilterDataProvider));

        grid.asSingleSelect().addValueChangeListener(e-> gridFormLayout.showFormLayout(e.getValue()));
        //grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        grid.setSortableColumns("name", "description", "inStock", "minInStock", "updated");

        updateGrid();
    }

    private Span createInStockBadge(Component component) {
        int inStock = component.getInStock();
        Span badge = new Span(String.valueOf(component.getInStock()));
        badge.getElement().getThemeList().add("badge pill ");

        if (inStock < 0)
            badge.getElement().getThemeList().add("error");
        else if (component.getMinInStock() == null)
            badge.getElement().getThemeList().add("contrast");
        else if (inStock > component.getMinInStock())
            badge.getElement().getThemeList().add("success");

        return badge;
    }

    private String getFullName(Component component) {
        return component.getUser() != null ? component.getUser().getFullName() : "";
    }


    private MultiSelectComboBox<Product> createProductsComponent(Component component) {
        MultiSelectComboBox<Product> comboBox = new MultiSelectComboBox<>();
        comboBox.setItemLabelGenerator(Product::getName);
        comboBox.setReadOnly(true);
        comboBox.setItems(query -> productService.findAllProductsIdAndName(query.getPage(), query.getPageSize(), query.getFilter().orElse("")));
        comboBox.setValue(component.getSelectedProduct());
        comboBox.setSizeFull();
        return comboBox;
    }

    private TabWithBadge createTabWithBadge(String labelText, String style, ComponentTag tag){
        TabWithBadge tabWithBadge = new TabWithBadge(labelText, "", style);

        tabWithBadge.getElement().addEventListener("click", event -> {
            componentFilter.setTagFilter(tag);
            dataProvider.refreshAll();
        });

        return tabWithBadge;
    }

    //endregion

    private void updateGrid(){
        grid.setItems(configurableFilterDataProvider);

        all.badge.setText(String.valueOf(componentService.getCountAll()));
        inStock.badge.setText(String.valueOf(componentService.getCountInStock()));
        supply.badge.setText(String.valueOf(componentService.getCountInStockSupply()));
        missing.badge.setText(String.valueOf(componentService.getCountInStockMissing()));
    }
}
