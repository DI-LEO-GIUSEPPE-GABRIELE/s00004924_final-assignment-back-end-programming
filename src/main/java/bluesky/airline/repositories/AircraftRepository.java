package bluesky.airline.repositories;

import org.springframework.stereotype.Repository;
import java.util.UUID;
import bluesky.airline.entities.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository for Aircraft entities
@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, UUID> {
}
