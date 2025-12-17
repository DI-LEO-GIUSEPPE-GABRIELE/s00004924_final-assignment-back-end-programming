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
    @org.springframework.beans.factory.annotation.Autowired
    private RoleRepository roles;
    @org.springframework.beans.factory.annotation.Autowired
    private UserRepository users;

    @GetMapping
    public java.util.List<Role> list() {
        return roles.findAll();
    }

    
}
