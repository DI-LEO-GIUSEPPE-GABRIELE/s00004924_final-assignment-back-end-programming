package bluesky.airline.services;

import bluesky.airline.entities.Role;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import bluesky.airline.repositories.RoleRepository;

// Service for Role entities
@Service
public class RoleService {
    @Autowired
    private RoleRepository roles;

    // Find all roles
    public List<Role> findAll() {
        return roles.findAll();
    }
}
