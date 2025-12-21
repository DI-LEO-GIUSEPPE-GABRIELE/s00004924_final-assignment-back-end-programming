package bluesky.airline.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import bluesky.airline.entities.Role;
import bluesky.airline.entities.User;
import bluesky.airline.exceptions.ValidationException;
import bluesky.airline.repositories.RoleRepository;
import bluesky.airline.repositories.UserRepository;

// Service for User entities
@Service
public class UserService {
    @Autowired
    private UserRepository repo;
    @Autowired
    private RoleRepository roles;
    @Autowired
    private PasswordEncoder encoder;

    public List<User> findAll() {
        return repo.findAll();
    }

    public Optional<User> findByEmail(String email) {
        return repo.findByEmailIgnoreCase(email);
    }

    public List<User> findAllFiltered(String nameContains, String emailDomain) {
        String nc = nameContains;
        String suffix = emailDomain != null ? ("@" + emailDomain) : null;
        if (nc != null && suffix != null) {
            return repo.findByNameContainingIgnoreCaseAndEmailEndingWithIgnoreCase(nc, suffix);
        } else if (nc != null) {
            return repo.findByNameContainingIgnoreCase(nc);
        } else if (suffix != null) {
            return repo.findByEmailEndingWithIgnoreCase(suffix);
        }
        return repo.findAll();
    }

    public Page<User> searchPaged(String mode, String nameContains, String emailDomain, Pageable pageable) {
        String nc = nameContains;
        String domain = emailDomain;
        switch (mode == null ? "derived" : mode.toLowerCase()) {
            case "jpql":
                return repo.searchUsersJpql(nc, domain, pageable);
            case "native":
                return repo.searchUsersNative(nc, domain, pageable);
            case "derived":
            default:
                if (nc != null && domain != null) {
                    return repo.findByNameContainingIgnoreCaseAndEmailEndingWithIgnoreCase(nc, "@" + domain, pageable);
                } else if (nc != null) {
                    return repo.findByNameContainingIgnoreCase(nc, pageable);
                } else if (domain != null) {
                    return repo.findByEmailEndingWithIgnoreCase("@" + domain, pageable);
                }
                return repo.findAll(pageable);
        }
    }

    public Optional<User> findById(UUID id) {
        return repo.findById(id);
    }

    public User create(String name, String surname, String username, String email, String password, String avatarUrl,
            Integer roleCode) {
        validateCreate(name, email);
        User u = new User(null, name, email);
        u.setSurname(surname);
        u.setUsername(username);
        u.setAvatarUrl(avatarUrl);
        u.setPassword(encoder.encode(password));
        if (roleCode != null) {
            String roleName = mapRoleCode(roleCode);
            Role r = roleName == null ? null : roles.findByNameIgnoreCase(roleName).orElse(null);
            if (r == null || !isAllowedRole(r))
                throw new bluesky.airline.exceptions.ValidationException(java.util.List.of("roleCode: Invalid role"));
            java.util.Set<Role> rs = new java.util.HashSet<>();
            rs.add(r);
            u.setRoles(rs);
        }
        return repo.save(u);
    }

    public User register(String name, String surname, String username, String email, String avatarUrl, String password,
            Integer roleCode) {
        validateCreate(name, email);
        User u = new User(null, name, email);
        u.setSurname(surname);
        u.setUsername(username);
        u.setAvatarUrl(avatarUrl);
        u.setPassword(encoder.encode(password));
        if (roleCode != null) {
            String roleName = mapRoleCode(roleCode);
            Role r = roleName == null ? null : roles.findByNameIgnoreCase(roleName).orElse(null);
            if (r == null || !isAllowedRole(r))
                throw new bluesky.airline.exceptions.ValidationException(java.util.List.of("roleCode: Invalid role"));
            java.util.Set<Role> rs = new java.util.HashSet<>();
            rs.add(r);
            u.setRoles(rs);
        }
        return repo.save(u);
    }

    private String mapRoleCode(Integer code) {
        if (code == null)
            return null;
        return switch (code) {
            case 0 -> "ADMIN";
            case 1 -> "FLIGHT_MANAGER";
            case 2 -> "TOUR_OPERATOR";
            default -> null;
        };
    }

    public Optional<User> update(UUID id, String name, String surname, String username, String email, String avatarUrl,
            Integer roleCode) {
        return repo.findById(id).map(existing -> {
            validateUpdate(id, name, email);
            existing.setName(name);
            existing.setSurname(surname);
            existing.setUsername(username);
            existing.setEmail(email);
            existing.setAvatarUrl(avatarUrl);
            if (roleCode != null) {
                String roleName = mapRoleCode(roleCode);
                Role r = roleName == null ? null : roles.findByNameIgnoreCase(roleName).orElse(null);
                if (r == null || !isAllowedRole(r))
                    throw new bluesky.airline.exceptions.ValidationException(
                            java.util.List.of("roleCode: Invalid role"));
                java.util.Set<Role> rs = new java.util.HashSet<>();
                rs.add(r);
                existing.setRoles(rs);
            }
            return repo.save(existing);
        });
    }

    private boolean isAllowedRole(Role r) {
        String n = r.getName();
        return n != null && (n.equalsIgnoreCase("ADMIN")
                || n.equalsIgnoreCase("TOUR_OPERATOR")
                || n.equalsIgnoreCase("FLIGHT_MANAGER"));
    }

    public boolean delete(UUID id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    private void validateCreate(String name, String email) {
        validateName(name);
        validateEmail(email);
        boolean exists = repo.findByEmailIgnoreCase(email).isPresent();
        if (exists)
            throw new ValidationException(java.util.List.of("email: Email already registered"));
    }

    private void validateUpdate(UUID id, String name, String email) {
        validateName(name);
        validateEmail(email);
        boolean existsOther = repo.findByEmailIgnoreCase(email)
                .map(u -> !u.getId().equals(id))
                .orElse(false);
        if (existsOther)
            throw new ValidationException(java.util.List.of("email: Email already used by another user"));
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new ValidationException(java.util.List.of("name: Name is required"));
        String n = name.trim();
        if (n.length() < 2)
            throw new ValidationException(java.util.List.of("name: Name too short (min 2)"));
        if (n.length() > 50)
            throw new ValidationException(java.util.List.of("name: Name too long (max 50)"));
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty())
            throw new ValidationException(java.util.List.of("email: Email is required"));
        String e = email.trim();
        if (!e.contains("@") || !e.contains("."))
            throw new ValidationException(java.util.List.of("email: Invalid email format"));
    }
}
