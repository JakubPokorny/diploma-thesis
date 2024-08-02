package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import cz.upce.fei.dt.beckend.entities.Contract;
import cz.upce.fei.dt.beckend.entities.Note;
import cz.upce.fei.dt.beckend.services.NoteService;
import cz.upce.fei.dt.beckend.utilities.CzechI18n;

public class NoteForm extends Details {
    private final MessageInput messageInput = new MessageInput();
    Grid<MessageListItem> messageGrid = new Grid<>();
    private final NoteService noteService;


    public NoteForm(NoteService noteService, Contract contract) {
        this.setClassName("notes-layout");
        this.noteService = noteService;

        setupMessageInput(contract);
        setupMessageList(contract.getId());

        this.add(messageInput, messageGrid);
    }

    private void setupMessageList(Long contractId) {
        messageGrid.setClassName("notes-grid-layout");
        messageGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        messageGrid.addComponentColumn(messageListItem -> {
            MessageList messageList = new MessageList(messageListItem);
            messageList.setClassName("message-list");
            return messageList;
        });
        messageGrid.setAllRowsVisible(true);
        updateMessageList(contractId);
    }

    private void setupMessageInput(Contract contract) {
        messageInput.setClassName("message-input");
        messageInput.setWidthFull();
        messageInput.setI18n(CzechI18n.getMessageInputI18n());

        messageInput.getElement().addPropertyChangeListener("value", "change", event -> {
            int length = event.getValue().toString().length();
            messageInput.setTooltipText("%d/%d".formatted(length, Note.MAX_NOTE_LENGTH));

            if (length >= Note.MAX_NOTE_LENGTH)
                Notification.show("Poznámka je moc dlouhá. %d/%d".formatted(length, Note.MAX_NOTE_LENGTH)).addThemeVariants(NotificationVariant.LUMO_WARNING);
        });

        messageInput.addSubmitListener(submitEvent -> {
            Note note = Note.builder()
                    .contract(contract)
                    .note(submitEvent.getValue())
                    .build();
            try {
                if (note.getNote().length() >= Note.MAX_NOTE_LENGTH)
                    throw new Exception("Poznámka je moc dlouhá. %d/%d".formatted(note.getNote().length(), Note.MAX_NOTE_LENGTH));
                noteService.save(note);
                updateMessageList(contract.getId());
                Notification.show("Poznámka uložena.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception exception) {
                Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
    }


    private void updateMessageList(Long contractId) {
        long count = messageGrid.setItems(query -> noteService.findAllByContractId(contractId, query))
                .getItems()
                .count();
        this.setSummaryText("Poznámky (%d)".formatted(count));

        if (count > 0) {
            this.setOpened(true);
        }
    }
}
