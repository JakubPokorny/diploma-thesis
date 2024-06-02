package cz.upce.fei.dt.ui.components.forms.events;

import cz.upce.fei.dt.ui.components.GridFormLayout;

public class CloseEvent extends FormEvent<GridFormLayout, Object> {
    public CloseEvent(GridFormLayout source) {
        super(source, null);
    }
}
