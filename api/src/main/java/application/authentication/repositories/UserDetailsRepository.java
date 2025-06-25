package application.authentication.repositories;

import application.authentication.entities.User;
import application.authentication.entities.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserDetailsRepository extends JpaRepository<UserDetails, UUID> {
}
