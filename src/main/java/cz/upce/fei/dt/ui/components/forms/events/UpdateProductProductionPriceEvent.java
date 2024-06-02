package cz.upce.fei.dt.ui.components.forms.events;

import cz.upce.fei.dt.ui.components.forms.ProductComponentForm;

public class UpdateProductProductionPriceEvent extends FormEvent<ProductComponentForm, Double> {
    public UpdateProductProductionPriceEvent(ProductComponentForm source, Double value) {
        super(source, value);
    }
}
