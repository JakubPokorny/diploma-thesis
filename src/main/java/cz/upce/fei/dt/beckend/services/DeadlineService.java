package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.upce.fei.dt.beckend.entities.Deadline;
import cz.upce.fei.dt.beckend.entities.Deadline_;
import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.exceptions.AuthenticationException;
import cz.upce.fei.dt.beckend.repositories.DeadlineRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class DeadlineService {
    private final DeadlineRepository deadlineRepository;
    private final AuthenticationContext authenticationContext;

    @Transactional
    public void save(Deadline deadline) throws AuthenticationException {
        if (deadline.getId() != null) {
            Optional<Deadline> savedDeadline = deadlineRepository.findById(deadline.getId());
            if (savedDeadline.isPresent() && savedDeadline.get().equals(deadline))
                return;
        }

        User user = authenticationContext.getAuthenticatedUser(User.class)
                .orElseThrow(() -> new AuthenticationException("Neznámý uživatel. Přihlašte se prosím."));

        deadline.setUser(user);
        deadline.setId(null); // save as new deadline
        deadlineRepository.save(deadline);
    }

    @Transactional
    public void deleteAll(Long contractId) {
        deadlineRepository.deleteAllByContractId(contractId);
    }

    public Stream<Deadline> findAllByContractId(Long contractId, Query<Deadline, Void> query) {
        return deadlineRepository.findAllByContractId(PageRequest.of(query.getPage(), query.getPageSize(), Sort.by(Sort.Direction.DESC, Deadline_.CREATED)), contractId)
                .stream()
                .map(iDeadline -> Deadline.builder()
                        .id(iDeadline.getId())
                        .status(iDeadline.getStatus())
                        .user(User.builder()
                                .firstName(iDeadline.getFirstName())
                                .lastName(iDeadline.getLastName())
                                .build())
                        .deadline(iDeadline.getDeadline())
                        .created(iDeadline.getCreated())
                        .build());
    }

    public Deadline findFirstByContractIdOrderByCreatedDesc(Long contractId) {
        return deadlineRepository.findFirstByContractIdOrderByCreatedDesc(contractId).orElse(new Deadline());
    }

    public List<Deadline> findAllCurrentDeadlinesByStatusId(Long statusId) {
        return deadlineRepository.findAllCurrentDeadlinesByStatusId(statusId);
    }

    public List<Deadline> findAllCurrentDeadlines() {
        return deadlineRepository.findAllCurrentDeadlines();
    }

    @Transactional
    public void updateAllUserByUser(Long userId, Long alternateUserId) {
        deadlineRepository.updateAllUserByUser(userId, alternateUserId);
    }
}
