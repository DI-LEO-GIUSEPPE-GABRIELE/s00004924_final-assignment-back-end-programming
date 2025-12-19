package bluesky.airline.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO for Authentication login requests
public class AuthLoginRequest {
    @NotBlank(message = "email: Email is required")
    @Email(message = "email: Invalid email format")
    private String email;
    @NotBlank(message = "password: Password is required")
    @Size(min = 4, message = "password: Password too short (min 4)")
    private String password;

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
}
