package bluesky.airline.repositories;

import org.springframework.stereotype.Repository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import bluesky.airline.entities.Role;

// Repository for Role entities
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    java.util.Optional<Role> findByNameIgnoreCase(String name);
}
