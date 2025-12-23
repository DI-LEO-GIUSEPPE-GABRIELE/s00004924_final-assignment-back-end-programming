package bluesky.airline.services;

import org.springframework.data.domain.Page;
import bluesky.airline.repositories.RoleRepository;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import bluesky.airline.entities.User;
import bluesky.airline.repositories.UserRepository;
import bluesky.airline.entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import bluesky.airline.exceptions.ValidationException;
import java.util.Optional;

// Service for User entities
@Service
public class UserService {
    @Autowired
    private UserRepository repo;
    @Autowired
    private RoleRepository roles;
    @Autowired
    private PasswordEncoder encoder;

    // Find all users
    public List<User> findAll() {
        return repo.findAll();
    }

    // Find a user by its email
    public Optional<User> findByEmail(String email) {
        return repo.findByEmailIgnoreCase(email);
    }

    // Find all users filtered by name and email domain
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

    // Search users with pagination
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

    // Find a user by its ID
    public Optional<User> findById(UUID id) {
        return repo.findById(id);
    }

    // Create a new user
    public User create(String name, String surname, String username, String email, String password, String avatarUrl,
            Integer roleCode) {
        validateCreate(email, username);
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

    // Map role code to role name
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

    // Update a user from a UserReqDTO
    public Optional<User> update(UUID id, String name, String surname, String username, String email, String avatarUrl,
            Integer roleCode) {
        return repo.findById(id).map(existing -> {
            validateUpdate(id, email, username);
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

    // Check if a role is allowed for user creation/registration
    private boolean isAllowedRole(Role r) {
        String n = r.getName();
        return n != null && (n.equalsIgnoreCase("ADMIN")
                || n.equalsIgnoreCase("TOUR_OPERATOR")
                || n.equalsIgnoreCase("FLIGHT_MANAGER"));
    }

    // Delete a user by its ID
    public boolean delete(UUID id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    // Validate user creation/registration data
    private void validateCreate(String email, String username) {
        java.util.List<String> errors = new java.util.ArrayList<>();
        if (repo.findByEmailIgnoreCase(email).isPresent())
            errors.add("email: Email already registered");
        if (username != null && repo.findByUsernameIgnoreCase(username).isPresent())
            errors.add("username: Username already registered");

        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }

    // Validate user update data
    private void validateUpdate(UUID id, String email, String username) {
        java.util.List<String> errors = new java.util.ArrayList<>();
        boolean existsEmail = repo.findByEmailIgnoreCase(email)
                .map(u -> !u.getId().equals(id))
                .orElse(false);
        if (existsEmail)
            errors.add("email: Email already used by another user");

        if (username != null) {
            boolean existsUsername = repo.findByUsernameIgnoreCase(username)
                    .map(u -> !u.getId().equals(id))
                    .orElse(false);
            if (existsUsername)
                errors.add("username: Username already registered");
        }

        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
