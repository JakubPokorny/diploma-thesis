package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import cz.upce.fei.dt.beckend.entities.Deadline;
import cz.upce.fei.dt.beckend.entities.State;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
public class DeadlineForm extends FormLayout implements IEditForm<Deadline> {
    private final Binder<Deadline> binder = new BeanValidationBinder<>(Deadline.class);
    private final ComboBox<State> state = new ComboBox<>("Stav");
    private final DatePicker deadlineDate = new DatePicker("Deadline");
    private final Span blame = new Span();
    private Deadline deadline = new Deadline();

    public DeadlineForm() {

        state.setItems(State.values());
        state.setItemLabelGenerator(State::getName);
        state.setValue(State.CREATED);
        binder.forField(state)
                .asRequired()
                .bind(Deadline::getState, Deadline::setState);

        deadlineDate.setI18n(getCzechI18n());
        binder.forField(deadlineDate)
                .withValidator(localDate -> localDate == null || localDate.isAfter(LocalDate.now().minusDays(1)), "Vyberte Deadline v budoucnosti.")
                .bind(Deadline::getDeadline, Deadline::setDeadline);

        blame.setWidthFull();
        add(state, deadlineDate);
    }

    private static DatePicker.DatePickerI18n getCzechI18n() {
        DatePicker.DatePickerI18n czech = new DatePicker.DatePickerI18n();
        czech.setMonthNames(List.of("Leden", "Únor", "Březen", "Duben", "Květen", "Červen", "Červenec", "Srpen", "Září", "Říjen", "Listopad", "Prosinec"));
        czech.setWeekdays(List.of("Pondělí", "Úterý", "Středa", "Čtvrtek", "Pátek", "Sobota", "Neděle"));
        czech.setWeekdaysShort(List.of("Po", "Út", "St", "Čt", "Pá", "So", "Ne"));
        czech.setToday("Dnes");
        czech.setCancel("Zrušit");
        czech.setDateFormat("d. M. yyyy");
        return czech;
    }

    //region IEditForm
    @Override
    public Deadline getValue() {
        return deadline;
    }

    @Override
    public void setValue(Deadline value) {
        deadline = value;
        if (value != null && deadline.getUser() != null) {
            blame.setText("Nastavil: %s, %s".formatted(
                    deadline.getUser().getFullName(),
                    deadline.getUpdated().format(DateTimeFormatter.ofPattern("H:m d. M. yyyy"))));
            add(blame);
        } else {
            deadline = new Deadline();
        }
        binder.readBean(deadline);
    }

    @Override
    public void validate() throws ValidationException {
        binder.writeBean(deadline);
    }

    @Override
    public void expand(boolean expended) {

    }
    //endregion
}
