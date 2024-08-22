package cz.upce.fei.dt.backend.services;

import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.upce.fei.dt.backend.dto.IComment;
import cz.upce.fei.dt.backend.entities.Comment;
import cz.upce.fei.dt.backend.entities.User;
import cz.upce.fei.dt.backend.exceptions.AuthenticationException;
import cz.upce.fei.dt.backend.repositories.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private AuthenticationContext authenticationContext;

    @InjectMocks
    private CommentService commentService;

    @Test
    void saveThrowsAuthenticationExceptionWhenNoOneIsAuthenticated() {
        Exception exception = assertThrows(AuthenticationException.class, () -> commentService.save(mock(Comment.class)));
        assertEquals(exception.getMessage(), "Neznámý uživatel. Přihlašte se prosím.");

        verify(authenticationContext).getAuthenticatedUser(User.class);
    }

    @Test
    void saveNote() {
        User authUser = mock(User.class);
        when(authUser.getFullName()).thenReturn("Joe Doe");

        LocalDateTime created = LocalDateTime.now();
        Comment comment = Comment.builder()
                .id(1L)
                .comment("Lorem ipsum dolor sit amet")
                .user(authUser)
                .created(created)
                .build();

        when(authenticationContext.getAuthenticatedUser(User.class)).thenReturn(Optional.of(authUser));
        when(commentRepository.save(comment)).thenReturn(comment);

        MessageListItem message = commentService.save(comment);

        assertEquals(message.getText(), "Lorem ipsum dolor sit amet");
        assertEquals(message.getUserName(), "Joe Doe");
        assertEquals(message.getTime(), created.toInstant(ZoneOffset.UTC));

        verify(authenticationContext).getAuthenticatedUser(User.class);
        verify(commentRepository).save(comment);
    }

    @Test
    void findAllByContractId() {
        IComment iComment = mock(IComment.class);
        when(iComment.getComment()).thenReturn("Lorem ipsum dolor sit amet");
        when(iComment.getFirstName()).thenReturn("Joe");
        when(iComment.getLastName()).thenReturn("Doe");
        LocalDateTime created = LocalDateTime.now();
        when(iComment.getCreated()).thenReturn(created);

        List<IComment> iComments = List.of(iComment);
        Page<IComment> page = new PageImpl<>(iComments);

        when(commentRepository.findAllByContractId(any(), anyLong())).thenReturn(page);

        Stream<MessageListItem> result = commentService.findAllByContractId(1L, new Query<>());

        List<MessageListItem> resultList = result.toList();

        MessageListItem message = resultList.getFirst();
        assertEquals(message.getText(), iComment.getComment());
        assertEquals(message.getTime(), iComment.getCreated().toInstant(ZoneOffset.UTC));
        assertEquals(message.getUserName(), iComment.getFirstName() + " " + iComment.getLastName());

        verify(commentRepository).findAllByContractId(any(), anyLong());
    }

    @Test
    void updateAllUserByUser() {
        commentService.updateAllUserByUser(1L, 2L);
        verify(commentRepository).updateAllUserByUser(1L, 2L);
    }
}