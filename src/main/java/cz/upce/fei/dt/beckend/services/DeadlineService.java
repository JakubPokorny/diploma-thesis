package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.upce.fei.dt.beckend.entities.Deadline;
import cz.upce.fei.dt.beckend.entities.Status;
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

    public Stream<Deadline> findAllByContractId(Long contractId, int page, int pageSize) {
        return deadlineRepository.findAllByContractId(contractId, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "created")))
                .stream()
                .map(iDeadline -> Deadline.builder()
                        .status(iDeadline.getStatus())
                        .user(User.builder()
                                .firstName(iDeadline.getUser().getFirstName())
                                .lastName(iDeadline.getUser().getLastName())
                                .build())
                        .deadline(iDeadline.getDeadline())
                        .created(iDeadline.getCreated())
                        .build());
    }

    public Deadline findFirstByContractIdOrderByCreatedDesc(Long contractId) {
        return deadlineRepository.findFirstByContractIdOrderByCreatedDesc(contractId).orElse(new Deadline());
    }

    public List<Deadline> findAllCurrentDeadlinesByStatus(Status.Theme theme) {
        return deadlineRepository.findAllCurrentDeadlinesByStatus(theme);
    }

    public List<Deadline> findAllCurrentDeadlines() {
        return deadlineRepository.findAllCurrentDeadlines();
    }

    public int countAfterDeadline() {
        return deadlineRepository.countAfterDeadline();
    }

    public int countWithoutDeadline() {
        return deadlineRepository.countWithoutDeadline();
    }

    public int countBeforeDeadline() {
        return deadlineRepository.countBeforeDeadline();
    }

}
