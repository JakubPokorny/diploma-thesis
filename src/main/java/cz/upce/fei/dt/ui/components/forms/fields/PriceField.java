package cz.upce.fei.dt.ui.components.forms.fields;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;

public class PriceField extends NumberField {
    public HorizontalLayout suffixLayout = new HorizontalLayout();
    public Span currencySuffix = new Span("Kč");
    public boolean round = true;

    public PriceField(String label) {
        super(label);

        this.setValue(0.0);
        this.setMax(Double.MAX_VALUE);
        this.setSuffixComponent(suffixLayout);
        this.addValueChangeListener(event -> {
            if (round && event.getValue() != null)
                this.setValue((double) Math.round(event.getValue()));
        });

        currencySuffix.setHeightFull();

        suffixLayout.setPadding(false);
        suffixLayout.setMargin(false);
        suffixLayout.add(currencySuffix);
        suffixLayout.setAlignSelf(FlexComponent.Alignment.CENTER, currencySuffix);
    }
}
