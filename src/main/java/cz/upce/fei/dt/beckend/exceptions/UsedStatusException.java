package cz.upce.fei.dt.beckend.exceptions;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import cz.upce.fei.dt.beckend.entities.Deadline;
import cz.upce.fei.dt.ui.views.ContractsView;

import java.util.Collection;

public class UsedStatusException extends RuntimeException {
    private final Notification notification = new Notification();

    public UsedStatusException(Collection<Deadline> deadlines) {
        super(getMessage(deadlines));

        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setDuration(10000);
        Div info = new Div(new Text(this.getMessage()));
        for (Deadline deadline : deadlines) {
            long contractID = deadline.getContract().getId();
            info.add(new RouterLink(String.valueOf(contractID), ContractsView.class, String.valueOf(contractID)));
            info.add(new Text(", "));
        }

        Button closeBTN = new Button(VaadinIcon.CLOSE_SMALL.create(), _ -> notification.close());
        closeBTN.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        HorizontalLayout layout = new HorizontalLayout(info, closeBTN);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
    }

    public void showNotification() {
        notification.open();
    }

    private static String getMessage(Collection<Deadline> deadlines) {
        return deadlines.size() == 1
                ? "Status nelze smazat, protože je použit v zakázce: "
                : "Status nelze smazat, protože je použit v zakázkách: ";
    }
}
