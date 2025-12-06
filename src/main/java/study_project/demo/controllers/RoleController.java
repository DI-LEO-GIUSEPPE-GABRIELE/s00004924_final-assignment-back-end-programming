package study_project.demo.controllers;

import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import study_project.demo.entities.Role;
import study_project.demo.entities.User;
import study_project.demo.repositories.RoleRepository;
import study_project.demo.repositories.UserRepository;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private final RoleRepository roles;
    private final UserRepository users;
    public RoleController(RoleRepository roles, UserRepository users) { this.roles = roles; this.users = users; }

    @GetMapping
    public java.util.List<Role> list() { return roles.findAll(); }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Role> create(@RequestBody Role body) {
        Role r = roles.save(body);
        return ResponseEntity.created(java.net.URI.create("/roles/" + r.getId())).body(r);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign")
    public ResponseEntity<?> assignToUser(@RequestParam UUID userId, @RequestParam String roleName) {
        User u = users.findById(userId).orElse(null);
        Role r = roles.findByNameIgnoreCase(roleName).orElse(null);
        if (u == null || r == null) return ResponseEntity.badRequest().build();
        u.getRoles().add(r);
        users.save(u);
        return ResponseEntity.ok(java.util.Map.of("userId", u.getId(), "roles", u.getRoles().stream().map(Role::getName).toList()));
    }
}
