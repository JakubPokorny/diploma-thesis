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
import cz.upce.fei.dt.beckend.services.UserService;
import cz.upce.fei.dt.ui.components.GridFormLayout;
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
    private final Grid<Component> grid;
    private final GridFormLayout<ComponentForm, Component> gridFormLayout;


    public ComponentsView(ComponentService componentService,
                          UserService userService,
                          ProductService productService) {

        this.componentService = componentService;
        this.productService = productService;

        ComponentForm form = new ComponentForm(productService, userService);
        grid = new Grid<>(Component.class, false);
        gridFormLayout = new GridFormLayout<>(form, grid);
        MainLayout.setPageTitle("Komponenty", ComponentsView.class);
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

        grid.addColumn(Component::getName).setHeader("Název");
        grid.addColumn(Component::getDescription).setHeader("Popis");
        grid.addColumn(Component::getAmount).setHeader("Skladem");
        grid.addColumn(Component::getMin).setHeader("Minimum pro notifikaci");
        grid.addColumn(this::getFullName).setHeader("Notifikovat");
        grid.addComponentColumn(this::createProductsComponent).setHeader("Produkty").setWidth("150px");
        grid.addColumn(new LocalDateTimeRenderer<>(Component::getUpdated, "H:mm d. M. yyyy")).setHeader("Naposledy upraveno");

        grid.asSingleSelect().addValueChangeListener(e-> gridFormLayout.showFormLayout(e.getValue()));
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        updateGrid();
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

    //endregion

    private void updateGrid(){
        grid.setItems(query -> componentService.findAll(query.getPage(), query.getPageSize()));
    }
}
