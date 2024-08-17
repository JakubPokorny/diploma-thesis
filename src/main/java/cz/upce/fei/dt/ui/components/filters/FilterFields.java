package cz.upce.fei.dt.ui.components.filters;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBoxBase;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBoxVariant;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import cz.upce.fei.dt.backend.entities.Contact;
import cz.upce.fei.dt.backend.entities.Product;
import cz.upce.fei.dt.backend.entities.Status;
import cz.upce.fei.dt.backend.entities.User;
import cz.upce.fei.dt.backend.services.*;

import java.util.HashSet;
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
        return new FilterHeaderLayout(fromAmountFilter, toAmountFilter);
    }

    public static Component createFromToNumberFilter(
            Consumer<Double> fromNumber,
            Consumer<Double> toNumber,
            ConfigurableFilterDataProvider<?, ?, ?> dataProvider) {

        NumberField fromAmountFilter = createNumberField("od", fromNumber, dataProvider);
        NumberField toAmountFilter = createNumberField("do", toNumber, dataProvider);
        return new FilterHeaderLayout(fromAmountFilter, toAmountFilter);
    }

    public static Component createTextFieldFilter(String placeHolder, Consumer<String> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider) {
        TextField filter = createTextField(placeHolder, consumer, dataProvider);
        return new FilterHeaderLayout(filter);
    }

    public static Component createProductMultiSelectComboBoxFilter(String placeHolder, Consumer<Set<Long>> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider, ProductService productService) {
        MultiSelectComboBox<Product> filter = createProductMultiSelectComboBoxField(placeHolder, consumer, dataProvider, productService);
        return new FilterHeaderLayout(filter);
    }

    public static Component createComponentMultiSelectComboBoxFilter(String placeHolder, Consumer<Set<Long>> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider, ComponentService componentService) {
        MultiSelectComboBox<cz.upce.fei.dt.backend.entities.Component> filter = createComponentMultiSelectComboBoxField(placeHolder, consumer, dataProvider, componentService);
        return new FilterHeaderLayout(filter);
    }

    public static Component createUserMultiSelectComboBoxFilter(String placeHolder, Consumer<Set<Long>> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider, UserService userService) {
        MultiSelectComboBox<User> filter = createUserMultiSelectComboBoxField(placeHolder, consumer, dataProvider, userService);
        return new FilterHeaderLayout(filter);
    }

    public static Component createContactMultiSelectComboBoxFilter(String placeHolder, Consumer<Set<Long>> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider, ContactService contactService) {
        MultiSelectComboBox<Contact> filter = createContactMultiSelectComboBoxField(placeHolder, consumer, dataProvider, contactService);
        return new FilterHeaderLayout(filter);
    }

    public static Component createStatusMultiSelectComboBoxFilter(String placeHolder, Consumer<Set<Status>> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider, StatusService statusService) {
        MultiSelectComboBox<Status> filter = createStatusMultiSelectComboBoxField(placeHolder, consumer, dataProvider, statusService);
        return new FilterHeaderLayout(filter);
    }

    private static MultiSelectComboBox<Status> createStatusMultiSelectComboBoxField(String placeHolder, Consumer<Set<Status>> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider, StatusService statusService) {
        MultiSelectComboBox<Status> msb = new MultiSelectComboBox<>();
        setupComboBoxBase(placeHolder, msb);

        msb.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
        msb.setItemLabelGenerator(Status::getStatus);
        msb.setItems(statusService::findAllByStatus);

        msb.addValueChangeListener(event -> {
            consumer.accept(new HashSet<>(event.getValue()));
            dataProvider.refreshAll();
        });
        return msb;
    }

    private static MultiSelectComboBox<User> createUserMultiSelectComboBoxField(String placeHolder, Consumer<Set<Long>> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider, UserService userService) {
        MultiSelectComboBox<User> msb = new MultiSelectComboBox<>();
        setupComboBoxBase(placeHolder, msb);

        msb.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
        msb.setItemLabelGenerator(User::getFullName);
        msb.setItems(userService::findAllByFirstnameAndLastnameAndEmail);

        msb.addValueChangeListener(event -> {
            consumer.accept(event.getValue().stream().map(User::getId).collect(Collectors.toSet()));
            dataProvider.refreshAll();
        });
        return msb;
    }

    private static MultiSelectComboBox<Product> createProductMultiSelectComboBoxField(String placeHolder, Consumer<Set<Long>> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider, ProductService productService) {
        MultiSelectComboBox<Product> msb = new MultiSelectComboBox<>();
        setupComboBoxBase(placeHolder, msb);

        msb.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
        msb.setItemLabelGenerator(Product::getName);

        msb.setItems(productService::findAllByName);

        msb.addValueChangeListener(event -> {
            consumer.accept(event.getValue().stream().map(Product::getId).collect(Collectors.toSet()));
            dataProvider.refreshAll();
        });
        return msb;
    }

    private static MultiSelectComboBox<Contact> createContactMultiSelectComboBoxField(String placeHolder, Consumer<Set<Long>> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider, ContactService contactService) {
        MultiSelectComboBox<Contact> msb = new MultiSelectComboBox<>();
        setupComboBoxBase(placeHolder, msb);

        msb.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
        msb.setItemLabelGenerator(Contact::getClient);

        msb.setItems(contactService::findAllByIcoOrClientOrEmailOrPhone);

        msb.addValueChangeListener(event -> {
            consumer.accept(event.getValue().stream().map(Contact::getId).collect(Collectors.toSet()));
            dataProvider.refreshAll();
        });
        return msb;
    }

    private static MultiSelectComboBox<cz.upce.fei.dt.backend.entities.Component> createComponentMultiSelectComboBoxField(String placeHolder, Consumer<Set<Long>> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider, ComponentService componentService) {
        MultiSelectComboBox<cz.upce.fei.dt.backend.entities.Component> msb = new MultiSelectComboBox<>();
        setupComboBoxBase(placeHolder, msb);

        msb.addThemeVariants(MultiSelectComboBoxVariant.LUMO_SMALL);
        msb.setItemLabelGenerator(cz.upce.fei.dt.backend.entities.Component::getName);

        msb.setItems(componentService::findAllByName);

        msb.addValueChangeListener(event -> {
            consumer.accept(event.getValue().stream().map(cz.upce.fei.dt.backend.entities.Component::getId).collect(Collectors.toSet()));
            dataProvider.refreshAll();
        });
        return msb;
    }

    private static void setupComboBoxBase(String placeholder, ComboBoxBase<?, ?, ?> comboBoxBase) {
        comboBoxBase.setPlaceholder(placeholder + "...");
        comboBoxBase.setClearButtonVisible(true);
        comboBoxBase.setWidthFull();
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
        setupNumberBase(integerField, placeholder);
        integerField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        integerField.addValueChangeListener(event -> {
            consumer.accept(event.getValue());
            dataProvider.refreshAll();
        });

        return integerField;
    }

    private static NumberField createNumberField(String placeholder, Consumer<Double> consumer, ConfigurableFilterDataProvider<?, ?, ?> dataProvider) {
        NumberField numberField = new NumberField();
        setupNumberBase(numberField, placeholder);
        numberField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        numberField.addValueChangeListener(event -> {
            consumer.accept(event.getValue());
            dataProvider.refreshAll();
        });

        return numberField;
    }

    private static void setupNumberBase(AbstractNumberField<?, ?> component, String placeholder) {
        setupTextBase(component, placeholder);
        component.setStepButtonsVisible(true);
        component.setValueChangeMode(ValueChangeMode.ON_CHANGE);
    }

    private static void setupTextBase(TextFieldBase<?, ?> component, String placeholder) {
        component.setPlaceholder(placeholder + "...");
        component.setClearButtonVisible(true);
        component.setValueChangeMode(ValueChangeMode.LAZY);
        component.setWidthFull();
    }
}
