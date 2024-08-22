package cz.upce.fei.dt.ui.utilities;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class CustomComponent {
    public static Component createContextIcon(VaadinIcon vaadinIcon) {
        Icon icon = vaadinIcon.create();
        icon.setClassName("context-icon");
        return icon;
    }

    public static Button createSmallTertiaryButton(Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button button = new Button(icon, clickListener);
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        return button;
    }

}
