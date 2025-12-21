package bluesky.airline.controllers;

import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import bluesky.airline.dto.users.CreateUserRequest;
import bluesky.airline.dto.users.UpdateUserRequest;
import bluesky.airline.entities.User;
import bluesky.airline.services.UserService;

// Controller for users management, only accessible by ADMIN role
// Endpoint: /users
@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    @org.springframework.beans.factory.annotation.Autowired
    private UserService service;

    // List users endpoint with pagination/sorting and optional filters
    // Endpoint: GET /users
    @GetMapping
    public Page<User> list(
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) String nameContains,
            @RequestParam(required = false) String emailDomain,
            @PageableDefault(size = 10, sort = { "name" }, direction = Sort.Direction.ASC) Pageable pageable) {
        return service.searchPaged(mode, nameContains, emailDomain, pageable);
    }

    // Get user details endpoint
    // Endpoint: GET /users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new bluesky.airline.exceptions.NotFoundException("User not found: " + id));
    }

    // Create user endpoint
    // Endpoint: POST /users
    @PostMapping
    public ResponseEntity<User> create(@RequestBody @jakarta.validation.Valid CreateUserRequest body) {
        User u = service.create(body.getName(), body.getSurname(), body.getUsername(), body.getEmail(),
                body.getPassword(), body.getAvatarUrl(), body.getRoleCode());
        return ResponseEntity.created(java.net.URI.create("/users/" + u.getId())).body(u);
    }

    // Update user endpoint
    // Endpoint: PUT /users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable UUID id,
            @RequestBody @jakarta.validation.Valid UpdateUserRequest body) {
        return service
                .update(id, body.getName(), body.getSurname(), body.getUsername(), body.getEmail(), body.getAvatarUrl(),
                        body.getRoleCode())
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new bluesky.airline.exceptions.NotFoundException("User not found: " + id));
    }

    // Delete user endpoint
    // Endpoint: DELETE /users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!service.delete(id))
            throw new bluesky.airline.exceptions.NotFoundException("User not found: " + id);
        return ResponseEntity.noContent().build();
    }
}
