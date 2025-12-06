package bluesky.airline.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import bluesky.airline.entities.User;
import org.springframework.stereotype.Service;
import bluesky.airline.repositories.UserRepository;
import bluesky.airline.exceptions.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service: logica applicativa e storage in memoria per la risorsa User.
 */
@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    // Read All: restituisce tutti gli utenti
    public List<User> findAll() {
        return repo.findAll();
    }

    // Read All filtrato: nameContains e emailDomain
    public List<User> findAllFiltered(String nameContains, String emailDomain) {
        String nc = nameContains;
        String suffix = emailDomain != null ? ("@" + emailDomain) : null;
        if (nc != null && suffix != null) {
            // Filtra per nome contenente e dominio email
            return repo.findByNameContainingIgnoreCaseAndEmailEndingWithIgnoreCase(nc, suffix);
        } else if (nc != null) {
            // Filtra solo per nome contenente
            return repo.findByNameContainingIgnoreCase(nc);
        } else if (suffix != null) {
            // Filtra solo per dominio email
            return repo.findByEmailEndingWithIgnoreCase(suffix);
        }
        // Nessun filtro applicato, restituisce tutti gli utenti
        return repo.findAll();
    }

    // Ricerca paginata/sortata con tre modalità di query (derived, jpql, native)
    public Page<User> searchPaged(String mode, String nameContains, String emailDomain, Pageable pageable) {
        String nc = nameContains;
        String domain = emailDomain;
        // Modalità: derived (metodi di naming), jpql (@Query JPQL), native (SQL
        // Postgres)
        switch (mode == null ? "derived" : mode.toLowerCase()) {
            case "jpql":
                return repo.searchUsersJpql(nc, domain, pageable);
            case "native":
                return repo.searchUsersNative(nc, domain, pageable);
            case "derived":
            default:
                if (nc != null && domain != null) {
                    return repo.findByNameContainingIgnoreCaseAndEmailEndingWithIgnoreCase(nc, "@" + domain, pageable);
                } else if (nc != null) {
                    return repo.findByNameContainingIgnoreCase(nc, pageable);
                } else if (domain != null) {
                    return repo.findByEmailEndingWithIgnoreCase("@" + domain, pageable);
                }
                // Nessun filtro: usa findAll(Pageable)
                return repo.findAll(pageable);
        }
    }

    // Read One: trova utente per ID
    public Optional<User> findById(UUID id) {
        return repo.findById(id);
    }

    // Metodo demo rimosso (details con profilo e ordini)

    // Create: crea un utente con ID auto-generato
    public User create(String name, String email) {
        // Validazioni preliminari
        validateCreate(name, email);
        User u = new User(null, name, email);
        return repo.save(u);
    }

    // Update: aggiorna campi dell'utente se esiste
    public Optional<User> update(UUID id, String name, String email) {
        return repo.findById(id).map(existing -> {
            // Validazioni preliminari
            validateUpdate(id, name, email);
            existing.setName(name);
            existing.setEmail(email);
            return repo.save(existing);
        });
    }

    // Delete: rimuove utente per ID
    public boolean delete(UUID id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    // Validazioni per create
    private void validateCreate(String name, String email) {
        // Validazioni preliminari
        validateName(name);
        validateEmail(email);
        // Unicità email (case-insensitive)
        boolean exists = repo.findByEmailIgnoreCase(email).isPresent();
        if (exists)
            throw new ValidationException("email", "Email già registrata");
    }

    // Validazioni per update
    private void validateUpdate(UUID id, String name, String email) {
        // Validazioni preliminari
        validateName(name);
        validateEmail(email);
        // Unicità email (case-insensitive), escluso l'utente corrente
        boolean existsOther = repo.findByEmailIgnoreCase(email)
                .map(u -> !u.getId().equals(id))
                .orElse(false);
        if (existsOther)
            // Se l'email è già usata da un altro utente, l'utente corrente non può
            // cambiarla
            throw new ValidationException("email", "Email già usata da un altro utente");
    }

    // Regole: name non vuoto, lunghezza 2..50
    private void validateName(String name) {
        // Validazioni preliminari
        if (name == null || name.trim().isEmpty())
            // Se il nome è nullo o vuoto, l'utente non può essere creato
            throw new ValidationException("name", "Nome obbligatorio");
        String n = name.trim();
        if (n.length() < 2)
            // Se il nome è più corto di 2 caratteri, l'utente non può essere creato
            throw new ValidationException("name", "Nome troppo corto (min 2)");
        if (n.length() > 50)
            // Se il nome è più lungo di 50 caratteri, l'utente non può essere creato
            throw new ValidationException("name", "Nome troppo lungo (max 50)");
    }

    // Regole: formato email semplice
    private void validateEmail(String email) {
        // Validazioni preliminari
        if (email == null || email.trim().isEmpty())
            // Se l'email è nullo o vuota, l'utente non può essere creato
            throw new ValidationException("email", "Email obbligatoria");
        String e = email.trim();
        // Controllo basilare; si può sostituire con Bean Validation @Email
        if (!e.contains("@") || !e.contains("."))
            // Se l'email non contiene "@" o "." o è vuota, l'utente non può essere creato
            throw new ValidationException("email", "Formato email non valido");
    }
}
