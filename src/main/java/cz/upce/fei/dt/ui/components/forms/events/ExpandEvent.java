package cz.upce.fei.dt.ui.components.forms.events;

import cz.upce.fei.dt.ui.components.GridFormLayout;
import cz.upce.fei.dt.ui.components.forms.FormEvent;

public class ExpandEvent extends FormEvent<GridFormLayout, Object> {
    public ExpandEvent(GridFormLayout source) {
        super(source, null);
    }
}
