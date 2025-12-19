package bluesky.airline.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bluesky.airline.entities.Aircraft;

// Repository for Aircraft entities
@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, UUID> {
}
