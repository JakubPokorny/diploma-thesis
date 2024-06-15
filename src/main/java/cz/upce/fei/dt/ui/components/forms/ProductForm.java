package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import cz.upce.fei.dt.beckend.entities.Component;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.ProductComponent;
import cz.upce.fei.dt.beckend.entities.keys.ProductComponentKey;
import cz.upce.fei.dt.beckend.services.ComponentService;
import cz.upce.fei.dt.ui.components.PriceFieldWithButton;
import cz.upce.fei.dt.ui.components.forms.events.UpdateProductProductionPriceEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class ProductForm extends FormLayout implements IEditForm<Product> {
    private final Binder<Product> binder = new BeanValidationBinder<>(Product.class);
    private Product product;
    private final TextField name = new TextField("Název");
    private final PriceFieldWithButton productionPrice = new PriceFieldWithButton("Výrobní cena", VaadinIcon.REFRESH);
    private final NumberField profit = new NumberField("Marže");
    private final PriceFieldWithButton sellingPrice = new PriceFieldWithButton("Prodejní cena", VaadinIcon.EDIT);
    private final MultiSelectComboBox<Component> componentMSB = new MultiSelectComboBox<>("Komponenty");
    private final FormLayout productComponentsFormLayout = new FormLayout();
    private final HashMap<Long, ProductComponentForm> productComponentForms = new HashMap<>();

    public ProductForm(ComponentService componentService) {
        setClassName("edit-form");

        setupName();
        setupProductionPrice();
        setupProfit();
        setupSellingPrice();
        setupComponentMSB(componentService);
        setupProductComponentsForms();

        this.setResponsiveSteps(new ResponsiveStep("0", 1), new ResponsiveStep("600px", 3));
        this.setColspan(name, 2);
        this.setColspan(componentMSB, 3);
        this.setColspan(productComponentsFormLayout, 3);

        ComponentUtil.addListener(UI.getCurrent(), UpdateProductProductionPriceEvent.class, this::updateProductionPrice);

        add(name, productionPrice, profit, sellingPrice, componentMSB, productComponentsFormLayout);

    }

    //region Setups
    private void setupProductionPrice() {
        productionPrice.button.addClickListener(event -> updateProductionPrice(null));
        productionPrice.setReadOnly(true);
        binder.forField(productionPrice).withValidator(new DoubleRangeValidator("Výrobní cena mimo hodnoty", 0.0, Double.MAX_VALUE)).asRequired().bind(Product::getProductionPrice, Product::setProductionPrice);
    }

    private void updateProductionPrice(UpdateProductProductionPriceEvent updateProductionProductPriceEvent) {
        double totalProductionPrice = 0;
        for (ProductComponentForm form : productComponentForms.values()) {
            try {
                form.validate();
            } catch (ValidationException ex) {
                Notification.show(ex.getValidationErrors().toString()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            totalProductionPrice += form.getValue().getComponentsPerProduct() * form.getValue().getComponent().getPrice();
        }
        productionPrice.setValue(totalProductionPrice);
        updateSellingPrice();
    }

    private void setupProfit() {
        profit.setStepButtonsVisible(true);
        profit.setMin(0);
        profit.setMax(Double.MAX_VALUE);
        profit.setValue(15.0);
        profit.setSuffixComponent(new Span("%"));

        profit.addValueChangeListener(event -> updateSellingPrice());

        binder.forField(profit).withValidator(new DoubleRangeValidator("Profit mimo hodnoty", 0.0, Double.MAX_VALUE)).asRequired().bind(Product::getProfit, Product::setProfit);
    }

    private void setupSellingPrice() {
        sellingPrice.button.addClickListener(event -> {
            product.setOwnSellingPrice(sellingPrice.isReadOnly());
            sellingPrice.setReadOnly(!sellingPrice.isReadOnly());
            updateSellingPrice();
        });

        binder.forField(sellingPrice).withValidator(new DoubleRangeValidator("Prodejní cena mimo hodnoty", 0.0, Double.MAX_VALUE)).asRequired().bind(Product::getSellingPrice, Product::setSellingPrice);
    }

    private void updateSellingPrice() {
        if (sellingPrice.isReadOnly() && productionPrice.getValue() != null && profit.getValue() != null)
            sellingPrice.setValue(productionPrice.getValue() * (1 + (profit.getValue() / 100)));
    }

    private void setupComponentMSB(ComponentService componentService) {
        componentMSB.setItems(query -> componentService.findAllByName(query.getPage(), query.getPageSize(), query.getFilter().orElse("")));
        componentMSB.setItemLabelGenerator(Component::getName);
        componentMSB.setClearButtonVisible(true);
        componentMSB.addSelectionListener(this::updateProductComponentForms);
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

    private void updateProductComponentForms(MultiSelectionEvent<MultiSelectComboBox<Component>, Component> event) {
        event.getAddedSelection().forEach(component -> {
            if (!productComponentForms.containsKey(component.getId())) {
                ProductComponentForm form = new ProductComponentForm(ProductComponent.builder()
                        .id(new ProductComponentKey(product.getId(), component.getId()))
                        .componentsPerProduct(1)
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
        updateProductionPrice(null);
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
                            new ProductComponentForm(productComponent, "Počet pro: " + productComponent.getComponent().getName())
                    )
            );
            componentMSB.setValue(product.getSelectedComponents());
            sellingPrice.setReadOnly(!product.getOwnSellingPrice());
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
