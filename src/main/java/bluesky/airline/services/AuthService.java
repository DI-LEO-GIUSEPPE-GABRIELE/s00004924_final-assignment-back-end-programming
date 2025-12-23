package bluesky.airline.services;

import bluesky.airline.security.JwtTools;
import bluesky.airline.entities.User;
import bluesky.airline.exceptions.UnauthorizedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import bluesky.airline.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import bluesky.airline.dto.auth.AuthLoginRequest;

// Service for authentication
@Service
public class AuthService {
    @Autowired
    private UserRepository users;
    @Autowired
    private JwtTools jwtTools;
    @Autowired
    private PasswordEncoder encoder;

    // Check user credentials and generate a JWT token
    public String checkCredentialsAndGenerateToken(AuthLoginRequest body) {
        User u = users.findByEmailIgnoreCase(body.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        if (!encoder.matches(body.getPassword(), u.getPassword()))
            throw new UnauthorizedException("Invalid email or password");
        return jwtTools.generateForUser(u);
    }
}
