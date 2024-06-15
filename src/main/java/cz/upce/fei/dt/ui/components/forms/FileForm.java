package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.upload.StartedEvent;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import cz.upce.fei.dt.beckend.entities.Contract;
import cz.upce.fei.dt.beckend.entities.File;
import cz.upce.fei.dt.beckend.services.FileService;
import cz.upce.fei.dt.beckend.utilities.CzechI18n;
import jakarta.validation.*;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;

public class FileForm extends Details {
    private final MultiFileMemoryBuffer multiFileMemoryBuffer = new MultiFileMemoryBuffer();
    private final Upload upload = new Upload(multiFileMemoryBuffer);
    private final Grid<File> downloadArea = new Grid<>();
    private final FileService fileService;
    private final Contract contract;

    public FileForm(FileService fileService, Contract contract) {
        this.setClassName("file-layout");
        this.fileService = fileService;
        this.contract = contract;

        setupUpload();
        setupDownload();

        this.add(upload, downloadArea);
    }

    private void setupDownload() {
        downloadArea.setClassName("files-grid-layout");
        downloadArea.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_WRAP_CELL_CONTENT);
        downloadArea.addComponentColumn(this::createDownloadLink).setAutoWidth(true).setFlexGrow(1);
        downloadArea.addComponentColumn(this::createDeleteButton).setAutoWidth(true).setFlexGrow(0);

        downloadArea.setAllRowsVisible(true);
        updateDownloadArea();
    }

    private Component createDeleteButton(File file) {
        Button deleteFile = new Button(new Icon(VaadinIcon.TRASH));
        deleteFile.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteFile.setSizeFull();
        deleteFile.addClickListener(event -> {
            try {
                fileService.delete(file);
                Notification.show("Soubor %s smazán.".formatted(file.getName())).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception exception) {
                Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_WARNING);
            }
            updateDownloadArea();
        });
        return deleteFile;
    }

    private Component createDownloadLink(File file) {
        try {
            byte[] bytes = fileService.getFile(file);
            StreamResource streamResource = new StreamResource(
                    file.getName(),
                    (InputStreamFactory) () -> new ByteArrayInputStream(bytes));

            Anchor anchor = new Anchor(
                    streamResource,
                    String.format("%s (%d KB)", file.getName(), file.getSize() / 1024));
            anchor.getElement().setAttribute("download", true);
            return anchor;
        } catch (IOException ioException) {
            Notification.show(ioException.getLocalizedMessage()).addThemeVariants(NotificationVariant.LUMO_WARNING);
            return new Span("%s: %s".formatted(file.getName(), ioException.getLocalizedMessage()));
        } catch (S3Exception s3Exception) {
            Notification.show(" %s chyba s uložištem.".formatted(file.getName())).addThemeVariants(NotificationVariant.LUMO_WARNING);
            return new Span("%s: chyba s uložištěm.".formatted(file.getName()));
        }
    }

    private void updateDownloadArea() {
        long count = downloadArea.setItems(query -> fileService.findAllByContractId(contract.getId(), query.getPage(), query.getPageSize()))
                .getItems()
                .count();
        this.setSummaryText("Soubory (%d), max 10MB na soubor".formatted(count));

        if (count > 0) {
            this.setOpened(true);
        }
    }

    private void setupUpload() {
        upload.setI18n(CzechI18n.getUploadI18n());
        upload.setWidthFull();
        upload.setMaxFileSize(10 * 1024 * 1024);
        upload.addFileRejectedListener(event -> Notification.show(event.getErrorMessage() + " max 10MB").addThemeVariants(NotificationVariant.LUMO_ERROR));
        upload.addStartedListener(this::validate);
        upload.addSucceededListener(this::save);
    }

    private void validate(StartedEvent event) {
        File file = File.builder()
                .name(event.getFileName())
                .type(event.getMIMEType())
                .size(event.getContentLength())
                .contract(contract)
                .path("%s/%s".formatted(contract.getId(), event.getFileName()))
                .build();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<File>> violations = validator.validate(file);

        if (!violations.isEmpty()) {
            violations.forEach(violation -> Notification.show(violation.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR));
            upload.interruptUpload();
        }
    }

    private void save(SucceededEvent event) {
        try {
            File file = File.builder()
                    .name(event.getFileName())
                    .type(event.getMIMEType())
                    .size(event.getContentLength())
                    .contract(contract)
                    .path("%s/%s".formatted(contract.getId(), event.getFileName()))
                    .build();

            fileService.saveFile(file, multiFileMemoryBuffer.getInputStream(file.getName()));
            Notification.show("Soubor %s nahrán.".formatted(event.getFileName())).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            updateDownloadArea();
        } catch (NotFoundException | ValidationException exception) {
            Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
