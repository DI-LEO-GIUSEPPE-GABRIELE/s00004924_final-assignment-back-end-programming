package bluesky.airline.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import bluesky.airline.dto.auth.AuthLoginRequest;
import bluesky.airline.entities.User;
import bluesky.airline.repositories.UserRepository;
import bluesky.airline.security.JwtTools;

@Service
public class AuthService {
    private final UserRepository users;
    private final JwtTools jwtTools;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository users, JwtTools jwtTools, PasswordEncoder encoder) {
        this.users = users;
        this.jwtTools = jwtTools;
        this.encoder = encoder;
    }

    public String checkCredentialsAndGenerateToken(AuthLoginRequest body) {
        User u = users.findByEmailIgnoreCase(body.getEmail())
                .orElseThrow(() -> new bluesky.airline.exceptions.UnauthorizedException("Credenziali errate"));
        if (!encoder.matches(body.getPassword(), u.getPassword()))
            throw new bluesky.airline.exceptions.UnauthorizedException("Credenziali errate");
        return jwtTools.generateForUser(u);
    }
}
