package bluesky.airline.security;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import bluesky.airline.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

// Service for JWT token generation and validation
@Service
public class JwtTools {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration-seconds}")
    private long expirationSeconds;

    public String generate(Authentication auth) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationSeconds);
        String roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return Jwts.builder()
                .subject(auth.getName())
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                .compact();
    }

    public String generateForUser(User user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationSeconds);
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                .compact();
    }

    public boolean validate(String token) {
        try {
            Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                    .build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    public List<SimpleGrantedAuthority> extractAuthorities(String token) {
        String roles = (String) Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8))).build()
                .parseSignedClaims(token).getPayload().get("roles");
        if (roles == null || roles.isBlank())
            return java.util.List.of();
        return java.util.Arrays.stream(roles.split(","))
                .filter(r -> !r.isBlank())
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
