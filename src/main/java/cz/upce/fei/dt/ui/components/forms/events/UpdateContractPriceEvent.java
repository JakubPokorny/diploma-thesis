package cz.upce.fei.dt.ui.components.forms.events;

import cz.upce.fei.dt.ui.components.forms.ContractProductForm;

public class UpdateContractPriceEvent extends FormEvent<ContractProductForm, Double> {
    public UpdateContractPriceEvent(ContractProductForm source, Double value) {
        super(source, value);
    }
}
