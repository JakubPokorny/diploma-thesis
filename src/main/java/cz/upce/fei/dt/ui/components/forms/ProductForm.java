package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import cz.upce.fei.dt.backend.entities.Component;
import cz.upce.fei.dt.backend.entities.Product;
import cz.upce.fei.dt.backend.entities.ProductComponent;
import cz.upce.fei.dt.backend.entities.keys.ProductComponentKey;
import cz.upce.fei.dt.backend.services.ComponentService;
import cz.upce.fei.dt.ui.components.forms.events.UpdateProductProductionPriceEvent;
import cz.upce.fei.dt.ui.components.forms.fields.PriceField;
import cz.upce.fei.dt.ui.utilities.CustomComponent;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class ProductForm extends FormLayout implements IEditForm<Product> {
    private final Binder<Product> binder = new BeanValidationBinder<>(Product.class);
    private Product product;
    private final TextField name = new TextField("Název");
    private final PriceField productionPriceField = new PriceField("Výrobní cena");
    private final PriceField sellingPriceField = new PriceField("Prodejní cena");
    private final PriceField profitField = new PriceField("Marže");
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
        this.setColspan(name, 3);
        this.setColspan(componentMSB, 3);
        this.setColspan(productComponentsFormLayout, 3);

        ComponentUtil.addListener(UI.getCurrent(), UpdateProductProductionPriceEvent.class, this::updateProductionPrice);

        add(name, productionPriceField, profitField, sellingPriceField, componentMSB, productComponentsFormLayout);

    }

    //region Setups
    private void setupProductionPrice() {
        productionPriceField.setReadOnly(true);
        Button updateProductionPriceField = CustomComponent.createSmallTertiaryButton(
                VaadinIcon.REFRESH.create(),
                _ -> updateProductionPrice(null)
        );
        productionPriceField.suffixLayout.add(updateProductionPriceField);

        binder.forField(productionPriceField).withValidator(new DoubleRangeValidator("Výrobní cena mimo hodnoty", 0.0, Double.MAX_VALUE)).asRequired().bind(Product::getProductionPrice, Product::setProductionPrice);
        productionPriceField.addValueChangeListener(event -> {
            if (product != null && product.getProductionPrice() != null)
                productionPriceField.setValue((double) Math.round(event.getValue()));
        });
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
        productionPriceField.setValue(totalProductionPrice);
        updateSellingPrice();
    }

    private void setupProfit() {
        profitField.setStepButtonsVisible(true);
        profitField.setMin(0);
        profitField.setMax(Double.MAX_VALUE);
        profitField.setValue(15.0);
        profitField.round = false;
        profitField.currencySuffix.setText("%");

        profitField.addValueChangeListener(_ -> updateSellingPrice());

        binder.forField(profitField)
                .withValidator(new DoubleRangeValidator("Profit mimo hodnoty", 0.0, Double.MAX_VALUE))
                .asRequired()
                .bind(Product::getProfit, Product::setProfit);
    }

    private void setupSellingPrice() {
        Button editSellingPriceField = CustomComponent.createSmallTertiaryButton(
                VaadinIcon.EDIT.create(),
                _ -> {
                    product.setOwnSellingPrice(sellingPriceField.isReadOnly());
                    sellingPriceField.setReadOnly(!sellingPriceField.isReadOnly());
                    updateSellingPrice();
                }
        );
        sellingPriceField.suffixLayout.add(editSellingPriceField);

        binder.forField(sellingPriceField)
                .withValidator(new DoubleRangeValidator("Prodejní cena mimo hodnoty", 0.0, Double.MAX_VALUE))
                .asRequired()
                .bind(Product::getSellingPrice, Product::setSellingPrice);
    }

    private void updateSellingPrice() {
        if (sellingPriceField.isReadOnly() && productionPriceField.getValue() != null && profitField.getValue() != null)
            sellingPriceField.setValue(productionPriceField.getValue() * (1 + (profitField.getValue() / 100)));
    }

    private void setupComponentMSB(ComponentService componentService) {
        componentMSB.setItems(componentService::findAllByName);
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
        productComponentForms.forEach((_, form) ->
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
            sellingPriceField.setReadOnly(!product.getOwnSellingPrice());
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
