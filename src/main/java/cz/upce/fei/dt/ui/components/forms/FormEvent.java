package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;

@Getter
public abstract class FormEvent<F extends Component, T> extends ComponentEvent<F>{
    private final T value;
    protected FormEvent(F source, T value){
        super(source, false);
        this.value = value;
    }
}
