package cz.upce.fei.dt.beckend.repositories;

import cz.upce.fei.dt.beckend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
