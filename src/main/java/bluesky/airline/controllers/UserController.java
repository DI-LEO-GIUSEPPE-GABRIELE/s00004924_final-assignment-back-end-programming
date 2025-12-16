package bluesky.airline.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
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

/**
 * REST controller for /users with simple filters.
 */
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // Single list endpoint: pagination/sorting + optional filters
    @GetMapping
    public Page<User> list(
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) String nameContains,
            @RequestParam(required = false) String emailDomain,
            @PageableDefault(size = 10, sort = { "name" }, direction = Sort.Direction.ASC) Pageable pageable) {
        return service.searchPaged(mode, nameContains, emailDomain, pageable);
    }

    // Retrieve user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create user (payload CreateUserRequest)
    @PostMapping
    public ResponseEntity<User> create(@RequestBody CreateUserRequest body) {
        User u = service.create(body.getName(), body.getEmail(), body.getRoleId());
        return ResponseEntity.created(java.net.URI.create("/users/" + u.getId())).body(u);
    }

    // Update user (payload UpdateUserRequest)
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable UUID id, @RequestBody UpdateUserRequest body) {
        return service.update(id, body.getName(), body.getEmail(), body.getRoleId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete user (no payload)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
