package bluesky.airline.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import bluesky.airline.services.AuthService;
import bluesky.airline.services.UserService;
import bluesky.airline.entities.User;
import bluesky.airline.dto.auth.AuthLoginRequest;
import bluesky.airline.dto.auth.AuthRegisterRequest;
import bluesky.airline.dto.auth.LoginRespDTO;
import bluesky.airline.dto.auth.NewUserRespDTO;

// Controller for authentication and user management, accessible by all roles
// Endpoint: /auth
@RestController
@RequestMapping("/auth")
public class AuthController {
    @org.springframework.beans.factory.annotation.Autowired
    private AuthService authService;
    @org.springframework.beans.factory.annotation.Autowired
    private UserService userService;

    // Do login and return a JWT token
    // Endpoint: POST /auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated AuthLoginRequest body) {
        String token = authService.checkCredentialsAndGenerateToken(body);
        return ResponseEntity.ok(new LoginRespDTO(token));
    }

    // Register a new user and return the created user's ID
    // Endpoint: POST /auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Validated AuthRegisterRequest body) {
        User u = userService.create(body.getName(), body.getSurname(), body.getUsername(), body.getEmail(),
                body.getPassword(), body.getAvatarUrl(), body.getRoleCode());
        return ResponseEntity.created(java.net.URI.create("/users/" + u.getId()))
                .body(new NewUserRespDTO(u.getId()));
    }
}
