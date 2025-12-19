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

// Repository for User entities
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
      Optional<User> findByEmailIgnoreCase(String email);

      List<User> findByNameContainingIgnoreCase(String name);

      Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);

      List<User> findByEmailEndingWithIgnoreCase(String suffix);

      Page<User> findByEmailEndingWithIgnoreCase(String suffix, Pageable pageable);

      List<User> findByNameContainingIgnoreCaseAndEmailEndingWithIgnoreCase(String name, String suffix);

      Page<User> findByNameContainingIgnoreCaseAndEmailEndingWithIgnoreCase(String name, String suffix,
                  Pageable pageable);

      @EntityGraph(attributePaths = { "roles" })
      Optional<User> findWithRolesById(UUID id);

      @Query("select u from User u \n" +
                  "where (:name is null or lower(u.name) like lower(concat('%', :name, '%'))) \n" +
                  "and (:domain is null or lower(u.email) like lower(concat('%@', :domain)))")
      Page<User> searchUsersJpql(@Param("name") String name, @Param("domain") String domain, Pageable pageable);

      @Query(value = "select * from users u \n" +
                  "where (:name is null or u.name ilike '%' || :name || '%') \n" +
                  "and (:domain is null or u.email ilike '%@' || :domain)", countQuery = "select count(*) from users u \n"
                              +
                              "where (:name is null or u.name ilike '%' || :name || '%') \n" +
                              "and (:domain is null or u.email ilike '%@' || :domain)", nativeQuery = true)
      Page<User> searchUsersNative(@Param("name") String name, @Param("domain") String domain, Pageable pageable);
}
