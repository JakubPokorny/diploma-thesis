package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import cz.upce.fei.dt.beckend.entities.Component;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.ProductComponent;
import cz.upce.fei.dt.beckend.entities.keys.ProductComponentKey;
import cz.upce.fei.dt.beckend.services.ComponentService;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class ProductForm extends FormLayout implements IEditForm<Product> {
    private final Binder<Product> binder = new BeanValidationBinder<>(Product.class);
    private Product product;
    private final TextField name = new TextField("Název");
    private final MultiSelectComboBox<Component> componentMSB = new MultiSelectComboBox<>("Komponenty");
    private final FormLayout productComponentsFormLayout = new FormLayout();
    private final HashMap<Long, ProductComponentForm> productComponentForms = new HashMap<>();

    public ProductForm(ComponentService componentService) {
        setClassName("edit-form");

        setupName();
        setupComponentMSB(componentService);
        setupProductComponentsForms();

        this.setColspan(name, 2);
        this.setColspan(componentMSB, 2);
        this.setColspan(productComponentsFormLayout, 2);
        add(name, componentMSB, productComponentsFormLayout);
    }


    //region Setups
    private void setupComponentMSB(ComponentService componentService) {
        componentMSB.setItems(query -> componentService.findAllComponentsIdAndName(query.getPage(), query.getPageSize(), query.getFilter().orElse("")));
        componentMSB.setItemLabelGenerator(Component::getName);
        componentMSB.setClearButtonVisible(true);
        componentMSB.addSelectionListener(this::addProductComponentForm);
    }

    private void setupName() {
        binder.forField(name)
                .asRequired()
                .bind(Product::getName, Product::setName);
    }

    private void setupProductComponentsForms() {
        productComponentsFormLayout.setWidthFull();
        productComponentsFormLayout.setClassName("product-components-layout");
        productComponentsFormLayout.setResponsiveSteps(
                new ResponsiveStep("0", 1),
                new ResponsiveStep("300px", 2),
                new ResponsiveStep("450px", 3),
                new ResponsiveStep("600px", 4),
                new ResponsiveStep("750px", 5),
                new ResponsiveStep("900px", 6)
        );
    }

    private void addProductComponentForm(MultiSelectionEvent<MultiSelectComboBox<Component>, Component> event) {
        event.getAddedSelection().forEach(component -> {
            if (!productComponentForms.containsKey(component.getId())) {
                ProductComponentForm form = new ProductComponentForm(ProductComponent.builder()
                        .id(new ProductComponentKey(product.getId(), component.getId()))
                        .amount(1)
                        .product(product)
                        .component(component)
                        .build(),
                        "Počet pro " + component.getName()
                        );
                productComponentForms.put(component.getId(), form);
                productComponentsFormLayout.add(form);
            } else {
                productComponentsFormLayout.add(productComponentForms.get(component.getId()));
            }
        });

        event.getRemovedSelection().forEach(component ->
                productComponentsFormLayout.remove(productComponentForms.remove(component.getId()))
        );
    }
    //endregion

    //region IEditForm
    @Override
    public Product getValue() {
        product.getProductComponents().clear();
        productComponentForms.forEach((key, form) ->
                product.getProductComponents().add(form.getValue()));
        return product;
    }

    @Override
    public void setValue(Product value) {
        componentMSB.clear();
        product = value;

        if (product != null) {
            product.getProductComponents().forEach(productComponent ->
                    productComponentForms.put(
                            productComponent.getId().getComponentId(),
                            new ProductComponentForm(productComponent, "Počet pro: "+ productComponent.getComponent().getName())
                    )
            );
            componentMSB.setValue(product.getSelectedComponents());
        }
        binder.readBean(product);
    }

    @Override
    public void validate() throws ValidationException {
        binder.writeBean(product);
        for (ProductComponentForm form : productComponentForms.values()) {
            form.validate();
        }

    }

    @Override
    public void expand(boolean expended) {

    }
    //endregion
}
