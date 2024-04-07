package cz.upce.fei.dt.beckend.services;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InvalidClassException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class UserService{
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public Stream<User> findAllByFirstnameAndLastname(Query<User, String> query){
        String searchTerm = query.getFilter().orElse("");
        return userRepository.findAllByFirstnameAndLastname(VaadinSpringDataHelpers.toSpringPageRequest(query), searchTerm)
                .stream()
                .map(iUser -> User.builder()
                        .id(iUser.getId())
                        .firstName(iUser.getFirstName())
                        .lastName(iUser.getLastName())
                        .build()
                );
    }
    @Transactional
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void saveUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isEmpty()){
            String resetToken = sendResetToken(user);
            user.setPassword(passwordEncoder.encode(resetToken));
        }
        userRepository.save(user);
    }
    public void changePassword(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setResetToken("");
        userRepository.save(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public void generateResetToken(User user) throws InvalidClassException {
        if (userRepository.findByEmail(user.getEmail()).isPresent()){
            sendResetToken(user);
            userRepository.save(user);
        } else {
            throw new InvalidClassException(user.getEmail() + "nebyl najit.");
        }
    }

    private String sendResetToken(User user){
        String resetToken = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        user.setResetToken(resetToken);
        emailService.send(
                user.getEmail(),
                "Nastav si heslo.",
                "Pro nastavení hesla přejdi na http://localhost:8888/password/" + resetToken);
        return resetToken;
    }
}
