package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import cz.upce.fei.dt.beckend.entities.Component;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.services.ProductService;
import cz.upce.fei.dt.beckend.services.UserService;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class ComponentForm extends FormLayout implements IEditForm<Component> {
    private final Binder<Component> binder = new BeanValidationBinder<>(Component.class);
    private Component component;
    private final TextField name = new TextField("Název");
    private final TextArea description = new TextArea("Popis");
    private final IntegerField amount = new IntegerField("Skladem");
    private final IntegerField min  = new IntegerField("Minimálně skladem");
    private final ComboBox<User> notify = new ComboBox<>("Notifikovat");
    private final MultiSelectComboBox<Product> productsMSB = new MultiSelectComboBox<>("Produkty");
    private final HashMap<Long, ProductComponentForm> productComponentForms = new HashMap<>();

    public ComponentForm(ProductService productService, UserService userService) {
        setClassName("edit-form");

        binder.forField(name)
                .asRequired()
                .bind(Component::getName, Component::setName);

        description.setMaxLength(Component.MAX_DESCRIPTION_LENGTH);
        description.addValueChangeListener(
                event -> event.getSource().setHelperText(event.getValue().length() +"/"+ Component.MAX_DESCRIPTION_LENGTH)
        );
        binder.forField(description)
                .bind(Component::getDescription, Component::setDescription);

        amount.setStepButtonsVisible(true);
        amount.setMin(0);
        amount.setMax(Integer.MAX_VALUE);
        amount.setValue(0);
        binder.forField(amount)
                .withValidator(new IntegerRangeValidator("Skladem mimo hodnoty <0;4 294 967 296>", 0, Integer.MAX_VALUE ))
                .asRequired()
                .bind(Component::getAmount, Component::setAmount);

        min.setStepButtonsVisible(true);
        min.setMin(0);
        min.setMax(Integer.MAX_VALUE);
        binder.forField(min)
                .withValidator(new IntegerRangeValidator("Minimum mimo hodnoty <0;4 294 967 296>", 0, Integer.MAX_VALUE ))
                .bind(Component::getMin, Component::setMin);

        notify.setItems(userService.getAll());
        notify.setItemLabelGenerator(User::getFullName);
        //todo clear button for notify combobox
        binder.forField(notify)
                .bind(Component::getUser, Component::setUser);

        productsMSB.setItems(query -> productService.findAllProductsIdAndName(query.getPage(), query.getPageSize(), query.getFilter().orElse("")));
        productsMSB.setItemLabelGenerator(Product::getName);
        productsMSB.addSelectionListener(this::addProductComponentForm);

        this.add(name, description, amount, min, notify, productsMSB);
    }

    private void addProductComponentForm(MultiSelectionEvent<MultiSelectComboBox<Product>, Product> event) {
        event.getAddedSelection().forEach(product -> {
            if (!productComponentForms.containsKey(product.getId())){
                ProductComponentForm form = new ProductComponentForm(product);
                productComponentForms.put(product.getId(), form);
                this.add(form);
            }
        });
        event.getRemovedSelection().forEach(product ->
            remove(productComponentForms.remove(product.getId()))
        );
    }

    //region IEditForm
    @Override
    public Component getValue() {
        component.getProductComponents().clear();
        productComponentForms.forEach((key, form) ->
            component.getProductComponents().add(form.getValue())
        );
        return component;
    }

    @Override
    public void setValue(Component value) {
        productsMSB.clear();

        component = value;
        if (component != null){
            List<Product> selectedProducts = new ArrayList<>();
            component.getProductComponents().forEach(productComponent -> {
                selectedProducts.add(productComponent.getProduct());
                ProductComponentForm form = new ProductComponentForm(productComponent.getProduct());
                form.setValue(productComponent);
                productComponentForms.put(productComponent.getProduct().getId(), form);
                this.add(form);
            });
            productsMSB.setValue(selectedProducts);
        }
        binder.readBean(component);
    }

    @Override
    public void validate() throws ValidationException {
        binder.writeBean(component);
        for (ProductComponentForm form : productComponentForms.values()){
            form.getComponentCB().setValue(component);
            form.getProductComponent().setComponent(component);
            form.validate();
        }
    }

    @Override
    public void expand(boolean expended) {

    }
    //endregion
}
