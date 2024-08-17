package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import cz.upce.fei.dt.backend.entities.Deadline;
import cz.upce.fei.dt.backend.entities.Status;
import cz.upce.fei.dt.backend.services.DeadlineService;
import cz.upce.fei.dt.backend.services.StatusService;
import cz.upce.fei.dt.backend.utilities.CzechI18n;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Getter
@Setter
public class DeadlineForm extends FormLayout implements IEditForm<Deadline> {
    private final Binder<Deadline> binder = new BeanValidationBinder<>(Deadline.class);
    private final ComboBox<Status> state = new ComboBox<>("Stav");
    private final DatePicker deadlineDate = new DatePicker("Deadline");
    private final Button historyButton = new Button("Historie");
    private final Grid<Deadline> historyGrid= new Grid<>();
    private Deadline deadline = new Deadline();
    private final DeadlineService deadlineService;

    public DeadlineForm(DeadlineService deadlineService, StatusService statusService) {
        this.deadlineService = deadlineService;
        this.setResponsiveSteps(
                new ResponsiveStep("0", 1),
                new ResponsiveStep("500px", 8)
        );
        this.setColspan(historyButton, 2);

        setupGrid();

        state.setItems(statusService::findAllByStatus);
        state.setItemLabelGenerator(Status::getStatus);
        binder.forField(state)
                .asRequired()
                .bind(Deadline::getStatus, Deadline::setStatus);

        deadlineDate.setI18n(CzechI18n.getDatePickerI18n());
        binder.forField(deadlineDate)
                //.withValidator(localDate -> localDate == null || localDate.isAfter(LocalDate.now().minusDays(1)), "Vyberte Deadline v budoucnosti.")
                .bind(Deadline::getDeadline, Deadline::setDeadline);

        historyButton.addClickListener(this::showHistory);

        add(state, deadlineDate, historyButton, historyGrid);
    }

    private void setupGrid() {
        this.setColspan(historyGrid, 8);
        historyGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        historyGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        historyGrid.setVisible(false);

        historyGrid.addColumn(new TextRenderer<>(deadline -> deadline.getStatus().getStatus())).setHeader("Stav");
        historyGrid.addColumn(new LocalDateRenderer<>(Deadline::getDeadline, "d. M. yyyy")).setHeader("Deadline");
        historyGrid.addColumn(new TextRenderer<>(deadline-> "%s, %s".formatted(
                deadline.getUser().getFullName(),
                deadline.getCreated().format(DateTimeFormatter.ofPattern("H:mm d. M. yyyy"))))
        ).setHeader("Nastavil");
        //historyGrid.addColumn(new LocalDateTimeRenderer<>(Deadline::getCreated, "H:mm d. M. yyyy"));
    }

    private void showHistory(ClickEvent<Button> event) {
        historyGrid.setVisible(!historyGrid.isVisible());
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
            state.setLabel("Stav: %s, %s".formatted(
                    deadline.getUser().getFullName(),
                    deadline.getUpdated().format(DateTimeFormatter.ofPattern("H:mm d. M. yyyy"))));

            historyButton.setVisible(true);
            this.setColspan(state, 3);
            this.setColspan(deadlineDate, 3);
            historyGrid.setItems(query -> deadlineService.findAllByContractId(deadline.getContract().getId(), query));
        } else {
            deadline = new Deadline();
            state.setLabel("Stav");
            historyButton.setVisible(false);
            historyGrid.setVisible(false);
            historyGrid.setItems(Collections.emptyList());
            this.setColspan(state, 4);
            this.setColspan(deadlineDate, 4);
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
