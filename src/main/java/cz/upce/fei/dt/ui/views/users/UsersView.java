package cz.upce.fei.dt.ui.views.users;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.entities.User_;
import cz.upce.fei.dt.beckend.services.UserService;
import cz.upce.fei.dt.beckend.services.filters.UserFilter;
import cz.upce.fei.dt.ui.components.FilterFields;
import cz.upce.fei.dt.ui.components.GridFormLayout;
import cz.upce.fei.dt.ui.components.forms.events.DeleteEvent;
import cz.upce.fei.dt.ui.components.forms.events.SaveEvent;
import cz.upce.fei.dt.ui.views.MainLayout;
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
        Button addUser = new Button("Přidat uživatele");
        addUser.addClickListener(event -> gridFormLayout.addNewValue(new User()));
        gridFormLayout.getActionsLayout().add(addUser);
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
        try {
            User user = (User) deleteEvent.getValue();
            userService.deleteUser(user);
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
        Grid.Column<User> createdColumn = grid.addColumn(new LocalDateTimeRenderer<>(User::getCreated, "H:mm d. M. yyyy")).setHeader("Vytvořen").setKey(User_.CREATED).setWidth("150px");
        Grid.Column<User> updatedColumn = grid.addColumn(new LocalDateTimeRenderer<>(User::getUpdated, "H:mm d. M. yyyy")).setHeader("Upraven").setKey(User_.UPDATED).setWidth("150px");

        HeaderRow headerRow = grid.appendHeaderRow();
        headerRow.getCell(firstNameColumn).setComponent(FilterFields.createTextFieldFilter("jméno", userFilter::setFirstNameFilter, configurableFilterDataProvider));
        headerRow.getCell(lastNameColumn).setComponent(FilterFields.createTextFieldFilter("příjmení", userFilter::setLastNameFilter, configurableFilterDataProvider));
        headerRow.getCell(emailColumn).setComponent(FilterFields.createTextFieldFilter("email", userFilter::setEmailFilter, configurableFilterDataProvider));
        headerRow.getCell(roleColumn).setComponent(FilterFields.createTextFieldFilter("role", userFilter::setRoleFilter, configurableFilterDataProvider));
        headerRow.getCell(tokenColumn).setComponent(FilterFields.createTextFieldFilter("token", userFilter::setTokenFilter, configurableFilterDataProvider));
        headerRow.getCell(createdColumn).setComponent(FilterFields.createFromToDatePickerFilter(userFilter::setFromCreatedFilter, userFilter::setToCreatedFilter, configurableFilterDataProvider));
        headerRow.getCell(updatedColumn).setComponent(FilterFields.createFromToDatePickerFilter(userFilter::setFromUpdatedFilter, userFilter::setToUpdatedFilter, configurableFilterDataProvider));

        grid.asSingleSelect().addValueChangeListener(e -> gridFormLayout.showFormLayout(e.getValue()));
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        grid.setSortableColumns(User_.FIRST_NAME, User_.LAST_NAME, User_.EMAIL, User_.ROLE, User_.CREATED, User_.UPDATED);

        updateGrid();
    }

    private Component createTokenComponent(User user) {
        if (user.getResetToken() == null || user.getResetToken().isEmpty()) {
            Button generate = new Button("Generovat");
            generate.addClickListener(click -> {
                try {
                    userService.generateResetToken(user);
                    updateGrid();
                } catch (Exception exception) {
                    Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
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
