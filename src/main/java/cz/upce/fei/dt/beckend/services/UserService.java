package cz.upce.fei.dt.beckend.services;

import cz.upce.fei.dt.beckend.entities.User;
import cz.upce.fei.dt.beckend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService{
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    public List<User> getAll() {
        return userRepository.findAll();
    }

    public void saveUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isEmpty()){
            String resetToken = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            user.setResetToken(resetToken);
            emailService.send(
                    user.getEmail(),
                    "Nastav si heslo.",
                    "Pro nastavení hesla přejdi na http://localhost:8888/password/" + resetToken);
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
}
