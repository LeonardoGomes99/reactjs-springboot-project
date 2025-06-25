package application.authentication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import application.authentication.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
