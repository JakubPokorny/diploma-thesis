package cz.upce.fei.dt.beckend.utilities;

import com.vaadin.flow.component.datepicker.DatePicker;

import java.util.List;

public class CzechI18n {
    public static DatePicker.DatePickerI18n getDatePickerI18n() {
        DatePicker.DatePickerI18n czech = new DatePicker.DatePickerI18n();
        czech.setMonthNames(List.of("Leden", "Únor", "Březen", "Duben", "Květen", "Červen", "Červenec", "Srpen", "Září", "Říjen", "Listopad", "Prosinec"));
        czech.setWeekdays(List.of("Pondělí", "Úterý", "Středa", "Čtvrtek", "Pátek", "Sobota", "Neděle"));
        czech.setWeekdaysShort(List.of("Po", "Út", "St", "Čt", "Pá", "So", "Ne"));
        czech.setToday("Dnes");
        czech.setCancel("Zrušit");
        czech.setDateFormat("d. M. yyyy");
        return czech;
    }
}
