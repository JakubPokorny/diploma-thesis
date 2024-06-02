package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import cz.upce.fei.dt.beckend.entities.ProductComponent;
import cz.upce.fei.dt.ui.components.forms.events.UpdateProductProductionPriceEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductComponentForm extends FormLayout implements IEditForm<ProductComponent> {
    private final Binder<ProductComponent> binder = new BeanValidationBinder<>(ProductComponent.class);
    private ProductComponent productComponent;
    private final IntegerField amount = new IntegerField();

    public ProductComponentForm(ProductComponent productComponent, String label) {
        amount.setLabel(label);
        amount.setMin(1);
        amount.setValue(productComponent.getAmount());
        amount.setStep(1);
        amount.setMax(Integer.MAX_VALUE);
        amount.setStepButtonsVisible(true);
        binder.forField(amount)
                .withValidator(new IntegerRangeValidator("Počet dílů " + label + " mimo hodnoty <0; 4 294 967 296>", 0, Integer.MAX_VALUE))
                .withValidationStatusHandler(statusChange -> amount.setErrorMessage("Hodnoty mimo <1; 4 294 967 296>"))
                .asRequired()
                .bind(ProductComponent::getAmount, ProductComponent::setAmount);

        amount.addValueChangeListener(event -> ComponentUtil.fireEvent(UI.getCurrent(), new UpdateProductProductionPriceEvent(this, null)));

        this.setColspan(amount, 2);
        add(amount);

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
