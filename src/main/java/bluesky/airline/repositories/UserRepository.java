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
      // Find a user by email ignoring case
      Optional<User> findByEmailIgnoreCase(String email);

      // Find users by name containing (case-insensitive)
      List<User> findByNameContainingIgnoreCase(String name);

      // Paged variant of derived queries
      Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);

      // Find users by email ending with a suffix (case-insensitive)
      List<User> findByEmailEndingWithIgnoreCase(String suffix);

      // Paged variant of derived queries
      Page<User> findByEmailEndingWithIgnoreCase(String suffix, Pageable pageable);

      // Find users by name and email ending with a specific suffix (case-insensitive)
      List<User> findByNameContainingIgnoreCaseAndEmailEndingWithIgnoreCase(String name, String suffix);

      // Paged variant of derived queries
      Page<User> findByNameContainingIgnoreCaseAndEmailEndingWithIgnoreCase(String name, String suffix,
                  Pageable pageable);

      @EntityGraph(attributePaths = { "roles" })
      Optional<User> findWithRolesById(UUID id);

      // JPQL: optional filters with case-insensitive LIKE
      @Query("select u from User u \n" +
                  "where (:name is null or lower(u.name) like lower(concat('%', :name, '%'))) \n" +
                  "and (:domain is null or lower(u.email) like lower(concat('%@', :domain)))")
      Page<User> searchUsersJpql(@Param("name") String name, @Param("domain") String domain, Pageable pageable);

      // Native (PostgreSQL): use ILIKE for case-insensitive matching and
      // concatenation
      @Query(value = "select * from users u \n" +
                  "where (:name is null or u.name ilike '%' || :name || '%') \n" +
                  "and (:domain is null or u.email ilike '%@' || :domain)", countQuery = "select count(*) from users u \n"
                              +
                              "where (:name is null or u.name ilike '%' || :name || '%') \n" +
                              "and (:domain is null or u.email ilike '%@' || :domain)", nativeQuery = true)
      Page<User> searchUsersNative(@Param("name") String name, @Param("domain") String domain, Pageable pageable);
}
