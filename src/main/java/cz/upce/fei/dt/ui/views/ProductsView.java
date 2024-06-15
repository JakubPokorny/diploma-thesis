package cz.upce.fei.dt.ui.views;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import cz.upce.fei.dt.beckend.entities.Component;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.services.ComponentService;
import cz.upce.fei.dt.beckend.services.ProductService;
import cz.upce.fei.dt.beckend.services.filters.ProductFilter;
import cz.upce.fei.dt.ui.components.FilterFields;
import cz.upce.fei.dt.ui.components.GridFormLayout;
import cz.upce.fei.dt.ui.components.forms.ProductForm;
import cz.upce.fei.dt.ui.components.forms.events.DeleteEvent;
import cz.upce.fei.dt.ui.components.forms.events.SaveEvent;
import jakarta.annotation.security.PermitAll;

@Route(value = "products", layout = MainLayout.class)
@RouteAlias(value = "produkty",  layout = MainLayout.class)
@PageTitle("Produkty")
@PermitAll
public class ProductsView extends VerticalLayout {
    private final ProductService productService;
    private final ComponentService componentService;
    private final Grid<Product> grid;
    private final GridFormLayout<ProductForm, Product> gridFormLayout;
    private final ProductFilter productFilter = new ProductFilter();
    private ConfigurableFilterDataProvider<Product, Void, ProductFilter> configurableFilterDataProvider;

    public ProductsView(
            ProductService productService,
            ComponentService componentService) {

        this.productService = productService;
        this.componentService = componentService;

        ProductForm form = new ProductForm(componentService);
        grid = new Grid<>(Product.class, false);
        gridFormLayout = new GridFormLayout<>(form, grid);
        MainLayout.setPageTitle("Produkty", ProductsView.class);
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
        Button addContact = new Button("Přidat Produkt");
        addContact.addClickListener(event -> gridFormLayout.addNewValue(new Product()));
        gridFormLayout.getActionsLayout().add(addContact);
    }

    private void configureForm() {
        ComponentUtil.addListener(gridFormLayout, SaveEvent.class, this::saveProduct);
        ComponentUtil.addListener(gridFormLayout, DeleteEvent.class, this::deleteProduct);
    }
    //region events: save, delete
    private void deleteProduct(DeleteEvent deleteEvent) {
        try {
            Product product = (Product) deleteEvent.getValue();
            productService.deleteProduct(product);
            updateGrid();
            gridFormLayout.closeFormLayout();
            Notification.show("Produkt " + product.getName() + " odstraněn.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }catch (Exception exception){
            Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void saveProduct(SaveEvent saveEvent) {
        try {
            Product product = (Product) saveEvent.getValue();
            productService.saveProduct(product);
            updateGrid();
            gridFormLayout.closeFormLayout();
            Notification.show("Produkt " + product.getName() + " uložen.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }catch (Exception exception){
            Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    //endregion
    private void configureGrid() {
        grid.setClassName("grid-content");
        grid.setSizeFull();

        DataProvider<Product, ProductFilter> dataProvider = DataProvider.fromFilteringCallbacks(
                productService::fetchFromBackEnd,
                productService::sizeInBackEnd
        );

        configurableFilterDataProvider = dataProvider.withConfigurableFilter();
        configurableFilterDataProvider.setFilter(productFilter);

        Grid.Column<Product> nameColumn = grid.addColumn(Product::getName).setHeader("Název").setKey("name").setWidth("150px");
        Grid.Column<Product> productionPrice = grid.addColumn(this::getProductionPrice).setHeader("Výrobní cena").setKey("productionPrice").setWidth("150px");
        Grid.Column<Product> profitColumn = grid.addColumn(this::getProfit).setHeader("Marže").setKey("profit").setWidth("150px");
        Grid.Column<Product> sellingPrice = grid.addColumn(this::getSellingPrice).setHeader("Prodejní cena").setKey("sellingPrice").setWidth("150px");
        Grid.Column<Product> componentsColumn = grid.addComponentColumn(this::createProductsComponent).setHeader("Komponenty").setWidth("150px");

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(nameColumn).setComponent(FilterFields.createTextFieldFilter("název", productFilter::setNameFilter, configurableFilterDataProvider));
        headerRow.getCell(productionPrice).setComponent(FilterFields.createFromToNumberFilter(productFilter::setFromProductionPriceFilter, productFilter::setToProductionPriceFilter, configurableFilterDataProvider));
        headerRow.getCell(profitColumn).setComponent(FilterFields.createFromToNumberFilter(productFilter::setFromProfitFilter, productFilter::setToProfitFilter, configurableFilterDataProvider));
        headerRow.getCell(sellingPrice).setComponent(FilterFields.createFromToNumberFilter(productFilter::setFromSellingPriceFilter, productFilter::setToSellingPriceFilter, configurableFilterDataProvider));
        headerRow.getCell(componentsColumn).setComponent(FilterFields.createComponentMultiSelectComboBoxFilter("komponenty", productFilter::setComponentsFilter, configurableFilterDataProvider, componentService));

        grid.asSingleSelect().addValueChangeListener(e-> gridFormLayout.showFormLayout(e.getValue()));
//        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        grid.setSortableColumns("name", "productionPrice", "profit", "sellingPrice");

        updateGrid();
    }

    private String getSellingPrice(Product product) {
        return String.format("%.2f", product.getSellingPrice())+ "Kč";
    }

    private String getProfit(Product product) {
        return String.format("%.2f", product.getProfit())+ "%";
    }

    private String getProductionPrice(Product product) {
        return String.format("%.2f", product.getProductionPrice())+ "Kč";
    }

    private MultiSelectComboBox<Component> createProductsComponent(Product product) {
        MultiSelectComboBox<Component> comboBox = new MultiSelectComboBox<>();
        comboBox.setItemLabelGenerator(Component::getName);
        comboBox.setReadOnly(true);
        comboBox.setItems(query -> componentService.findAllByName(query.getPage(), query.getPageSize(), query.getFilter().orElse("")));
        comboBox.setValue(product.getSelectedComponents());
        comboBox.setSizeFull();
        return comboBox;
    }
    //endregion
    private void updateGrid(){
        grid.setItems(configurableFilterDataProvider);
    }
}
