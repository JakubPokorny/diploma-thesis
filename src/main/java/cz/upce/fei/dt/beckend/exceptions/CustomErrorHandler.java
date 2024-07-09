package cz.upce.fei.dt.beckend.exceptions;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.ErrorEvent;
import com.vaadin.flow.server.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class CustomErrorHandler implements ErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorHandler.class);
    private final Map<Class<? extends Throwable>, String> exceptionMessages = Map.of(
            AuthenticationException.class, "Nedostatečné oprávnění: "
    );

    @Override
    public void error(ErrorEvent event) {
        Throwable throwable = event.getThrowable();
        String message = exceptionMessages.getOrDefault(throwable.getClass(), "Neočekávaná chyba");
        UI.getCurrent().getUI().ifPresent(_ -> Notification.show(message + " " + throwable.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR));
        logger.error(message, throwable);
    }
}
