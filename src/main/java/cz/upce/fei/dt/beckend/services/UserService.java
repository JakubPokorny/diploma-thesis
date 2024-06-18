package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.repositories.UserRepository;
import cz.upce.fei.dt.beckend.services.filters.UserFilter;
import cz.upce.fei.dt.beckend.services.specifications.UserSpec;
import cz.upce.fei.dt.ui.views.users.PasswordView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.InvalidClassException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class UserService extends AbstractBackEndDataProvider<User, UserFilter> {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Stream<User> fetchFromBackEnd(Query<User, UserFilter> query) {
        Specification<User> spec = UserSpec.filterBy(query.getFilter().orElse(null));
        Stream<User> stream = userRepository.findAll(spec, VaadinSpringDataHelpers.toSpringDataSort(query)).stream();
        return stream.skip(query.getOffset()).limit(query.getLimit());
    }

    @Override
    public int sizeInBackEnd(Query<User, UserFilter> query) {
        return (int) fetchFromBackEnd(query).count();
    }

    public Stream<User> findAllByFirstnameAndLastnameAndEmail(Query<User, String> query) {
        String searchTerm = query.getFilter().orElse("");
        return userRepository.findAllByFirstnameAndLastnameAndEmail(VaadinSpringDataHelpers.toSpringPageRequest(query), searchTerm)
                .stream()
                .map(iUser -> User.builder()
                        .id(iUser.getId())
                        .firstName(iUser.getFirstName())
                        .lastName(iUser.getLastName())
                        .email(iUser.getEmail())
                        .build()
                );
    }

    public void saveUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isEmpty()) {
            String resetToken = sendResetToken(user);
            user.setPassword(passwordEncoder.encode(resetToken));
        }
        userRepository.save(user);
    }

    public void changePassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setResetToken("");
        userRepository.save(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public void generateResetToken(User user) throws InvalidClassException {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            sendResetToken(user);
            userRepository.save(user);
        } else {
            throw new InvalidClassException(user.getEmail() + "nebyl najit.");
        }
    }

    private String sendResetToken(User user) {
        String resetToken = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        user.setResetToken(resetToken);
        UI.getCurrent().getPage().fetchCurrentURL(currentUrl -> {
            String url = String.format("%s://%s:%s/%s",
                    currentUrl.getProtocol(),
                    currentUrl.getHost(),
                    currentUrl.getPort(),
                    RouteConfiguration.forSessionScope().getUrl(PasswordView.class, resetToken));
            try {
                emailService.send(
                        user.getEmail(),
                        "Nastav si heslo.",
                        "Pro nastavení hesla přejdi na " + url
                );
                Notification.show("Email pro nastavení hesla odeslán.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception exception) {
                Notification.show(exception.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        return resetToken;
    }


}
