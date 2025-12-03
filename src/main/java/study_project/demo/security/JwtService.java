package study_project.demo.security;

import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private final byte[] secret;
    private final long expirationSeconds;

    public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration-seconds}") long expirationSeconds) {
        this.secret = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        this.expirationSeconds = expirationSeconds;
    }

    public String generate(Authentication auth) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationSeconds);
        String roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        return Jwts.builder()
            .subject(auth.getName())
            .claim("roles", roles)
            .issuedAt(Date.from(now))
            .expiration(Date.from(exp))
            .signWith(Keys.hmacShaKeyFor(secret))
            .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret)).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validate(String token) {
        try {
            Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret)).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

