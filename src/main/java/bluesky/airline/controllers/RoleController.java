package bluesky.airline.controllers;

import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import bluesky.airline.entities.Role;
import bluesky.airline.entities.User;
import bluesky.airline.repositories.RoleRepository;
import bluesky.airline.repositories.UserRepository;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private final RoleRepository roles;
    private final UserRepository users;

    public RoleController(RoleRepository roles, UserRepository users) {
        this.roles = roles;
        this.users = users;
    }

    @GetMapping
    public java.util.List<Role> list() {
        return roles.findAll();
    }

    
}
