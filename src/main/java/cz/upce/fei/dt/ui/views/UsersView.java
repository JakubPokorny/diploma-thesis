package cz.upce.fei.dt.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import cz.upce.fei.dt.backend.entities.User;
import cz.upce.fei.dt.backend.entities.User_;
import cz.upce.fei.dt.backend.services.UserService;
import cz.upce.fei.dt.backend.services.filters.UserFilter;
import cz.upce.fei.dt.ui.components.GridFormLayout;
import cz.upce.fei.dt.ui.components.filters.FilterFields;
import cz.upce.fei.dt.ui.components.filters.FromToLocalDateFilterFields;
import cz.upce.fei.dt.ui.components.forms.UserForm;
import cz.upce.fei.dt.ui.components.forms.events.DeleteEvent;
import cz.upce.fei.dt.ui.components.forms.events.SaveEvent;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "users", layout = MainLayout.class)
@RouteAlias(value = "uzivatele", layout = MainLayout.class)
@PageTitle("Users")
@RolesAllowed("ADMIN")
public class UsersView extends VerticalLayout {
    private final UserService userService;
    private final GridFormLayout<UserForm, User> gridFormLayout;
    private final Grid<User> grid;
    private final UserFilter userFilter = new UserFilter();
    private ConfigurableFilterDataProvider<User, Void, UserFilter> configurableFilterDataProvider;

    public UsersView(UserService userService) {

        this.userService = userService;

        UserForm form = new UserForm();
        grid = new Grid<>(User.class, false);
        gridFormLayout = new GridFormLayout<>(form, grid);
        MainLayout.setPageTitle("Uživatelé", UsersView.class);
        setSizeFull();

        configureGrid();
        configureForm();
        configureActions();
        configureFilters();

        add(gridFormLayout);
    }

    //region configures: grid, form, actions, filters, events
    private void configureFilters() {
    }

    private void configureActions() {
        gridFormLayout.addButton.addClickListener(_ -> gridFormLayout.addNewValue(new User()));
    }

    private void configureForm() {
        ComponentUtil.addListener(gridFormLayout, SaveEvent.class, this::saveUser);
        ComponentUtil.addListener(gridFormLayout, DeleteEvent.class, this::deleteUser);
    }

