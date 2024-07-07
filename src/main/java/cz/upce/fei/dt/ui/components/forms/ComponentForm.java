package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import cz.upce.fei.dt.beckend.entities.Component;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.ProductComponent;
import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.entities.keys.ProductComponentKey;
import cz.upce.fei.dt.beckend.services.ProductService;
import cz.upce.fei.dt.beckend.services.UserService;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class ComponentForm extends FormLayout implements IEditForm<Component> {
    private final Binder<Component> binder = new BeanValidationBinder<>(Component.class);
    private Component component;
    private final TextField name = new TextField("Název");
    private final TextArea description = new TextArea("Popis");
    private final IntegerField inStock = new IntegerField("Skladem");
    private final IntegerField minInStock = new IntegerField("Minimálně skladem");
    private final NumberField price = new NumberField("Cena");
    private final ComboBox<User> notify = new ComboBox<>("Notifikovat");
    private final MultiSelectComboBox<Product> productsMSB = new MultiSelectComboBox<>("Produkty");
    private final FormLayout productComponentsFormLayout = new FormLayout();
    private final HashMap<Long, ProductComponentForm> productComponentForms = new HashMap<>();

    public ComponentForm(ProductService productService, UserService userService) {
        setClassName("edit-form");

        setupName();
        setupDescription();
        setupInStock();
        setupMinInStock();
        setupPrice();
        setupNotify(userService);
        setupProductMSB(productService);
        setupProductComponentsForms();

        this.setResponsiveSteps(new ResponsiveStep("0", 1), new ResponsiveStep("600px", 4));
        this.setColspan(name, 4);
        this.setColspan(description, 4);
        this.setColspan(productsMSB, 4);
        this.setColspan(productComponentsFormLayout, 4);
        this.add(name, description, inStock, minInStock, price, notify, productsMSB, productComponentsFormLayout);
    }


    //region Setups

    private void setupNotify(UserService userService) {
        notify.setItems(userService::findAllByFirstnameAndLastnameAndEmail);
        notify.setItemLabelGenerator(user -> user.getFullName() + ", " + user.getEmail());
        notify.setClearButtonVisible(true);
        binder.forField(notify).bind(Component::getUser, Component::setUser);
    }

    private void setupPrice() {
        price.setStepButtonsVisible(true);
        price.setMin(0);
        price.setMax(Double.MAX_VALUE);
        price.setSuffixComponent(new Span("Kč"));
        binder.forField(price).withValidator(new DoubleRangeValidator("Minimum mimo hodnoty", 0.0, Double.MAX_VALUE)).asRequired().bind(Component::getPrice, Component::setPrice);
    }

    private void setupMinInStock() {
        minInStock.setStepButtonsVisible(true);
        minInStock.setMin(Integer.MIN_VALUE);
        minInStock.setMax(Integer.MAX_VALUE);
        minInStock.setValue(0);
        minInStock.setSuffixComponent(new Span("ks"));
        binder.forField(minInStock)
                .asRequired()
                .withValidator(new IntegerRangeValidator("Minimum mimo hodnoty <-2 147 483 648; 2 147 483 647>", Integer.MIN_VALUE, Integer.MAX_VALUE))
                .bind(Component::getMinInStock, Component::setMinInStock);
    }

    private void setupInStock() {
        inStock.setStepButtonsVisible(true);
        inStock.setMin(Integer.MIN_VALUE);
        inStock.setMax(Integer.MAX_VALUE);
        inStock.setValue(0);
        inStock.setSuffixComponent(new Span("ks"));
        binder.forField(inStock)
                .withValidator(new IntegerRangeValidator("Skladem mimo hodnoty <-2 147 483 648; 2 147 483 647>", Integer.MIN_VALUE, Integer.MAX_VALUE))
                .asRequired()
                .bind(Component::getInStock, Component::setInStock);
    }

    private void setupDescription() {
        description.setMaxLength(Component.MAX_DESCRIPTION_LENGTH);
        description.addValueChangeListener(event -> event.getSource().setHelperText(event.getValue().length() + "/" + Component.MAX_DESCRIPTION_LENGTH));
        binder.forField(description).bind(Component::getDescription, Component::setDescription);
    }

    private void setupName() {
        binder.forField(name).asRequired().bind(Component::getName, Component::setName);
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
                new ResponsiveStep("900px", 6));
    }

    private void setupProductMSB(ProductService productService) {
        productsMSB.setItems(productService::findAllByName);
        productsMSB.setItemLabelGenerator(Product::getName);
        productsMSB.setClearButtonVisible(true);
        productsMSB.addSelectionListener(this::addProductComponentForm);
    }

    private void addProductComponentForm(MultiSelectionEvent<MultiSelectComboBox<Product>, Product> event) {
        event.getAddedSelection().forEach(product -> {
            if (!productComponentForms.containsKey(product.getId())) {
                ProductComponentForm form = new ProductComponentForm(ProductComponent.builder()
                        .id(new ProductComponentKey(product.getId(), component.getId()))
                        .componentsPerProduct(1)
                        .product(product)
                        .component(component)
                        .build(), "Počet pro " + product.getName());
                productComponentForms.put(product.getId(), form);
                productComponentsFormLayout.add(form);
            } else {
                productComponentsFormLayout.add(productComponentForms.get(product.getId()));
            }
        });

        event.getRemovedSelection().forEach(product -> productComponentsFormLayout.remove(productComponentForms.remove(product.getId())));
    }

    //endregion

    //region IEditForm
    @Override
    public Component getValue() {
        component.getProductComponents().clear();
        productComponentForms.forEach((_, form) -> component.getProductComponents().add(form.getValue()));
        return component;
    }

    @Override
    public void setValue(Component value) {
        productsMSB.clear();
        component = value;

        if (component != null) {
            component.getProductComponents().forEach(productComponent -> productComponentForms.put(productComponent.getId().getProductId(), new ProductComponentForm(productComponent, "Počet pro: " + productComponent.getProduct().getName())));
            productsMSB.setValue(component.getSelectedProduct());
        }
        binder.readBean(component);
    }

    @Override
    public void validate() throws ValidationException {
        binder.writeBean(component);
        for (ProductComponentForm form : productComponentForms.values()) {
            form.validate();
        }
    }

    @Override
    public void expand(boolean expended) {

    }
    //endregion
}
