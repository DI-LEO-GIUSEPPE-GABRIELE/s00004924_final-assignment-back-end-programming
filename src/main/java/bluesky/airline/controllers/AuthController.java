package bluesky.airline.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import bluesky.airline.repositories.UserRepository;
import bluesky.airline.services.AuthService;
import bluesky.airline.entities.User;
import bluesky.airline.dto.auth.AuthLoginRequest;
import bluesky.airline.dto.auth.AuthRegisterRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthController(AuthService authService, UserRepository users, PasswordEncoder encoder) {
        this.authService = authService;
        this.users = users;
        this.encoder = encoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated AuthLoginRequest body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new bluesky.airline.exceptions.ValidationException(
                    validationResult.getFieldErrors().stream().map(fe -> fe.getDefaultMessage()).toList());
        }
        String token = authService.checkCredentialsAndGenerateToken(body);
        return ResponseEntity.ok(java.util.Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Validated AuthRegisterRequest body,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new bluesky.airline.exceptions.ValidationException(
                    validationResult.getFieldErrors().stream().map(fe -> fe.getDefaultMessage()).toList());
        }
        boolean exists = users.findByEmailIgnoreCase(body.getEmail()).isPresent();
        if (exists)
            return ResponseEntity.status(409).body(java.util.Map.of("error", "email exists"));
        User u = new User(null, body.getName(), body.getEmail());
        u.setPassword(encoder.encode(body.getPassword()));
        u = users.save(u);
        return ResponseEntity.created(java.net.URI.create("/users/" + u.getId()))
                .body(java.util.Map.of("id", u.getId()));
    }
}
