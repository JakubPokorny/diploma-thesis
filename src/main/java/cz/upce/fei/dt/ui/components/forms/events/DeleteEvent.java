package cz.upce.fei.dt.ui.components.forms.events;

import cz.upce.fei.dt.ui.components.GridFormLayout;

public class DeleteEvent extends FormEvent<GridFormLayout, Object> {
    public DeleteEvent(GridFormLayout source, Object value) {
        super(source, value);
    }
}
