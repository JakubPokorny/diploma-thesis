package cz.upce.fei.dt.ui.components.forms;

import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import cz.upce.fei.dt.backend.entities.Comment;
import cz.upce.fei.dt.backend.entities.Contract;
import cz.upce.fei.dt.backend.services.CommentService;
import cz.upce.fei.dt.backend.utilities.CzechI18n;

public class CommentForm extends Details {
    private final MessageInput messageInput = new MessageInput();
    Grid<MessageListItem> messageGrid = new Grid<>();
    private final CommentService commentService;


    public CommentForm(CommentService commentService, Contract contract) {
        this.setClassName("notes-layout");
        this.commentService = commentService;

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
            messageInput.setTooltipText("%d/%d".formatted(length, Comment.MAX_COMMENT_LENGTH));

            if (length >= Comment.MAX_COMMENT_LENGTH)
                Notification.show("Komentář je moc dlouhý. %d/%d".formatted(length, Comment.MAX_COMMENT_LENGTH)).addThemeVariants(NotificationVariant.LUMO_WARNING);
        });

        messageInput.addSubmitListener(submitEvent -> {
            Comment comment = Comment.builder()
                    .contract(contract)
                    .comment(submitEvent.getValue())
                    .build();
            try {
                if (comment.getComment().length() >= Comment.MAX_COMMENT_LENGTH)
                    throw new Exception("Komentář je moc dlouhý. %d/%d".formatted(comment.getComment().length(), Comment.MAX_COMMENT_LENGTH));
                commentService.save(comment);
                updateMessageList(contract.getId());
                Notification.show("Komentář uložen.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception exception) {
                Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
    }


    private void updateMessageList(Long contractId) {
        long count = messageGrid.setItems(query -> commentService.findAllByContractId(contractId, query))
                .getItems()
                .count();
        this.setSummaryText("Komentáře (%d)".formatted(count));

        if (count > 0) {
            this.setOpened(true);
        }
    }
}
