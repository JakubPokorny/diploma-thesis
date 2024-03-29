package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.upce.fei.dt.beckend.entities.Note;
import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.repositories.NoteRepository;
import jakarta.security.auth.message.AuthException;
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
    public MessageListItem save(Note note) throws AuthException{
        User user = authenticationContext.getAuthenticatedUser(User.class).orElseThrow(AuthException::new);
        note.setUser(user);
        Note savedNote = noteRepository.save(note);

        return new MessageListItem(
                savedNote.getNote(),
                savedNote.getCreated().toInstant(ZoneOffset.UTC),
                savedNote.getUser().getFullName());
    }

    public Stream<MessageListItem> findAllByContractId(Long contractId, int page, int pageSize){
        return noteRepository.findAllByContractId(contractId, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "created")))
                .stream()
                .map(iNote -> new MessageListItem(
                        iNote.getNote(),
                        iNote.getCreated().toInstant(ZoneOffset.UTC),
                        iNote.getUser().getFirstName() + " " + iNote.getUser().getLastName())
                );
    }
}
