package study_project.demo.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study_project.demo.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    List<User> findByNameContainingIgnoreCase(String name);
    List<User> findByEmailEndingWithIgnoreCase(String suffix);
    List<User> findByNameContainingIgnoreCaseAndEmailEndingWithIgnoreCase(String name, String suffix);
}