package bluesky.airline.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

// DTO for Authentication register requests
public class AuthRegisterRequest {
    @NotBlank(message = "name: Name is required")
    @Size(min = 2, max = 50, message = "name: Name length must be 2..50")
    private String name;

    @Size(max = 50, message = "surname: Surname too long")
    private String surname;

    @NotBlank(message = "username: Username is required")
    @Size(max = 50, message = "username: Username too long")
    private String username;

    @NotBlank(message = "email: Email is required")
    @Email(message = "email: Invalid email format")
    private String email;

    private String avatarUrl;

    @NotBlank(message = "password: Password is required")
    @Size(min = 4, message = "password: Password too short (min 4)")
    private String password;

    @NotNull(message = "roleCode: Role code is required")
    @Min(value = 0, message = "roleCode: Invalid role code")
    @Max(value = 2, message = "roleCode: Invalid role code")
    private Integer roleCode;

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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(Integer roleCode) {
        this.roleCode = roleCode;
    }
}
