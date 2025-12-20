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

import bluesky.airline.entities.enums.RoleType;

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

    // Initialize roles and admin user on application startup
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        for (RoleType roleType : RoleType.values()) {
            ensureRole(roleType);
        }

        // Create admin user if email and password are provided
        if (adminEmail != null && !adminEmail.isBlank() && adminPassword != null && !adminPassword.isBlank()) {
            users.findByEmailIgnoreCase(adminEmail).orElseGet(() -> {
                User admin = new User(null, "Admin", adminEmail);
                admin.setPassword(encoder.encode(adminPassword));
                admin.setRoleCode(RoleType.ADMIN.getCode());
                Role adminRole = roles.findByNameIgnoreCase(RoleType.ADMIN.name()).orElseThrow();
                admin.setRoles(new java.util.HashSet<>(Set.of(adminRole)));
                return users.save(admin);
            });
        }
    }

    // Ensure a role exists, creating it if not
    private void ensureRole(RoleType roleType) {
        Role r = roles.findByNameIgnoreCase(roleType.name()).orElse(new Role());
        if (r.getId() == null) {
            r.setName(roleType.name());
        }
        r.setRoleCode(roleType.getCode());
        roles.save(r);
    }
}
