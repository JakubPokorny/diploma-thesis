package cz.upce.fei.dt.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;

public class PriceFieldWithButton extends NumberField {
    public Button button;
    public PriceFieldWithButton(String label, VaadinIcon icon) {
        super(label);

        Span czk = new Span("Kč");
        czk.setHeight("100%");

        button = new Button(icon.create());
        button.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_SMALL);

        HorizontalLayout horizontalLayout = new HorizontalLayout(czk, button);
        horizontalLayout.setAlignSelf(FlexComponent.Alignment.CENTER, czk);
        horizontalLayout.setPadding(false);
        horizontalLayout.setMargin(false);

        this.setMin(0);
        this.setValue(0.0);
        this.setMax(Double.MAX_VALUE);
        this.setSuffixComponent(horizontalLayout);
    }
}
