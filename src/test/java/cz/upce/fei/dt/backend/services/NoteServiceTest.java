package cz.upce.fei.dt.backend.services;

import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.upce.fei.dt.backend.dto.INote;
import cz.upce.fei.dt.backend.entities.Note;
import cz.upce.fei.dt.backend.entities.User;
import cz.upce.fei.dt.backend.exceptions.AuthenticationException;
import cz.upce.fei.dt.backend.repositories.NoteRepository;
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
class NoteServiceTest {
    @Mock
    private NoteRepository noteRepository;
    @Mock
    private AuthenticationContext authenticationContext;

    @InjectMocks
    private NoteService noteService;

    @Test
    void saveThrowsAuthenticationExceptionWhenNoOneIsAuthenticated() {
        Exception exception = assertThrows(AuthenticationException.class, () -> noteService.save(mock(Note.class)));
        assertEquals(exception.getMessage(), "Neznámý uživatel. Přihlašte se prosím.");

        verify(authenticationContext).getAuthenticatedUser(User.class);
    }

    @Test
    void saveNote() {
        User authUser = mock(User.class);
        when(authUser.getFullName()).thenReturn("Joe Doe");

        LocalDateTime created = LocalDateTime.now();
        Note note = Note.builder()
                .id(1L)
                .note("Lorem ipsum dolor sit amet")
                .user(authUser)
                .created(created)
                .build();

        when(authenticationContext.getAuthenticatedUser(User.class)).thenReturn(Optional.of(authUser));
        when(noteRepository.save(note)).thenReturn(note);

        MessageListItem message = noteService.save(note);

        assertEquals(message.getText(), "Lorem ipsum dolor sit amet");
        assertEquals(message.getUserName(), "Joe Doe");
        assertEquals(message.getTime(), created.toInstant(ZoneOffset.UTC));

        verify(authenticationContext).getAuthenticatedUser(User.class);
        verify(noteRepository).save(note);
    }

    @Test
    void findAllByContractId() {
        INote iNote = mock(INote.class);
        when(iNote.getNote()).thenReturn("Lorem ipsum dolor sit amet");
        when(iNote.getFirstName()).thenReturn("Joe");
        when(iNote.getLastName()).thenReturn("Doe");
        LocalDateTime created = LocalDateTime.now();
        when(iNote.getCreated()).thenReturn(created);

        List<INote> iNotes = List.of(iNote);
        Page<INote> page = new PageImpl<>(iNotes);

        when(noteRepository.findAllByContractId(any(), anyLong())).thenReturn(page);

        Stream<MessageListItem> result = noteService.findAllByContractId(1L, new Query<>());

        List<MessageListItem> resultList = result.toList();

        MessageListItem message = resultList.getFirst();
        assertEquals(message.getText(), iNote.getNote());
        assertEquals(message.getTime(), iNote.getCreated().toInstant(ZoneOffset.UTC));
        assertEquals(message.getUserName(), iNote.getFirstName() + " " + iNote.getLastName());

        verify(noteRepository).findAllByContractId(any(), anyLong());
    }

    @Test
    void updateAllUserByUser() {
        noteService.updateAllUserByUser(1L, 2L);
        verify(noteRepository).updateAllUserByUser(1L, 2L);
    }
}