package cz.upce.fei.dt.ui.components.filters;

import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.function.Consumer;

public class IDFilterField extends NumberField {

    public IDFilterField(Consumer<Long> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider) {
        addThemeVariants(TextFieldVariant.LUMO_SMALL);
        setValueChangeMode(ValueChangeMode.EAGER);
        setPlaceholder("id...");
        setClearButtonVisible(true);
        setWidthFull();

        addValueChangeListener(event -> {
            if (event.getValue() != null)
                consumer.accept(event.getValue().longValue());
            else
                consumer.accept(null);
            dataProvider.refreshAll();
        });
    }

    public FilterHeaderLayout getFilterHeaderLayout() {
        return new FilterHeaderLayout(this);
    }
}
