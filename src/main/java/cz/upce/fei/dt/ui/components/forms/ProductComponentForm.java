package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import cz.upce.fei.dt.beckend.entities.Component;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.ProductComponent;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;

@Getter
@Setter
public class ProductComponentForm extends FormLayout implements IEditForm<ProductComponent>{
    private final Binder<ProductComponent> binder = new BeanValidationBinder<>(ProductComponent.class);
    private ProductComponent productComponent = new ProductComponent();
    private final ComboBox<Product> productCB = new ComboBox<>("Produkt", Collections.emptyList());
    private final ComboBox<Component> componentCB = new ComboBox<>("Komponenta", Collections.emptyList());
    private final IntegerField amount = new IntegerField("Počet dílů");

    public ProductComponentForm(Product product) {
        amount.setLabel("Počet pro " + product.getName());
        binder.forField(amount)
                .withValidator(new IntegerRangeValidator("Počet dílů "+ product.getName() +" mimo hodnoty <0;4 294 967 296>", 0, Integer.MAX_VALUE));
        productCB.setValue(product);
        productComponent.setProduct(product);
        configure();
    }

    public ProductComponentForm(Component component){
        amount.setLabel("Počet pro " + component.getName());
        binder.forField(amount)
                .withValidator(new IntegerRangeValidator("Počet dílů "+ component.getName() +" mimo hodnoty <0; 4 294 967 296>", 0, Integer.MAX_VALUE));
        componentCB.setValue(component);
        productComponent.setComponent(component);
        configure();
    }

    private void configure() {
        amount.setMin(1);
        amount.setValue(1);
        amount.setMax(Integer.MAX_VALUE);
        amount.setStep(1);
        amount.setStepButtonsVisible(true);
        binder.forField(amount)
                .withValidationStatusHandler(statusChange -> amount.setErrorMessage("Hodnoty mimo <1; 4 294 967 296>"))
                .asRequired()
                .bind(ProductComponent::getAmount, ProductComponent::setAmount);

        productCB.setReadOnly(true);
        productCB.setItemLabelGenerator(Product::getName);
        binder.forField(productCB)
                .asRequired()
                .bind(ProductComponent::getProduct, ProductComponent::setProduct);

        componentCB.setReadOnly(true);
        componentCB.setItemLabelGenerator(Component::getName);
        binder.forField(componentCB)
                .asRequired()
                .bind(ProductComponent::getComponent, ProductComponent::setComponent);

        add(amount);
    }
    //region IEditForm
    @Override
    public ProductComponent getValue() {
        return productComponent;
    }

    @Override
    public void setValue(ProductComponent value) {
        productComponent = value;
        binder.readBean(productComponent);
    }

    @Override
    public void validate() throws ValidationException {
        binder.writeBean(productComponent);
    }

    @Override
    public void expand(boolean expended) {

    }
    //endregion
}
