package bluesky.airline.dto.users;

/**
 * DTO for user update.
 */
public class UpdateUserRequest {
    private String name;
    private String surname;
    private String username;
    private String email;
    private String avatarUrl;
    private java.util.UUID roleId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public java.util.UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(java.util.UUID roleId) {
        this.roleId = roleId;
    }
}
