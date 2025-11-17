package study_project.demo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import study_project.demo.entities.User;
import org.springframework.stereotype.Service;

/**
 * Service: logica applicativa e storage in memoria per la risorsa User.
 */
@Service
public class UserService {
    private final Map<Long, User> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0); // generatore incrementale degli ID
    // Regola semplice per email (didattica): almeno un '@' e un '.' dopo
    private static final Pattern EMAIL_RE = Pattern.compile("^.+@.+\\..+$");

    // Read All: restituisce tutti gli utenti
    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }

    // Read All filtrato: nameContains e emailDomain
    public List<User> findAllFiltered(String nameContains, String emailDomain) {
        String nc = nameContains != null ? nameContains.trim().toLowerCase() : null;
        String ed = emailDomain != null ? emailDomain.trim().toLowerCase() : null;
        return store.values().stream()
                .filter(u -> nc == null || (u.getName() != null && u.getName().toLowerCase().contains(nc)))
                .filter(u -> ed == null || (u.getEmail() != null && u.getEmail().toLowerCase().endsWith("@" + ed)))
                .collect(java.util.stream.Collectors.toList());
    }

    // Read One: trova utente per ID
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    // Create: crea un utente con ID auto-generato
    public User create(String name, String email) {
        validateCreate(name, email);
        Long id = seq.incrementAndGet();
        User u = new User(id, name, email);
        store.put(id, u);
        return u;
    }

    // Update: aggiorna campi dell'utente se esiste
    public Optional<User> update(Long id, String name, String email) {
        User existing = store.get(id);
        if (existing == null) return Optional.empty();
        validateUpdate(id, name, email);
        existing.setName(name);
        existing.setEmail(email);
        return Optional.of(existing);
    }

    // Delete: rimuove utente per ID
    public boolean delete(Long id) {
        return store.remove(id) != null;
    }

    // Validazioni per create
    private void validateCreate(String name, String email) {
        validateName(name);
        validateEmail(email);
        // Unicità email (case-insensitive)
        boolean exists = store.values().stream().anyMatch(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email));
        if (exists) throw new ValidationException("email", "Email già registrata");
    }

    // Validazioni per update
    private void validateUpdate(Long id, String name, String email) {
        validateName(name);
        validateEmail(email);
        boolean existsOther = store.values().stream().anyMatch(u -> !u.getId().equals(id) && u.getEmail() != null && u.getEmail().equalsIgnoreCase(email));
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
        if (!EMAIL_RE.matcher(e).matches()) throw new ValidationException("email", "Formato email non valido");
    }
}