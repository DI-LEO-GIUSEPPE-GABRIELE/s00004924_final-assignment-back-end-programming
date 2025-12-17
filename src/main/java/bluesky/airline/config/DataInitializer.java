package bluesky.airline.config;

import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import bluesky.airline.entities.Role;
import bluesky.airline.entities.User;
import bluesky.airline.repositories.RoleRepository;
import bluesky.airline.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class DataInitializer {
    @org.springframework.beans.factory.annotation.Autowired
    private RoleRepository roles;
    @org.springframework.beans.factory.annotation.Autowired
    private UserRepository users;
    @org.springframework.beans.factory.annotation.Autowired
    private PasswordEncoder encoder;
    @Value("${bootstrap.admin.email:}")
    private String adminEmail;
    @Value("${bootstrap.admin.password:}")
    private String adminPassword;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        ensureRole("ADMIN");
        ensureRole("TOUR_OPERATOR");
        ensureRole("FLIGHT_MANAGER");

        if (adminEmail != null && !adminEmail.isBlank() && adminPassword != null && !adminPassword.isBlank()) {
            users.findByEmailIgnoreCase(adminEmail).orElseGet(() -> {
                User admin = new User(null, "Admin", adminEmail);
                admin.setPassword(encoder.encode(adminPassword));
                Role adminRole = roles.findByNameIgnoreCase("ADMIN").orElseThrow();
                admin.setRoles(new java.util.HashSet<>(Set.of(adminRole)));
                return users.save(admin);
            });
        }
    }

    private void ensureRole(String name) {
        roles.findByNameIgnoreCase(name).orElseGet(() -> {
            Role r = new Role();
            r.setName(name);
            return roles.save(r);
        });
    }
}
