package study_project.demo.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study_project.demo.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Metodo per trovare un utente per email ignorando la case sensitivity
    Optional<User> findByEmailIgnoreCase(String email);

    // Metodo per trovare utenti per nome ignorando la case sensitivity
    List<User> findByNameContainingIgnoreCase(String name);

    // Metodo per trovare utenti per email che termina con un suffisso ignorando la
    // case sensitivity
    List<User> findByEmailEndingWithIgnoreCase(String suffix);

    // Metodo per trovare utenti per nome e email che terminano con uno specifico
    // suffisso ignorando la case sensitivity
    List<User> findByNameContainingIgnoreCaseAndEmailEndingWithIgnoreCase(String name, String suffix);
}