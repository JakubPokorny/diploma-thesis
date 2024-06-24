package cz.upce.fei.dt.ui.components.filters;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerVariant;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import cz.upce.fei.dt.beckend.utilities.CzechI18n;

import java.time.LocalDate;
import java.util.function.Consumer;

public class FromToLocalDateFilterFields {
    public DatePicker fromDatePicker;
    public DatePicker toDatePicker;

    public FromToLocalDateFilterFields(
            Consumer<LocalDate> fromDate,
            Consumer<LocalDate> toDate,
            ConfigurableFilterDataProvider<?, ?, ?> dataProvider) {
        fromDatePicker = createDatePickerField("od", fromDate, dataProvider);
        toDatePicker = createDatePickerField("do", toDate, dataProvider);
    }

    public FilterHeaderLayout getFilterHeaderLayout() {
        return new FilterHeaderLayout(fromDatePicker, toDatePicker);
    }

    private DatePicker createDatePickerField(String placeHolder, Consumer<LocalDate> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider) {
        DatePicker datePicker = new DatePicker();
        datePicker.setPlaceholder(placeHolder + "...");
        datePicker.setClearButtonVisible(true);
        datePicker.setWidthFull();
        datePicker.addThemeVariants(DatePickerVariant.LUMO_SMALL);
        datePicker.setI18n(CzechI18n.getDatePickerI18n());
        datePicker.addValueChangeListener(event -> {
            consumer.accept(event.getValue());
            dataProvider.refreshAll();
        });
        return datePicker;
    }
}
