package cz.upce.fei.dt.ui.components.forms.events;

import cz.upce.fei.dt.ui.components.GridFormLayout;

public class ExpandEvent extends FormEvent<GridFormLayout, Object> {
    public ExpandEvent(GridFormLayout source) {
        super(source, null);
    }
}
