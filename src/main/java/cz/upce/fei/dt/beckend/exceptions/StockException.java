package cz.upce.fei.dt.beckend.exceptions;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoIcon;
import cz.upce.fei.dt.beckend.dto.CheckStockDto;

import java.util.Collection;

public class StockException extends RuntimeException{
    private final Collection<CheckStockDto> missingComponents;
    public StockException(Collection<CheckStockDto> missingComponents, String message){
        super(message);
        this.missingComponents = missingComponents;
    }

    public void showNotification(){
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        Div missing = new Div();
        missing.add(new Paragraph("Pro vytvoření objednávky na skladě chybí komponenty:"));
        UnorderedList list = new UnorderedList();
        missingComponents.forEach(component -> list.add(new ListItem("%d, %s, %d kusů".formatted(component.getComponentId(),component.getComponentName(), Math.abs(component.getComponentsInStock())))));
        missing.add(list);

        Button closeButton = new Button(LumoIcon.CROSS.create());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.setAriaLabel("Close");
        closeButton.addClickListener(event -> notification.close());

        notification.add(new HorizontalLayout(missing, closeButton));
        notification.open();

    }
}
