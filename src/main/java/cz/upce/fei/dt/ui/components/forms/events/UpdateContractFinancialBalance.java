package cz.upce.fei.dt.ui.components.forms.events;

import com.vaadin.flow.component.formlayout.FormLayout;

public class UpdateContractFinancialBalance extends FormEvent<FormLayout, Void> {
    public UpdateContractFinancialBalance(FormLayout source) {
        super(source, null);
    }
}
