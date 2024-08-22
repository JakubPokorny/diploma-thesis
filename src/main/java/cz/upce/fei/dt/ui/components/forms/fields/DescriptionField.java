package cz.upce.fei.dt.ui.components.forms.fields;

import com.vaadin.flow.component.textfield.TextArea;

public class DescriptionField extends TextArea {
    private static final String DEFAULT_DESCRIPTION = "popis";
    private static final String DEFAULT_HEIGHT = "300px";

    public DescriptionField(String label, int maxLength) {
        super(label, DEFAULT_DESCRIPTION);

        this.setWidthFull();
        this.setHeight(DEFAULT_HEIGHT);
        this.setMaxLength(maxLength);

        this.addValueChangeListener(_ -> reloadHelperText());
    }

    private void reloadHelperText() {
        this.setHelperText(this.getValue().length() + "/" + this.getMaxLength());
    }
}
