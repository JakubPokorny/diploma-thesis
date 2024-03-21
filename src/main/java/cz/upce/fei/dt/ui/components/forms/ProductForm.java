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
import cz.upce.fei.dt.beckend.services.ComponentService;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class ProductForm extends FormLayout implements IEditForm<Product> {
    private final Binder<Product> binder = new BeanValidationBinder<>(Product.class);
    private Product product;
    private final TextField name = new TextField("Název");
    private final MultiSelectComboBox<Component> componentMCB = new MultiSelectComboBox<>("Komponenty");
    private final HashMap<Long, ProductComponentForm> productComponentForms = new HashMap<>();

    public ProductForm(ComponentService componentService) {
        setClassName("edit-form");


        binder.forField(name)
                .asRequired()
                .bind(Product::getName, Product::setName);

        componentMCB.setItems(query -> componentService.findAllComponentsIdAndName(query.getPage(), query.getPageSize(), query.getFilter().orElse("")));
        componentMCB.setItemLabelGenerator(Component::getName);
        componentMCB.addSelectionListener(this::addProductComponentForm);

        add(name, componentMCB);
    }

    private void addProductComponentForm(MultiSelectionEvent<MultiSelectComboBox<Component>, Component> event) {
        event.getAddedSelection().forEach(component -> {
            if (!productComponentForms.containsKey(component.getId())){
                ProductComponentForm form = new ProductComponentForm(component);
                productComponentForms.put(component.getId(), form);
                this.add(form);
            }
        });
        event.getRemovedSelection().forEach(component ->
                remove(productComponentForms.remove(component.getId()))
        );
    }

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
        componentMCB.clear();
        product = value;
        if (product != null){
            List<Component> selectedComponents = new ArrayList<>();
            product.getProductComponents().forEach(productComponent -> {
                selectedComponents.add(productComponent.getComponent());
                ProductComponentForm form = new ProductComponentForm(productComponent.getComponent());
                form.setValue(productComponent);
                productComponentForms.put(productComponent.getComponent().getId(), form);
                this.add(form);
            });
            componentMCB.setValue(selectedComponents);
        }
        binder.readBean(product);
    }

    @Override
    public void validate() throws ValidationException {
        binder.writeBean(product);
        for (ProductComponentForm form : productComponentForms.values()){
            form.getProductCB().setValue(product);
            form.getProductComponent().setProduct(product);
            form.validate();
        }

    }

    @Override
    public void expand(boolean expended) {

    }
    //endregion
}
