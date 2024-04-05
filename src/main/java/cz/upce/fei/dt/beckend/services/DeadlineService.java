package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.upce.fei.dt.beckend.entities.Deadline;
import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.exceptions.AuthenticationException;
import cz.upce.fei.dt.beckend.repositories.DeadlineRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class DeadlineService {
    private final DeadlineRepository deadlineRepository;
    private final AuthenticationContext authenticationContext;

    public void save(Deadline deadline) throws AuthenticationException{
        Optional<Deadline> savedDeadline = deadlineRepository.findById(deadline.getId());
        if (savedDeadline.isPresent() && savedDeadline.get().equals(deadline))
            return;

        if (authenticationContext.getAuthenticatedUser(User.class).isEmpty())
            throw new AuthenticationException("Neznámý uživatel. Přihlašte se prosím.");
        User user = authenticationContext.getAuthenticatedUser(User.class).get();


        deadline.setUser(user);
        deadline.setId(null); // save as new deadline
        deadlineRepository.save(deadline);
    }
    public Stream<Deadline> findAllByContractId(Long contractId, int page, int pageSize){
        return deadlineRepository.findAllByContractId(contractId, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "created")))
                .stream()
                .map(iDeadline -> Deadline.builder()
                        .state(iDeadline.getState())
                        .user(User.builder()
                                .firstName(iDeadline.getUser().getFirstName())
                                .lastName(iDeadline.getUser().getLastName())
                                .build())
                        .deadline(iDeadline.getDeadline())
                        .created(iDeadline.getCreated())
                        .build());
    }
}
