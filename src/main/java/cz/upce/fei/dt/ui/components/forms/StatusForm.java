package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import cz.upce.fei.dt.backend.entities.Status;

public class StatusForm extends FormLayout implements IEditForm<Status> {
    private final Binder<Status> binder = new BeanValidationBinder<>(Status.class);
    private Status status;
    private final TextField statusTextField = new TextField("Status");
    private final ComboBox<Status.Theme> themeComboBox = new ComboBox<>("Motiv");

    public StatusForm() {
        setClassName("edit-form");

        setupStatusTextField();
        setupThemeComboBox();

        add(statusTextField, themeComboBox);

    }

    //regions setups
    private void setupThemeComboBox() {
        themeComboBox.setItems(Status.Theme.values());
        themeComboBox.setItemLabelGenerator(theme -> String.format("%s, %s", theme, theme.getMeaning()));
        binder.forField(themeComboBox)
                .asRequired()
                .bind(Status::getTheme, Status::setTheme);
    }

    private void setupStatusTextField() {
        binder.forField(statusTextField)
                .asRequired()
                .bind(Status::getStatus, Status::setStatus);
    }

    //endregion

    //region IEditForm
    @Override
    public Status getValue() {
        return status;
    }

    @Override
    public void setValue(Status value) {
        status = value;
        binder.readBean(status);
    }

    @Override
    public void validate() throws ValidationException {
        binder.writeBean(status);
    }

    @Override
    public void expand(boolean expended) {

    }
    //endRegion
}
