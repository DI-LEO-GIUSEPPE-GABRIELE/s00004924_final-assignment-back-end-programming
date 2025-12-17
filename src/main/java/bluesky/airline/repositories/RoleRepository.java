package bluesky.airline.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bluesky.airline.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    java.util.Optional<Role> findByNameIgnoreCase(String name);
}
