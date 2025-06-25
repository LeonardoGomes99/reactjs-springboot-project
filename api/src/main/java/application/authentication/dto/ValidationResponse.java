package application.authentication.dto;

import java.util.List;

public class ValidationResponse {
    private boolean valid;
    private String username;
    private List<String> roles;
    private String expiresAt;

    public ValidationResponse() {}

    public ValidationResponse(boolean valid, String username, List<String> roles, String expiresAt) {
        this.valid = valid;
        this.username = username;
        this.roles = roles;
        this.expiresAt = expiresAt;
    }

    public boolean isValid() {
        return valid;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }
}
