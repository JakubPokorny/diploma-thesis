package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageInputI18n;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import cz.upce.fei.dt.beckend.entities.Contract;
import cz.upce.fei.dt.beckend.entities.Note;
import cz.upce.fei.dt.beckend.services.NoteService;

public class NoteForm extends VerticalLayout {
    private final MessageInput messageInput = new MessageInput();
    Grid<MessageListItem> messageGrid = new Grid<>();
    private final NoteService noteService;
    private final Span noNote = new Span("0 poznámek.");
    private final Span title = new Span("Poznámky");


    public NoteForm(NoteService noteService, Contract contract) {
        this.setClassName("notes-layout");
        this.noteService = noteService;

        add(title, messageInput, messageGrid);

        configureMessageInput(contract);
        configureMessageList(contract.getId());
    }

    private void configureMessageList(Long contractId) {
        messageGrid.setClassName("notes-grid-layout");
        messageGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        messageGrid.addComponentColumn(messageListItem -> {
            MessageList messageList = new MessageList(messageListItem);
            messageList.setClassName("message-list");
            return messageList;
        });
        updateMessageList(contractId);
    }

    private void configureMessageInput(Contract contract) {
        messageInput.setClassName("message-input");
        messageInput.setWidthFull();
        messageInput.setI18n(getCzechI18n());

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
    private static MessageInputI18n getCzechI18n(){
        MessageInputI18n czech = new MessageInputI18n();
        czech.setSend("Poslat");
        czech.setMessage("Poznámka (max %d znaků)".formatted(Note.MAX_NOTE_LENGTH));
        return czech;
    }

    private void updateMessageList(Long contractId){
        long count = messageGrid.setItems(query -> noteService.findAllByContractId(contractId, query.getPage(), query.getPageSize()))
                .getItems()
                .count();
        title.setText("Poznámky (%d)".formatted(count));

        if (count == 0){
            messageGrid.setVisible(false);
            this.add(noNote);
        } else {
            messageGrid.setVisible(true);
            remove(noNote);
        }
    }
}
