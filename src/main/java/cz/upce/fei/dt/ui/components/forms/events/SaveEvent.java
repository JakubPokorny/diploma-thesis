package cz.upce.fei.dt.ui.components.forms.events;

import cz.upce.fei.dt.ui.components.GridFormLayout;

public class SaveEvent extends FormEvent<GridFormLayout,Object> {
    public SaveEvent(GridFormLayout source, Object value) {
        super(source, value);
    }
}
