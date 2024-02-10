package cz.upce.fei.dt.ui.views.users;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.services.UserService;
import cz.upce.fei.dt.ui.views.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import java.io.InvalidClassException;

@Route(value = "users", layout = MainLayout.class)
@PageTitle("Users")
@RolesAllowed("ADMIN")
public class UsersView extends VerticalLayout {
    private final UserService service;
    private UserForm form;
    private final Grid<User> grid = new Grid<>(User.class, false);

    public UsersView(UserService userService) {
        this.service = userService;
        setSizeFull();

        configureGrid();
        configureForm();

        Button addUser = new Button("Přidat uživatele");
        addUser.addClickListener(event-> addUser());

        add(addUser, getContent());
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new UserForm();
        form.setWidth("25em");
        form.setVisible(false);

        form.addSaveListener(this::saveUser);
        form.addDeleteListener(this::deleteUser);
        form.addCloseListener(closeEvent -> closeEditor());

    }

    public void addUser(){
        grid.asSingleSelect().clear();
        editUser(new User());
    }
    public void editUser(User user){
        if (user == null) {
            closeEditor();
        }
        else {
            form.setUser(user);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void saveUser(UserForm.SaveEvent saveEvent){
        service.saveUser(saveEvent.getUser());
        updateList();
        closeEditor();
    }
    private void deleteUser(UserForm.DeleteEvent deleteEvent){
        service.deleteUser(deleteEvent.getUser());
        updateList();
        closeEditor();
    }

    private void updateList() {
        grid.setItems(service.getAll());
    }

    private void closeEditor(){
        form.setUser(null);
        form.setVisible(false);
        removeClassName("editing");
    }
    private void configureGrid() {
        grid.addClassName("user-grid");
        grid.setSizeFull();

        grid.setItems(service.getAll());
        grid.addColumn(User::getFirstName).setHeader("Křestní jméno");
        grid.addColumn(User::getLastName).setHeader("Příjmení");
        grid.addColumn(User::getEmail).setHeader("Email");
        grid.addColumn(User::getRoles).setHeader("Oprávnění");
        grid.addComponentColumn(this::createTokenComponent).setHeader("Token pro obnovení");
        grid.asSingleSelect().addValueChangeListener(e->editUser(e.getValue()));
    }

    private Component createTokenComponent(User user) {
        if (user.getResetToken().isEmpty()){
            Button generate = new Button("Generovat");
            generate.addClickListener(click->{
                try {
                    service.generateResetToken(user);
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
