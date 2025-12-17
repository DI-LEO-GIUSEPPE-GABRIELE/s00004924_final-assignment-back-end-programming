package bluesky.airline.controllers;

import org.springframework.web.bind.annotation.*;
import bluesky.airline.entities.Role;

@RestController
@RequestMapping("/roles")
public class RoleController {
    @org.springframework.beans.factory.annotation.Autowired
    private bluesky.airline.services.RoleService roles;

    @GetMapping
    public java.util.List<Role> list() {
        return roles.findAll();
    }
}
