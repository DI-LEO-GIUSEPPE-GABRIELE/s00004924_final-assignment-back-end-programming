package bluesky.airline.dto;

/**
 * DTO for user update.
 */
public class UpdateUserRequest {
    private String name;
    private String email;
    private java.util.Set<String> roles;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public java.util.Set<String> getRoles() {
        return roles;
    }

    public void setRoles(java.util.Set<String> roles) {
        this.roles = roles;
    }
}
