package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.upce.fei.dt.beckend.entities.Note;
import cz.upce.fei.dt.beckend.entities.Note_;
import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.exceptions.AuthenticationException;
import cz.upce.fei.dt.beckend.repositories.NoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final AuthenticationContext authenticationContext;

    @Transactional
    public MessageListItem save(Note note) throws AuthenticationException {
        User authUser = authenticationContext.getAuthenticatedUser(User.class).orElseThrow(
                () -> new AuthenticationException("Neznámý uživatel. Přihlašte se prosím."));
        note.setUser(authUser);
        Note savedNote = noteRepository.save(note);

        return new MessageListItem(
                savedNote.getNote(),
                savedNote.getCreated().toInstant(ZoneOffset.UTC),
                savedNote.getUser().getFullName());
    }

    public Stream<MessageListItem> findAllByContractId(Long contractId, Query<MessageListItem, Void> query) {
        return noteRepository.findAllByContractId(PageRequest.of(query.getPage(), query.getPageSize(), Sort.by(Sort.Direction.DESC, Note_.CREATED)), contractId)
                .stream()
                .map(iNote -> new MessageListItem(
                        iNote.getNote(),
                        iNote.getCreated().toInstant(ZoneOffset.UTC),
                        iNote.getFirstName() + " " + iNote.getLastName())
                );
    }

    @Transactional
    public void updateAllUserByUser(Long userId, Long alternateUserId) {
        noteRepository.updateAllUserByUser(userId, alternateUserId);
    }
}
