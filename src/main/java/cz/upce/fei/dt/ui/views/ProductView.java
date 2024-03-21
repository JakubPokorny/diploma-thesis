package cz.upce.fei.dt.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import cz.upce.fei.dt.beckend.entities.Component;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.services.ComponentService;
import cz.upce.fei.dt.beckend.services.ProductService;
import cz.upce.fei.dt.ui.components.GridFormLayout;
import cz.upce.fei.dt.ui.components.forms.ProductForm;
import cz.upce.fei.dt.ui.components.forms.events.DeleteEvent;
import cz.upce.fei.dt.ui.components.forms.events.SaveEvent;
import jakarta.annotation.security.PermitAll;

import java.util.List;

@Route(value = "products", layout = MainLayout.class)
@RouteAlias(value = "produkty",  layout = MainLayout.class)
@PageTitle("Produkty")
@PermitAll
public class ProductView extends VerticalLayout {
    private final ProductService productService;
    private final Grid<Product> grid;
    private final GridFormLayout<ProductForm, Product> gridFormLayout;
    private final List<Component> components;

    public ProductView(
            ProductService productService,
            ComponentService componentService) {

        this.productService = productService;
        components = componentService.findAllComponentsIdAndName();

        ProductForm form = new ProductForm(components);
        grid = new Grid<>(Product.class, false);
        gridFormLayout = new GridFormLayout<>(form, grid);
        MainLayout.setPageTitle("Produkty", ProductView.class);
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
        gridFormLayout.addSaveListener(this::saveComponent);
        gridFormLayout.addDeleteListener(this::deleteComponent);
    }
    //region events: save, delete
    private void deleteComponent(DeleteEvent deleteEvent) {
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

    private void saveComponent(SaveEvent saveEvent) {
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

        grid.addColumn(Product::getName).setHeader("Název");
        grid.addComponentColumn(this::createProductsComponent).setHeader("Komponenty").setWidth("150px");

        grid.addColumn(new LocalDateTimeRenderer<>(Product::getUpdated, "H:mm d. M. yyyy")).setHeader("Naposledy upraveno");


        grid.asSingleSelect().addValueChangeListener(e-> gridFormLayout.showFormLayout(e.getValue()));
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        updateGrid();
    }
    private MultiSelectComboBox<Component> createProductsComponent(Product product) {
        MultiSelectComboBox<Component> comboBox = new MultiSelectComboBox<>();
        comboBox.setItemLabelGenerator(Component::getName);
        comboBox.setReadOnly(true);
        comboBox.setItems(components);
        comboBox.setValue(product.getSelectedComponents());
        comboBox.setSizeFull();
        return comboBox;
    }
    //endregion
    private void updateGrid(){
        grid.setItems(query -> productService.findAll(query.getPage(), query.getPageSize()));
    }
}
