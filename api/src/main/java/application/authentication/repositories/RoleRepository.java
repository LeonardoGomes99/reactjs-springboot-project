package application.authentication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import application.authentication.entities.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role , Long> {
    Role getByName(String name);

    Optional<Role> findByName(String name);
}
