package bluesky.airline.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bluesky.airline.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
       // Metodo per trovare un utente per email ignorando la case sensitivity
       Optional<User> findByEmailIgnoreCase(String email);

       // Metodo per trovare utenti per nome ignorando la case sensitivity
       List<User> findByNameContainingIgnoreCase(String name);

       // Variante paginata per derived queries
       Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);

       // Metodo per trovare utenti per email che termina con un suffisso ignorando la
       // case sensitivity
       List<User> findByEmailEndingWithIgnoreCase(String suffix);

       // Variante paginata per derived queries
       Page<User> findByEmailEndingWithIgnoreCase(String suffix, Pageable pageable);

       // Metodo per trovare utenti per nome e email che terminano con uno specifico
       // suffisso ignorando la case sensitivity
       List<User> findByNameContainingIgnoreCaseAndEmailEndingWithIgnoreCase(String name, String suffix);

       // Variante paginata per derived queries
       Page<User> findByNameContainingIgnoreCaseAndEmailEndingWithIgnoreCase(String name, String suffix,
                     Pageable pageable);

       @EntityGraph(attributePaths = { "roles" })
       Optional<User> findWithRolesById(UUID id);

       // JPQL: filtri opzionali con LIKE case-insensitive
       @Query("select u from User u \n" +
                     "where (:name is null or lower(u.name) like lower(concat('%', :name, '%'))) \n" +
                     "and (:domain is null or lower(u.email) like lower(concat('%@', :domain)))")
       Page<User> searchUsersJpql(@Param("name") String name, @Param("domain") String domain, Pageable pageable);

       // Native (PostgreSQL): uso di ILIKE per case-insensitive e concatenazione
       @Query(value = "select * from users u \n" +
                     "where (:name is null or u.name ilike '%' || :name || '%') \n" +
                     "and (:domain is null or u.email ilike '%@' || :domain)", countQuery = "select count(*) from users u \n"
                                   +
                                   "where (:name is null or u.name ilike '%' || :name || '%') \n" +
                                   "and (:domain is null or u.email ilike '%@' || :domain)", nativeQuery = true)
       Page<User> searchUsersNative(@Param("name") String name, @Param("domain") String domain, Pageable pageable);
}
