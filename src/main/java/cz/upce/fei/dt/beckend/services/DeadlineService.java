package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.upce.fei.dt.beckend.entities.Deadline;
import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.repositories.DeadlineRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class DeadlineService {
    private final DeadlineRepository deadlineRepository;
    private final AuthenticationContext authenticationContext;

    public void save(Deadline deadline) {
        if (authenticationContext.getAuthenticatedUser(User.class).isEmpty())
            return; //todo error
        User user = authenticationContext.getAuthenticatedUser(User.class).get();

        if (deadline.getId() == null){
            deadline.setUser(user);
        }else{
            Optional<Deadline> savedDeadline = deadlineRepository.findById(deadline.getId());
            if (savedDeadline.isPresent() && !savedDeadline.get().equals(deadline))
                deadline.setUser(user);
        }
        deadlineRepository.save(deadline);
    }
}
