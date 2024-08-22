package cz.upce.fei.dt.ui.utilities;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class CustomNotification {

    public static void showSimpleError(String text) {
        Notification.show(text).addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
