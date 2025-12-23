package bluesky.airline.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import bluesky.airline.entities.Aircraft;

// Repository for Aircraft entities
@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, UUID> {
}
