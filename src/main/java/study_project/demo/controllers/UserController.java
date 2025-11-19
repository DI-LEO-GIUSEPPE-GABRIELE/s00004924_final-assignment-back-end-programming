package study_project.demo.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import study_project.demo.dto.CreateUserRequest;
import study_project.demo.dto.UpdateUserRequest;
import study_project.demo.entities.User;
import study_project.demo.services.UserService;

/**
 * Controller REST per /users con filtri semplici.
 */
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // Recupera tutti gli utenti (Read All) con query params opzionali
    @GetMapping
    public List<User> list(
            @RequestParam(required = false) String nameContains,
            @RequestParam(required = false) String emailDomain) {
        if (nameContains == null && emailDomain == null) {
            return service.findAll();
        }
        return service.findAllFiltered(nameContains, emailDomain);
    }

    // Recupera utente per ID
    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crea utente (payload CreateUserRequest)
    @PostMapping
    public ResponseEntity<User> create(@RequestBody CreateUserRequest body) {
        User u = service.create(body.getName(), body.getEmail());
        return ResponseEntity.created(java.net.URI.create("/users/" + u.getId())).body(u);
    }

    // Aggiorna utente (payload UpdateUserRequest)
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody UpdateUserRequest body) {
        return service.update(id, body.getName(), body.getEmail())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Cancella utente (nessun payload)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}