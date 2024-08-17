package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import cz.upce.fei.dt.backend.entities.ContractProduct;
import cz.upce.fei.dt.backend.utilities.CzechI18n;
import cz.upce.fei.dt.ui.components.forms.events.UpdateContractPriceEvent;

public class ContractProductForm extends FormLayout implements IEditForm<ContractProduct> {
    private final Binder<ContractProduct> binder = new BeanValidationBinder<>(ContractProduct.class);
    private ContractProduct contractProduct;
    private final IntegerField amount = new IntegerField();

    public ContractProductForm(ContractProduct contractProduct) {
        amount.setLabel(contractProduct.getProduct().getName());
        amount.setHelperText(CzechI18n.getCurrency(contractProduct.getPricePerPiece()) +"/ks");
        amount.setMin(1);
        amount.setValue(1);
        amount.setStep(1);
        amount.setMax(Integer.MAX_VALUE);
        amount.setStepButtonsVisible(true);
        amount.addThemeVariants(TextFieldVariant.LUMO_HELPER_ABOVE_FIELD);
        binder.forField(amount)
                .withValidationStatusHandler(_ -> amount.setErrorMessage("Hodnoty mimo <1; 4 294 967 296>"))
                .asRequired()
                .bind(ContractProduct::getAmount, ContractProduct::setAmount);

        amount.addValueChangeListener(_ -> ComponentUtil.fireEvent(UI.getCurrent(), new UpdateContractPriceEvent(this)));

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
        amount.setHelperText(CzechI18n.getCurrency(contractProduct.getPricePerPiece()) +"/ks");
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