    private void saveUser(SaveEvent saveEvent) {
        try {
            User user = (User) saveEvent.getValue();
            userService.saveUser(user);
            updateGrid();
            gridFormLayout.closeFormLayout();
            Notification.show("Uživatel " + user.getFullName() + " uložen.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception exception) {
            Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteUser(DeleteEvent deleteEvent) {
        User user = (User) deleteEvent.getValue();

        Dialog dialog = new Dialog();

        ComboBox<User> userCB = new ComboBox<>("Nahradit uživatelem");
        userCB.setAutofocus(true);
        userCB.setWidthFull();
        userCB.setItemLabelGenerator(u -> u.getFullName() + ", " + u.getEmail());
        userCB.setItems(query -> userService.findAllByFirstnameAndLastnameAndEmail(query)
                .filter(u -> !u.getEmail().equals(user.getEmail())));

        Button confirmButton = new Button("Pokračovat", _ -> {
            User alternateUser = userCB.getValue();
            if (alternateUser == null) {
                Notification.show("Vyberte uživatele.");
            } else {
                dialog.close();
                performDeletion(user, alternateUser);
            }
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button closeButton = new Button("Zrušit", _ -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        HorizontalLayout buttonLayout = new HorizontalLayout(confirmButton, closeButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(
                new H2("Vyberte uživatele"),
                new Paragraph("Na koho převést záznamy od uživatele " + user.getFullName() + "?"),
                userCB,
                buttonLayout);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void performDeletion(User user, User alternateUser) {
        try {
            userService.deleteUser(user, alternateUser);
            updateGrid();
            gridFormLayout.closeFormLayout();
            Notification.show("Uživatel " + user.getFullName() + " odstraněn.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception exception) {
            Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void configureGrid() {
        grid.setClassName("grid-content");
        grid.setSizeFull();

        DataProvider<User, UserFilter> dataProvider = DataProvider.fromFilteringCallbacks(
                userService::fetchFromBackEnd,
                userService::sizeInBackEnd
        );

        configurableFilterDataProvider = dataProvider.withConfigurableFilter();
        configurableFilterDataProvider.setFilter(userFilter);

        Grid.Column<User> firstNameColumn = grid.addColumn(User::getFirstName).setHeader("Křestní jméno").setKey(User_.FIRST_NAME).setWidth("150px");
        Grid.Column<User> lastNameColumn = grid.addColumn(User::getLastName).setHeader("Příjmení").setKey(User_.LAST_NAME).setWidth("150px");
        Grid.Column<User> emailColumn = grid.addColumn(User::getEmail).setHeader("Email").setKey(User_.EMAIL).setWidth("150px");
        Grid.Column<User> roleColumn = grid.addColumn(User::getRole).setHeader("Role").setKey(User_.ROLE).setWidth("150px");
        Grid.Column<User> tokenColumn = grid.addComponentColumn(this::createTokenComponent).setHeader("Token pro obnovení").setWidth("150px");
        Grid.Column<User> createdColumn = grid.addColumn(new LocalDateTimeRenderer<>(User::getCreated, "H:mm d. M. yyyy")).setHeader("Vytvořeno").setKey(User_.CREATED).setWidth("150px");
        Grid.Column<User> updatedColumn = grid.addColumn(new LocalDateTimeRenderer<>(User::getUpdated, "H:mm d. M. yyyy")).setHeader("Upraveno").setKey(User_.UPDATED).setWidth("150px");

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(firstNameColumn).setComponent(FilterFields.createTextFieldFilter("jméno", userFilter::setFirstNameFilter, configurableFilterDataProvider));
        headerRow.getCell(lastNameColumn).setComponent(FilterFields.createTextFieldFilter("příjmení", userFilter::setLastNameFilter, configurableFilterDataProvider));
        headerRow.getCell(emailColumn).setComponent(FilterFields.createTextFieldFilter("email", userFilter::setEmailFilter, configurableFilterDataProvider));
        headerRow.getCell(roleColumn).setComponent(FilterFields.createTextFieldFilter("role", userFilter::setRoleFilter, configurableFilterDataProvider));
        headerRow.getCell(tokenColumn).setComponent(FilterFields.createTextFieldFilter("token", userFilter::setTokenFilter, configurableFilterDataProvider));
        headerRow.getCell(createdColumn).setComponent(new FromToLocalDateFilterFields(userFilter::setFromCreatedFilter, userFilter::setToCreatedFilter, configurableFilterDataProvider).getFilterHeaderLayout());
        headerRow.getCell(updatedColumn).setComponent(new FromToLocalDateFilterFields(userFilter::setFromUpdatedFilter, userFilter::setToUpdatedFilter, configurableFilterDataProvider).getFilterHeaderLayout());

        grid.asSingleSelect().addValueChangeListener(e -> gridFormLayout.showFormLayout(e.getValue()));
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        grid.setSortableColumns(User_.FIRST_NAME, User_.LAST_NAME, User_.EMAIL, User_.ROLE, User_.CREATED, User_.UPDATED);

        createdColumn.setVisible(false);
        updatedColumn.setVisible(false);
        gridFormLayout.showHideMenu.addColumnToggleItem("Křestní jméno", firstNameColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Příjmení", lastNameColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Email", emailColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Role", roleColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Token pro obnovení", tokenColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Vytvořeno", createdColumn);
        gridFormLayout.showHideMenu.addColumnToggleItem("Upraveno", updatedColumn);

        updateGrid();
    }

    private Component createTokenComponent(User user) {
        if (user.getResetToken() == null || user.getResetToken().isEmpty()) {
            Button generate = new Button("Generovat");
            generate.addClickListener(_ -> {
                userService.generateResetToken(user);
                updateGrid();
            });
            return generate;
        } else {
            Span span = new Span(user.getResetToken());
            span.setClassName("vaadin-grid-cell-content");
            return span;
        }
    }

    //endregion
    private void updateGrid() {
        grid.setItems(configurableFilterDataProvider);
    }
}
