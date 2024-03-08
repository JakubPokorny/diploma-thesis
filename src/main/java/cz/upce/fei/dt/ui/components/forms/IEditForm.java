package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

public interface IEditForm<T>{
    T getValue();
    void setValue(T value);
    void validate() throws ValidationException;
    void expand(boolean expended);
}
