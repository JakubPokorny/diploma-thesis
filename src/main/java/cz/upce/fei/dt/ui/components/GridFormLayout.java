package cz.upce.fei.dt.ui.components;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.theme.lumo.LumoIcon;
import cz.upce.fei.dt.ui.components.forms.IEditForm;
import cz.upce.fei.dt.ui.components.forms.events.CloseEvent;
import cz.upce.fei.dt.ui.components.forms.events.DeleteEvent;
import cz.upce.fei.dt.ui.components.forms.events.ExpandEvent;
import cz.upce.fei.dt.ui.components.forms.events.SaveEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GridFormLayout<F extends FormLayout & IEditForm<T>, T> extends HorizontalLayout {
    private final VerticalLayout formLayout;
    private final VerticalLayout contentLayout;
    private final F form;
    private final Grid<T> content;

    public Tabs filterTabs = new Tabs();
    public final Button addButton = new Button(LumoIcon.PLUS.create());
    public final Button showHideButton = new Button(LumoIcon.EYE.create());
    public ColumnToggleContextMenu<T> showHideMenu = new ColumnToggleContextMenu<>(showHideButton);
    public final Div filtersAndActionsContainer = new Div(filterTabs, addButton, showHideButton);

    public GridFormLayout(F form, Grid<T> content) {
        this.form = form;
        this.form.setClassName("edit-form");

        this.content = content;
        this.content.setClassName("grid");
        content.setSizeFull();

        contentLayout = new VerticalLayout();
        formLayout = new VerticalLayout();

        configureFormLayout(form);
        configureContentLayout(content);

        this.setFlexGrow(3, contentLayout);
        this.setFlexGrow(1, formLayout);
        this.addClassName("grid-content-with-form");
        this.setSizeFull();

        add(contentLayout, formLayout);

    }

    private void showConfirmDeleteDialog() {
        ConfirmDialog dialog = new ConfirmDialog();

        dialog.setHeader("Odstranit záznam");
        dialog.setText("Opravdu chcete smazat načtený záznam v formuláři?");

        dialog.setCancelable(true);
        dialog.setConfirmText("Odstranit");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(_ -> fireEvent(new DeleteEvent(this, form.getValue())));
        dialog.open();
    }

    public void addNewValue(T value) {
        content.asSingleSelect().clear();
        showFormLayout(value);
    }

    public void showFormLayout(T value) {
        if (value == null) {
            closeFormLayout();
        } else {
            form.setValue(value);
            formLayout.setVisible(true);
            addClassName("editing");
            UI.getCurrent().getPage().retrieveExtendedClientDetails(receiver -> {
                int screenWidth = receiver.getScreenWidth();
                int screenHeight = receiver.getScreenHeight();
                if (screenWidth < 550 || screenHeight < 400) {
                    contentLayout.setVisible(false);
                }
            });
        }
    }

    public void closeFormLayout() {
        form.setValue(null);
        formLayout.setVisible(false);
        contentLayout.setVisible(true);
        removeClassName("editing");
    }

    public void expandFormLayout() {
        contentLayout.setVisible(!contentLayout.isVisible());
        form.expand(!contentLayout.isVisible());
    }

    private void configureContentLayout(Grid<T> content) {
        filtersAndActionsContainer.setClassName("filters-and-action-container");
        filterTabs.addClassName("filter-tabs");

        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        showHideButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        contentLayout.setClassName("content-layout");
        contentLayout.setSpacing(false);
        contentLayout.setMargin(false);
        contentLayout.setPadding(false);

        contentLayout.add(filtersAndActionsContainer, content);
    }

    private void configureFormLayout(F form) {
        formLayout.setClassName("form-layout");
        formLayout.setWidth("40%");
        formLayout.setVisible(false);
        formLayout.setPadding(false);
        formLayout.setSpacing(false);
        formLayout.setMargin(false);

        ComponentUtil.addListener(this, CloseEvent.class, _ -> closeFormLayout());
        ComponentUtil.addListener(this, ExpandEvent.class, _ -> expandFormLayout());

        formLayout.add(createFormTopActions(), form, createFormBottomActions());
    }

    private HorizontalLayout createFormTopActions() {
        HorizontalLayout topActions = new HorizontalLayout();
        topActions.setClassName("form-top-actions");
        topActions.setSpacing(false);
        topActions.setMargin(false);
        topActions.setWidthFull();

        final Button save = new Button(LumoIcon.CHECKMARK.create());
        final Button delete = new Button(VaadinIcon.TRASH.create());
        final Button close = new Button(VaadinIcon.CLOSE_CIRCLE_O.create());
        final Button expand = new Button(VaadinIcon.EXPAND_FULL.create());

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        expand.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);

        save.addClickListener(_ -> validateAndSave());
        delete.addClickListener(_ -> showConfirmDeleteDialog());
        close.addClickListener(_ -> fireEvent(new CloseEvent(this)));
        expand.addClickListener(_ -> fireEvent(new ExpandEvent(this)));

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        topActions.add(close, expand, delete, save);
        return topActions;
    }

    private VerticalLayout createFormBottomActions() {
        VerticalLayout bottomActions = new VerticalLayout();
        bottomActions.setPadding(false);
        bottomActions.setMargin(false);
        bottomActions.setSpacing(false);

        final Button save = new Button("Uložit");
        save.setPrefixComponent(LumoIcon.CHECKMARK.create());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(_ -> validateAndSave());
        save.setWidthFull();

        final Button delete = new Button("Smazat");
        delete.setPrefixComponent(VaadinIcon.TRASH.create());
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.addClickListener(_ -> showConfirmDeleteDialog());
        delete.setWidthFull();

        bottomActions.add(save, delete);
        return bottomActions;
    }

    private void validateAndSave() {
        try {
            form.validate();
            fireEvent(new SaveEvent(this, form.getValue()));
        } catch (ValidationException e) {
            Notification.show("Nevalidní vstupy ve formuláři.").addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
