package bluesky.airline.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import bluesky.airline.entities.Role;
import bluesky.airline.services.RoleService;

// Controller for roles
// Endpoint: /roles
@RestController
@RequestMapping("/roles")
public class RoleController {
    @Autowired
    private RoleService roles;

    // Get all roles
    // Endpoint: GET /roles
    @GetMapping
    public java.util.List<Role> list() {
        return roles.findAll();
    }
}
