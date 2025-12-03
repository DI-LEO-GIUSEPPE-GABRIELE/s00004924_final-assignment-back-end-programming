package study_project.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study_project.demo.repositories.UserRepository;
import study_project.demo.security.JwtService;
import study_project.demo.entities.User;
import study_project.demo.dto.AuthLoginRequest;
import study_project.demo.dto.AuthRegisterRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthController(AuthenticationManager authManager, JwtService jwtService, UserRepository users, PasswordEncoder encoder) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.users = users;
        this.encoder = encoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequest body) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword()));
        String token = jwtService.generate(auth);
        return ResponseEntity.ok(java.util.Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRegisterRequest body) {
        boolean exists = users.findByEmailIgnoreCase(body.getEmail()).isPresent();
        if (exists) return ResponseEntity.status(409).body(java.util.Map.of("error", "email exists"));
        User u = new User(null, body.getName(), body.getEmail());
        u.setPassword(encoder.encode(body.getPassword()));
        u = users.save(u);
        return ResponseEntity.created(java.net.URI.create("/users/" + u.getId())).body(java.util.Map.of("id", u.getId()));
    }
}

