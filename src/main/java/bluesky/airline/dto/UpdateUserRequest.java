package bluesky.airline.dto;

/**
 * DTO for user update.
 */
public class UpdateUserRequest {
    private String name;
    private String email;
    private java.util.Set<java.util.UUID> roleIds;

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

    public java.util.Set<java.util.UUID> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(java.util.Set<java.util.UUID> roleIds) {
        this.roleIds = roleIds;
    }
}
