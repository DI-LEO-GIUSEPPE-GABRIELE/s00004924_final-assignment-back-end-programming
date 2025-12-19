package bluesky.airline.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

// DTO for User requests (update)
public class UpdateUserRequest {
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Size(max = 50, message = "Surname too long")
    private String surname;

    @Size(max = 50, message = "Username too long")
    private String username;

    @Email(message = "Invalid email format")
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
