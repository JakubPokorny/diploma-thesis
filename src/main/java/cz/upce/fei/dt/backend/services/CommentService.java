package cz.upce.fei.dt.backend.services;

import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.upce.fei.dt.backend.entities.Comment;
import cz.upce.fei.dt.backend.entities.Comment_;
import cz.upce.fei.dt.backend.entities.User;
import cz.upce.fei.dt.backend.exceptions.AuthenticationException;
import cz.upce.fei.dt.backend.repositories.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final AuthenticationContext authenticationContext;

    @Transactional
    public MessageListItem save(Comment comment) throws AuthenticationException {
        User authUser = authenticationContext.getAuthenticatedUser(User.class).orElseThrow(
                () -> new AuthenticationException("Neznámý uživatel. Přihlašte se prosím."));
        comment.setUser(authUser);
        Comment savedComment = commentRepository.save(comment);

        return new MessageListItem(
                savedComment.getComment(),
                savedComment.getCreated().toInstant(ZoneOffset.UTC),
                savedComment.getUser().getFullName());
    }

    public Stream<MessageListItem> findAllByContractId(Long contractId, Query<MessageListItem, Void> query) {
        return commentRepository.findAllByContractId(PageRequest.of(query.getPage(), query.getPageSize(), Sort.by(Sort.Direction.DESC, Comment_.CREATED)), contractId)
                .stream()
                .map(iComment -> new MessageListItem(
                        iComment.getComment(),
                        iComment.getCreated().toInstant(ZoneOffset.UTC),
                        iComment.getFirstName() + " " + iComment.getLastName())
                );
    }

    @Transactional
    public void updateAllUserByUser(Long userId, Long alternateUserId) {
        commentRepository.updateAllUserByUser(userId, alternateUserId);
    }
}
