package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.spring.security.AuthenticationContext;
import cz.upce.fei.dt.beckend.entities.Role;
import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.exceptions.AuthenticationException;
import cz.upce.fei.dt.beckend.exceptions.ResourceNotFoundException;
import cz.upce.fei.dt.beckend.repositories.UserRepository;
import cz.upce.fei.dt.beckend.services.filters.UserFilter;
import cz.upce.fei.dt.beckend.services.specifications.UserSpec;
import cz.upce.fei.dt.ui.views.ChangePasswordView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class UserService extends AbstractBackEndDataProvider<User, UserFilter> {
    private final AuthenticationContext authenticationContext;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final DeadlineService deadlineService;
    private final NoteService noteService;
    private final ComponentService componentService;

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

    @Transactional
    public void saveUser(User user) throws AuthenticationException {
        User authUser = authenticationContext.getAuthenticatedUser(User.class)
                .orElseThrow(() -> new AuthenticationException("Neznámý uživatel. Přihlašte se prosím."));

        Optional<User> savedUser = userRepository.findByEmail(user.getEmail());
        if (savedUser.isPresent() && user.getRole() != savedUser.get().getRole()) {
            if (!authUser.hasRole(Role.ADMIN))
                throw new AuthenticationException(authUser.getRole() + " nemůže měnit oprávnění");
        }

        if (savedUser.isEmpty()) {
            String resetToken = sendResetToken(user.getEmail());
            user.setResetToken(resetToken);
            user.setPassword(passwordEncoder.encode(resetToken));
        }
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(User user, User alternateUser) {
        if (user == null || alternateUser == null)
            return;

        Long userId = user.getId();
        Long alternateUserId = alternateUser.getId();

        deadlineService.updateAllUserByUser(userId, alternateUserId);
        noteService.updateAllUserByUser(userId, alternateUserId);
        componentService.updateAllUserByUser(userId, alternateUserId);

        userRepository.delete(user);
    }

    public User findByResetToken(String token) throws NotFoundException {
        return userRepository.findByResetToken(token).orElseThrow(
                () -> new NotFoundException("Neznámý token."));
    }

    public void changePassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setResetToken("");
        userRepository.save(user);
    }

    public void generateResetToken(User user) throws ResourceNotFoundException {
        User savedUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("neplatný email (" + user.getEmail() + ")."));
        String token = sendResetToken(savedUser.getEmail());
        savedUser.setResetToken(token);
        userRepository.save(savedUser);
    }

    private String sendResetToken(String email) {
        String resetToken = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        UI.getCurrent().getPage().fetchCurrentURL(currentUrl -> {
            String url = String.format("%s://%s:%s/%s",
                    currentUrl.getProtocol(),
                    currentUrl.getHost(),
                    currentUrl.getPort(),
                    RouteConfiguration.forSessionScope().getUrl(ChangePasswordView.class, resetToken));
            try {
                emailService.send(
                        email,
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
