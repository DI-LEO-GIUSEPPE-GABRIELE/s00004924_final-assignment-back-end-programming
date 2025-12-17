package bluesky.airline.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import bluesky.airline.dto.auth.AuthLoginRequest;
import bluesky.airline.entities.User;
import bluesky.airline.repositories.UserRepository;
import bluesky.airline.security.JwtTools;

@Service
public class AuthService {
    @Autowired
    private UserRepository users;
    @Autowired
    private JwtTools jwtTools;
    @Autowired
    private PasswordEncoder encoder;

    public String checkCredentialsAndGenerateToken(AuthLoginRequest body) {
        User u = users.findByEmailIgnoreCase(body.getEmail())
                .orElseThrow(() -> new bluesky.airline.exceptions.UnauthorizedException("Credenziali errate"));
        if (!encoder.matches(body.getPassword(), u.getPassword()))
            throw new bluesky.airline.exceptions.UnauthorizedException("Credenziali errate");
        return jwtTools.generateForUser(u);
    }
}
