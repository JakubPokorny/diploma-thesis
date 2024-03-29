package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import cz.upce.fei.dt.beckend.entities.ContractProduct;

public class ContractProductForm extends FormLayout implements IEditForm<ContractProduct> {
    private final Binder<ContractProduct> binder = new BeanValidationBinder<>(ContractProduct.class);
    private final IntegerField amount = new IntegerField();
    private ContractProduct contractProduct;

    public ContractProductForm(ContractProduct contractProduct) {
        amount.setLabel(contractProduct.getProduct().getName());
        amount.setMin(1);
        amount.setValue(1);
        amount.setMax(Integer.MAX_VALUE);
        amount.setStepButtonsVisible(true);
        binder.forField(amount)
                .withValidationStatusHandler(statusChange -> amount.setErrorMessage("Hodnoty mimo <1; 4 294 967 296>"))
                .asRequired()
                .bind(ContractProduct::getAmount, ContractProduct::setAmount);

        this.setColspan(amount, 2);
        add(amount);

        this.setValue(contractProduct);
    }

    //region IEditForm

    @Override
    public ContractProduct getValue() {
        return contractProduct;
    }

    @Override
    public void setValue(ContractProduct value) {
        contractProduct = value;
        binder.readBean(contractProduct);
    }

    @Override
    public void validate() throws ValidationException {
        binder.writeBean(contractProduct);
    }

    @Override
    public void expand(boolean expended) {

    }
    //endregion
}
