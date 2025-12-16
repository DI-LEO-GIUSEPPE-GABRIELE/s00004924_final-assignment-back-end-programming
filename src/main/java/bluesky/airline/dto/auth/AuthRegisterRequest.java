package bluesky.airline.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

public class AuthRegisterRequest {
    @NotBlank(message = "name: Name is required")
    @Size(min = 2, max = 50, message = "name: Name length must be 2..50")
    private String name;
    @NotBlank(message = "email: Email is required")
    @Email(message = "email: Invalid email format")
    private String email;
    @NotBlank(message = "password: Password is required")
    @Size(min = 4, message = "password: Password too short (min 4)")
    private String password;
    @NotNull(message = "roleId: Role is required")
    private java.util.UUID roleId;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public java.util.UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(java.util.UUID roleId) {
        this.roleId = roleId;
    }
}
