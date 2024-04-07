package cz.upce.fei.dt.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBoxBase;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBoxVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePickerVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldBase;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import cz.upce.fei.dt.beckend.entities.Product;
import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.services.ProductService;
import cz.upce.fei.dt.beckend.services.UserService;
import cz.upce.fei.dt.beckend.utilities.CzechI18n;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FilterFields {
    public static Component createFromToIntegerFilter(
            Consumer<Integer> fromInteger,
            Consumer<Integer> toInteger,
            ConfigurableFilterDataProvider<?, ?, ?> dataProvider) {

        IntegerField fromAmountFilter = createIntegerField("od", fromInteger, dataProvider);
        IntegerField toAmountFilter = createIntegerField("do", toInteger, dataProvider);
        return createFilterHeaderLayout(fromAmountFilter, toAmountFilter);
    }

    public static Component createFromToDatePickerFilter(
            Consumer<LocalDate> fromDate,
            Consumer<LocalDate> toDate,
            ConfigurableFilterDataProvider<?, ?, ?> dataProvider) {

        DatePicker fromDatePicker = createDatePickerField("od", fromDate, dataProvider);
        DatePicker toDatePicker = createDatePickerField("do", toDate, dataProvider);
        return createFilterHeaderLayout(fromDatePicker, toDatePicker);
    }

    public static Component createFromToDateTimePickerFilter(
            Consumer<LocalDateTime> fromDateTime,
            Consumer<LocalDateTime> toDateTime,
            ConfigurableFilterDataProvider<?, ?, ?> dataProvider) {

        DateTimePicker fromDateTimePicker = createDateTimePickerField("od", fromDateTime, dataProvider);
        DateTimePicker toDateTimePicker = createDateTimePickerField("do", toDateTime, dataProvider);
        return createFilterHeaderLayout(fromDateTimePicker, toDateTimePicker);
    }

    public static Component createTextFieldFilter(String placeHolder, Consumer<String> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider) {
        TextField filter = createTextField(placeHolder, consumer, dataProvider);
        return createFilterHeaderLayout(filter);
    }

    public static Component createProductMultiSelectComboBoxFilter(String placeHolder, Consumer<Set<Long>> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider, ProductService productService){
        MultiSelectComboBox<Product> filter = createMultiSelectComboBoxField(placeHolder, consumer, dataProvider, productService);
        return createFilterHeaderLayout(filter);
    }
    public static Component createUserMultiSelectComboBoxFilter(String placeHolder, Consumer<Set<Long>> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider, UserService userService){
        MultiSelectComboBox<User> filter = createUserMultiSelectComboBoxField(placeHolder, consumer, dataProvider, userService);
        return createFilterHeaderLayout(filter);
    }

    private static MultiSelectComboBox<User> createUserMultiSelectComboBoxField(String placeHolder, Consumer<Set<Long>> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider, UserService userService){
        MultiSelectComboBox<User> msb = new MultiSelectComboBox<>();
        setupComboBoxBase(placeHolder, msb);

        msb.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
        msb.setItemLabelGenerator(User::getFullName);
        msb.setItems(userService::findAllByFirstnameAndLastname);

        msb.addValueChangeListener(event -> {
            consumer.accept(event.getValue().stream().map(User::getId).collect(Collectors.toSet()));
            dataProvider.refreshAll();
        });
        return msb;
    }
    private static MultiSelectComboBox<Product> createMultiSelectComboBoxField(String placeHolder, Consumer<Set<Long>> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider, ProductService productService) {
        MultiSelectComboBox<Product> msb = new MultiSelectComboBox<>();
        setupComboBoxBase(placeHolder, msb);

        msb.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
        msb.setItemLabelGenerator(Product::getName);

        msb.setItems(query -> productService.findAllProductsIdAndName(query.getPage(), query.getPageSize(), query.getFilter().orElse("")));

        msb.addValueChangeListener(event -> {
            consumer.accept(event.getValue().stream().map(Product::getId).collect(Collectors.toSet()));
            dataProvider.refreshAll();
        });
        return msb;
    }

    private static void setupComboBoxBase(String placeholder, ComboBoxBase<?, ?, ?> comboBoxBase){
        comboBoxBase.setPlaceholder(placeholder +"...");
        comboBoxBase.setClearButtonVisible(true);
        comboBoxBase.setWidthFull();
    }

    private static DatePicker createDatePickerField(String placeHolder, Consumer<LocalDate> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider) {
        DatePicker datePicker = new DatePicker();
        datePicker.setPlaceholder(placeHolder);
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
    private static DateTimePicker createDateTimePickerField(String placeHolder, Consumer<LocalDateTime> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider) {
        DateTimePicker dateTimePicker = new DateTimePicker();
        dateTimePicker.setDatePlaceholder("datum "+placeHolder+"...");
        dateTimePicker.setTimePlaceholder("čas "+placeHolder+"...");
        dateTimePicker.setWidthFull();
        dateTimePicker.addThemeVariants(DateTimePickerVariant.LUMO_SMALL);
        dateTimePicker.setDatePickerI18n(CzechI18n.getDatePickerI18n());
        dateTimePicker.addValueChangeListener(event -> {
            consumer.accept(event.getValue());
            dataProvider.refreshAll();
        });
        return dateTimePicker;
    }

    private static TextField createTextField(String placeHolder, Consumer<String> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider) {
        TextField textField = new TextField();
        setupTextBase(textField, placeHolder);
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        textField.addValueChangeListener(event -> {
            consumer.accept(event.getValue());
            dataProvider.refreshAll();
        });
        return textField;
    }

    private static IntegerField createIntegerField(String placeholder, Consumer<Integer> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider) {
        IntegerField integerField = new IntegerField();
        setupTextBase(integerField, placeholder);
        integerField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        integerField.setStepButtonsVisible(true);
        integerField.setStep(1);
        integerField.setValueChangeMode(ValueChangeMode.ON_CHANGE);

        integerField.addValueChangeListener(event -> {
            consumer.accept(event.getValue());
            dataProvider.refreshAll();
        });

        return integerField;
    }

    private static VerticalLayout createFilterHeaderLayout(Component... components) {
        VerticalLayout layout = new VerticalLayout(components);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");
        return layout;
    }

    private static void setupTextBase(TextFieldBase<?, ?> component, String placeHolder) {
        component.setPlaceholder(placeHolder + "...");
        component.setClearButtonVisible(true);
        component.setValueChangeMode(ValueChangeMode.LAZY);
        component.setWidthFull();
    }
}
