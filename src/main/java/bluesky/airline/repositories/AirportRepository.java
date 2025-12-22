package bluesky.airline.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import bluesky.airline.entities.Airport;
import org.springframework.stereotype.Repository;
import java.util.UUID;

// Repository for Airport entities
@Repository
public interface AirportRepository extends JpaRepository<Airport, UUID> {
    Optional<Airport> findByCodeIgnoreCase(String code);
}
