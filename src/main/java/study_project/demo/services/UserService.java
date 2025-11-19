package study_project.demo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import study_project.demo.entities.User;
import org.springframework.stereotype.Service;
import study_project.demo.repositories.UserRepository;

/**
 * Service: logica applicativa e storage in memoria per la risorsa User.
 */
@Service
public class UserService {
    private final UserRepository repo;
    public UserService(UserRepository repo) { this.repo = repo; }

    // Read All: restituisce tutti gli utenti
    public List<User> findAll() { return repo.findAll(); }

    // Read All filtrato: nameContains e emailDomain
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

    // Read One: trova utente per ID
    public Optional<User> findById(Long id) { return repo.findById(id); }

    // Create: crea un utente con ID auto-generato
    public User create(String name, String email) {
        validateCreate(name, email);
        User u = new User(null, name, email);
        return repo.save(u);
    }

    // Update: aggiorna campi dell'utente se esiste
    public Optional<User> update(Long id, String name, String email) {
        return repo.findById(id).map(existing -> {
            validateUpdate(id, name, email);
            existing.setName(name);
            existing.setEmail(email);
            return repo.save(existing);
        });
    }

    // Delete: rimuove utente per ID
    public boolean delete(Long id) {
        if (repo.existsById(id)) { repo.deleteById(id); return true; }
        return false;
    }

    // Validazioni per create
    private void validateCreate(String name, String email) {
        validateName(name);
        validateEmail(email);
        // Unicità email (case-insensitive)
        boolean exists = repo.findByEmailIgnoreCase(email).isPresent();
        if (exists) throw new ValidationException("email", "Email già registrata");
    }

    // Validazioni per update
    private void validateUpdate(Long id, String name, String email) {
        validateName(name);
        validateEmail(email);
        boolean existsOther = repo.findByEmailIgnoreCase(email)
                .map(u -> !u.getId().equals(id))
                .orElse(false);
        if (existsOther) throw new ValidationException("email", "Email già usata da un altro utente");
    }

    // Regole: name non vuoto, lunghezza 2..50
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) throw new ValidationException("name", "Nome obbligatorio");
        String n = name.trim();
        if (n.length() < 2) throw new ValidationException("name", "Nome troppo corto (min 2)");
        if (n.length() > 50) throw new ValidationException("name", "Nome troppo lungo (max 50)");
    }

    // Regole: formato email semplice
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) throw new ValidationException("email", "Email obbligatoria");
        String e = email.trim();
        // Controllo basilare; si può sostituire con Bean Validation @Email
        if (!e.contains("@") || !e.contains(".")) throw new ValidationException("email", "Formato email non valido");
    }
}