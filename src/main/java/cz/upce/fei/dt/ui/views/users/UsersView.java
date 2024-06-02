package cz.upce.fei.dt.ui.views.users;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.services.UserService;
import cz.upce.fei.dt.ui.components.GridFormLayout;
import cz.upce.fei.dt.ui.components.forms.events.DeleteEvent;
import cz.upce.fei.dt.ui.components.forms.events.SaveEvent;
import cz.upce.fei.dt.ui.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;

import java.io.InvalidClassException;

@Route(value = "users", layout = MainLayout.class)
@RouteAlias(value = "uzivatele", layout = MainLayout.class)
@PageTitle("Users")
@RolesAllowed("ADMIN")
public class UsersView extends VerticalLayout {
    private final UserService userService;
    private final GridFormLayout<UserForm, User> gridFormLayout;
    private final UserForm form;
    private final Grid<User> grid;

    public UsersView(UserService userService) {
        this.userService = userService;
        MainLayout.setPageTitle("Uživatelé", UsersView.class);
        setSizeFull();

        form = new UserForm();
        grid = new Grid<>(User.class, false);
        gridFormLayout = new GridFormLayout<>(form, grid);

        configureGrid();
        configureForm();

        Button addUser = new Button("Přidat uživatele");
        addUser.addClickListener(event-> gridFormLayout.addNewValue(new User()));
        gridFormLayout.getActionsLayout().add(addUser);

        add(gridFormLayout);
    }

    private void configureForm() {
        ComponentUtil.addListener(gridFormLayout, SaveEvent.class, this::saveUser);
        ComponentUtil.addListener(gridFormLayout, DeleteEvent.class, this::deleteUser);
    }

    private void saveUser(SaveEvent saveEvent){
        userService.saveUser((User) saveEvent.getValue());
        grid.setItems(userService.findAll());
        gridFormLayout.closeFormLayout();
    }
    private void deleteUser(DeleteEvent deleteEvent){
        userService.deleteUser((User) deleteEvent.getValue());
        grid.setItems(userService.findAll());
        gridFormLayout.closeFormLayout();
    }

    private void configureGrid() {
        grid.setItems(userService.findAll());
        grid.addColumn(User::getFirstName).setHeader("Křestní jméno");
        grid.addColumn(User::getLastName).setHeader("Příjmení");
        grid.addColumn(User::getEmail).setHeader("Email");
        grid.addColumn(User::getRoles).setHeader("Oprávnění");
        grid.addComponentColumn(this::createTokenComponent).setHeader("Token pro obnovení");

        grid.asSingleSelect().addValueChangeListener(e-> gridFormLayout.showFormLayout(e.getValue()));
    }

    private Component createTokenComponent(User user) {
        if (user.getResetToken().isEmpty()){
            Button generate = new Button("Generovat");
            generate.addClickListener(click->{
                try {
                    userService.generateResetToken(user);
                    grid.getDataProvider().refreshAll();
                } catch (InvalidClassException exception){
                    //todo
                }
            });
            return generate;
        }else {
            Span span = new Span(user.getResetToken());
            span.setClassName("vaadin-grid-cell-content");
            return span;
        }
    }
}
