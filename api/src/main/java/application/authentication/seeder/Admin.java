package application.authentication.seeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import application.authentication.entities.Role;
import application.authentication.entities.User;
import application.authentication.repositories.RoleRepository;
import application.authentication.repositories.UserRepository;

import java.util.Collections;
import java.util.Optional;

@Component
public class Admin {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${security.admin.username}")
    private String DEFAULT_ADMIN_USERNAME;

    @Value("${security.admin.password}")
    private String DEFAULT_ADMIN_PASSWORD;

    public void createRolesIfNotExists() {
        try {
            String[] roleNames = {"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_MANAGER"};
            for (String roleName : roleNames) {
                if (roleRepository.findByName(roleName).isEmpty()) {
                    Role role = new Role();
                    role.setName(roleName);
                    roleRepository.save(role);
                    System.out.println("Role '" + roleName + "' criada com sucesso.");
                } else {
                    System.out.println("Role '" + roleName + "' já existe.");
                }
            }
        } catch (Exception e) {
            System.out.println("Aviso: não foi possível criar as roles neste momento. Motivo: " + e.getMessage());
            // e.printStackTrace(); // pode descomentar em caso de depuração
        }
    }

    public void createAdminUserIfNotExists() {
        try {
            Optional<User> existingAdmin = userRepository.findByUsername(DEFAULT_ADMIN_USERNAME);

            if (existingAdmin.isEmpty()) {
                Optional<Role> adminRole = roleRepository.findByName("ROLE_ADMIN");

                if (adminRole.isEmpty()) {
                    System.out.println("Aviso: Role 'ROLE_ADMIN' não encontrada. Execute createRolesIfNotExists() primeiro.");
                    return;
                }
                User admin = new User();
                admin.setUsername(DEFAULT_ADMIN_USERNAME);
                admin.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
                admin.setRoles(Collections.singleton(adminRole.get()));
                userRepository.save(admin);
                System.out.println("Usuário admin criado com sucesso.");
            } else {
                System.out.println("Usuário admin já existe.");
            }
        } catch (Exception e) {
            System.out.println("Aviso: não foi possível criar o usuário admin. Motivo: " + e.getMessage());
            // e.printStackTrace(); // descomente se precisar investigar
        }
    }


    @EventListener(ApplicationReadyEvent.class)
    public void initializer(){
        createRolesIfNotExists();
        createAdminUserIfNotExists();
    }
}
