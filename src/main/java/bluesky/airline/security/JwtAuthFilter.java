package bluesky.airline.security;

import java.io.IOException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import bluesky.airline.repositories.UserRepository;
import bluesky.airline.entities.User;
import java.util.UUID;

// Filter for JWT authentication
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @org.springframework.beans.factory.annotation.Autowired
    private JwtTools jwtTools;
    @org.springframework.beans.factory.annotation.Autowired
    private UserRepository users;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtTools.validate(token)) {
                String subject = jwtTools.extractUsername(token);
                if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    try {
                        UUID userId = UUID.fromString(subject);
                        User u = users.findWithRolesById(userId).orElse(null);
                        if (u != null) {
                            java.util.List<org.springframework.security.core.authority.SimpleGrantedAuthority> auths = u
                                    .getRoles().stream()
                                    .map(r -> new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                            "ROLE_" + r.getName()))
                                    .toList();
                            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, null,
                                    auths);
                            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    } catch (IllegalArgumentException ex) {
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return new AntPathMatcher().match("/auth/**", request.getServletPath());
    }
}
