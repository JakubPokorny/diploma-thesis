package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import cz.upce.fei.dt.backend.entities.ProductComponent;
import cz.upce.fei.dt.ui.components.forms.events.UpdateProductProductionPriceEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductComponentForm extends FormLayout implements IEditForm<ProductComponent> {
    private final Binder<ProductComponent> binder = new BeanValidationBinder<>(ProductComponent.class);
    private ProductComponent productComponent;
    private final IntegerField componentsPerProduct = new IntegerField();

    public ProductComponentForm(ProductComponent productComponent, String label) {
        componentsPerProduct.setLabel(label);
        componentsPerProduct.setMin(1);
        componentsPerProduct.setValue(productComponent.getComponentsPerProduct());
        componentsPerProduct.setStep(1);
        componentsPerProduct.setMax(Integer.MAX_VALUE);
        componentsPerProduct.setStepButtonsVisible(true);
        binder.forField(componentsPerProduct)
                .withValidator(new IntegerRangeValidator("Počet dílů " + label + " mimo hodnoty <0; 4 294 967 296>", 0, Integer.MAX_VALUE))
                .withValidationStatusHandler(_ -> componentsPerProduct.setErrorMessage("Hodnoty mimo <1; 4 294 967 296>"))
                .asRequired()
                .bind(ProductComponent::getComponentsPerProduct, ProductComponent::setComponentsPerProduct);

        componentsPerProduct.addValueChangeListener(_ -> ComponentUtil.fireEvent(UI.getCurrent(), new UpdateProductProductionPriceEvent(this, null)));

        this.setColspan(componentsPerProduct, 2);
        add(componentsPerProduct);

        this.setValue(productComponent);
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
