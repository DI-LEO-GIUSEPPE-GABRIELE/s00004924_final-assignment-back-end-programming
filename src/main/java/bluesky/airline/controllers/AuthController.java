package bluesky.airline.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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

@RestController
@RequestMapping("/auth")
public class AuthController {
    @org.springframework.beans.factory.annotation.Autowired
    private AuthService authService;
    @org.springframework.beans.factory.annotation.Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated AuthLoginRequest body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new bluesky.airline.exceptions.ValidationException(
                    validationResult.getFieldErrors().stream().map(fe -> fe.getDefaultMessage()).toList());
        }
        String token = authService.checkCredentialsAndGenerateToken(body);
        return ResponseEntity.ok(new LoginRespDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Validated AuthRegisterRequest body,
            BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            throw new bluesky.airline.exceptions.ValidationException(
                    validationResult.getFieldErrors().stream().map(fe -> fe.getDefaultMessage()).toList());
        }
        boolean exists = userService.findByEmail(body.getEmail()).isPresent();
        if (exists)
            return ResponseEntity.status(409).body(java.util.Map.of("error", "email exists"));
        User u = userService.register(body.getName(), body.getSurname(), body.getUsername(), body.getEmail(),
                body.getAvatarUrl(), body.getPassword(), body.getRoleCode());
        return ResponseEntity.created(java.net.URI.create("/users/" + u.getId()))
                .body(new NewUserRespDTO(u.getId()));
    }
}
