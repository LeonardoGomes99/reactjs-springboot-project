package application.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import application.authentication.entities.Role;
import application.authentication.entities.User;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserDTO {

    private UUID id;

    @NotBlank(message = "deve ter entre 10 e 50 caracteres")
    @Size(min = 10, max = 50, message = "deve ter entre 10 e 50 caracteres")
    private String username;

    @NotBlank(message = "deve ter entre 10 e 50 caracteres")
    @Size(min = 10, max = 50, message = "deve ter entre 10 e 50 caracteres")
    private String password;

    private Set<Role> roles = new HashSet<>();


    public UserDTO() {
    }

    public UserDTO(UUID id, String username, Set<Role> roles, String password) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.password = password;
    }

    public UserDTO(UUID id, String username, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public UserDTO(User entity) {
        this.id = entity.getId();
        this.username = entity.getUsername();
        this.roles = entity.getRoles();
        this.password = entity.getPassword();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
