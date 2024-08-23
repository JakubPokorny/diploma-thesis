package cz.upce.fei.dt.ui.views;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import cz.upce.fei.dt.backend.entities.Component;
import cz.upce.fei.dt.backend.entities.Product;
import cz.upce.fei.dt.backend.entities.Product_;
import cz.upce.fei.dt.backend.services.ComponentService;
import cz.upce.fei.dt.backend.services.ProductService;
import cz.upce.fei.dt.backend.services.filters.ProductFilter;
import cz.upce.fei.dt.ui.components.GridFormLayout;
import cz.upce.fei.dt.ui.components.filters.FilterFields;
import cz.upce.fei.dt.ui.components.filters.FromToLocalDateFilterFields;
import cz.upce.fei.dt.ui.components.forms.ProductForm;
import cz.upce.fei.dt.ui.components.forms.events.DeleteEvent;
import cz.upce.fei.dt.ui.components.forms.events.SaveEvent;
import jakarta.annotation.security.PermitAll;
import org.hibernate.exception.ConstraintViolationException;

@Route(value = "products", layout = MainLayout.class)
@RouteAlias(value = "produkty", layout = MainLayout.class)
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
        gridFormLayout.addButton.addClickListener(_ -> gridFormLayout.addNewValue(new Product()));
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
        } catch (Exception e) {
            if (e.getCause() instanceof ConstraintViolationException cve) {
                if ("23000".equals(cve.getSQLState())) {
                    throw new IllegalStateException("produkt nelze smazat dokud je přiřazen k zakázkám.", e);
                }
            }
            throw e;
        }
    }

    private void saveProduct(SaveEvent saveEvent) {
        try {
            Product product = (Product) saveEvent.getValue();
            productService.saveProduct(product);
            updateGrid();
            gridFormLayout.closeFormLayout();
            Notification.show("Produkt " + product.getName() + " uložen.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception exception) {
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

        Grid.Column<Product> nameColumn = grid.addColumn(Product::getName).setHeader("Název").setKey(Product_.NAME).setWidth("150px");
        Grid.Column<Product> productionPrice = grid.addColumn(this::getProductionPrice).setHeader("Výrobní cena").setKey(Product_.PRODUCTION_PRICE).setWidth("150px");
        Grid.Column<Product> profitColumn = grid.addColumn(this::getProfit).setHeader("Marže").setKey(Product_.PROFIT).setWidth("150px");
        Grid.Column<Product> sellingPrice = grid.addColumn(this::getSellingPrice).setHeader("Prodejní cena").setKey(Product_.SELLING_PRICE).setWidth("150px");
        Grid.Column<Product> componentsColumn = grid.addComponentColumn(this::createProductsComponent).setHeader("Komponenty").setWidth("150px");
        Grid.Column<Product> createdColumn = grid.addColumn(new LocalDateTimeRenderer<>(Product::getCreated, "H:mm d. M. yyyy")).setHeader("Vytvořeno").setKey(Product_.CREATED).setWidth("150px");
        Grid.Column<Product> updatedColumn = grid.addColumn(new LocalDateTimeRenderer<>(Product::getUpdated, "H:mm d. M. yyyy")).setHeader("Upraveno").setKey(Product_.UPDATED).setWidth("150px");

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(nameColumn).setComponent(FilterFields.createTextFieldFilter("název", productFilter::setNameFilter, configurableFilterDataProvider));
        headerRow.getCell(productionPrice).setComponent(FilterFields.createFromToNumberFilter(productFilter::setFromProductionPriceFilter, productFilter::setToProductionPriceFilter, configurableFilterDataProvider));
        headerRow.getCell(profitColumn).setComponent(FilterFields.createFromToNumberFilter(productFilter::setFromProfitFilter, productFilter::setToProfitFilter, configurableFilterDataProvider));
        headerRow.getCell(sellingPrice).setComponent(FilterFields.createFromToNumberFilter(productFilter::setFromSellingPriceFilter, productFilter::setToSellingPriceFilter, configurableFilterDataProvider));
        headerRow.getCell(componentsColumn).setComponent(FilterFields.createComponentMultiSelectComboBoxFilter("komponenty", productFilter::setComponentsFilter, configurableFilterDataProvider, componentService));
        headerRow.getCell(createdColumn).setComponent(new FromToLocalDateFilterFields(productFilter::setFromCreatedFilter, productFilter::setToCreatedFilter, configurableFilterDataProvider).getFilterHeaderLayout());
        headerRow.getCell(updatedColumn).setComponent(new FromToLocalDateFilterFields(productFilter::setFromUpdatedFilter, productFilter::setToUpdatedFilter, configurableFilterDataProvider).getFilterHeaderLayout());

        grid.asSingleSelect().addValueChangeListener(e -> gridFormLayout.showFormLayout(e.getValue()));
//        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        grid.setSortableColumns(Product_.NAME, Product_.PRODUCTION_PRICE, Product_.PROFIT, Product_.SELLING_PRICE, Product_.CREATED, Product_.UPDATED);

        createdColumn.setVisible(false);
        updatedColumn.setVisible(false);
        gridFormLayout.showHideMenu.addColumnToggleItem("Název", nameColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Výrobní cena", productionPrice);
        gridFormLayout.showHideMenu.addColumnToggleItem("Marže", profitColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Prodejní cena", sellingPrice);
        gridFormLayout.showHideMenu.addColumnToggleItem("Komponenty", componentsColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Vytvořeno", createdColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Upraveno", updatedColumn);

        updateGrid();
    }

    private String getSellingPrice(Product product) {
        return String.format("%s", Math.round(product.getSellingPrice())) + "Kč";
    }

    private String getProfit(Product product) {
        return String.format("%.2f", product.getProfit()) + "%";
    }

    private String getProductionPrice(Product product) {
        return String.format("%s", Math.round(product.getProductionPrice())) + "Kč";
    }

    private MultiSelectComboBox<Component> createProductsComponent(Product product) {
        MultiSelectComboBox<Component> comboBox = new MultiSelectComboBox<>();
        comboBox.setItemLabelGenerator(Component::getName);
        comboBox.setReadOnly(true);
        comboBox.setItems(componentService::findAllByName);
        comboBox.setValue(product.getSelectedComponents());
        comboBox.setSizeFull();
        return comboBox;
    }

    //endregion
    private void updateGrid() {
        grid.setItems(configurableFilterDataProvider);
    }
}
