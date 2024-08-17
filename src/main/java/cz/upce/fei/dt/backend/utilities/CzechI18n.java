package cz.upce.fei.dt.backend.utilities;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.messages.MessageInputI18n;
import com.vaadin.flow.component.upload.UploadI18N;
import cz.upce.fei.dt.backend.entities.Note;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class CzechI18n {
    private final static DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = DecimalFormatSymbols.getInstance(Locale.forLanguageTag("cs-CZ"));
    private final static DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,###.00 ¤", DECIMAL_FORMAT_SYMBOLS);
    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###.00", DECIMAL_FORMAT_SYMBOLS);

    public static String getCurrency(Double number) {
        return CURRENCY_FORMAT.format(number);
    }

    public static String getDecimal(Double number) {
        return DECIMAL_FORMAT.format(number);
    }

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

    public static UploadI18N getUploadI18n() {
        UploadI18N czech = new UploadI18N();

        czech.setAddFiles(new UploadI18N.AddFiles()
                .setOne("Nahrát soubor")
                .setMany("Nahrát soubory"));
        czech.setDropFiles(new UploadI18N.DropFiles()
                .setOne("Nahrát soubor")
                .setMany("Nahrát soubory"));
        czech.setError(new UploadI18N.Error()
                .setFileIsTooBig("Soubor je přiliš velký.")
                .setTooManyFiles("Příliš mnoho souborů")
                .setIncorrectFileType("Nesprávný souborový typ"));
        czech.setFile(new UploadI18N.File()
                .setRemove("Odstranit")
                .setRetry("Opakovat")
                .setStart("Zahájit"));
        czech.setUploading(new UploadI18N.Uploading()
                .setError(new UploadI18N.Uploading.Error()
                        .setForbidden("Nepovolený přístup.")
                        .setServerUnavailable("Server je nedostupný, zkuste to později.")
                        .setUnexpectedServerError("Na serveru nastala neočekávaná chyba, zkuste to později."))
                .setStatus(new UploadI18N.Uploading.Status()
                        .setConnecting("Připojuji se")
                        .setHeld("Pozastaveno")
                        .setProcessing("Zpracovávám")
                        .setStalled("Zastaveno"))
                .setRemainingTime(new UploadI18N.Uploading.RemainingTime()
                        .setPrefix("Zbývá ")
                        .setUnknown("Neznámý")));
        return czech;
    }

    public static MessageInputI18n getMessageInputI18n() {
        MessageInputI18n czech = new MessageInputI18n();
        czech.setSend("Poslat");
        czech.setMessage("Poznámka (max %d znaků)".formatted(Note.MAX_NOTE_LENGTH));
        return czech;
    }

    public static LoginI18n getLoginI18n() {
        LoginI18n loginI18n = LoginI18n.createDefault();
        LoginI18n.Header header = new LoginI18n.Header();
        header.setTitle("BOXE");
        header.setDescription("Dip práce 2024");
        loginI18n.setHeader(header);

        LoginI18n.Form form = loginI18n.getForm();
        form.setTitle("Přihlášení");
        form.setUsername("Email");
        form.setPassword("Heslo");
        form.setSubmit("Přihlásit se");
        form.setForgotPassword("Zapomenuté heslo");
        loginI18n.setForm(form);

        LoginI18n.ErrorMessage errorMessage = loginI18n.getErrorMessage();
        errorMessage.setTitle("Email nebo heslo je špatně.");
        errorMessage.setMessage("Zkontroluj si zadaný email a heslo a zkus to znova.");
        loginI18n.setErrorMessage(errorMessage);
        return loginI18n;
    }
}
