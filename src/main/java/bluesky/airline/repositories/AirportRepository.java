package bluesky.airline.repositories;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bluesky.airline.entities.Airport;

// Repository for Airport entities
@Repository
public interface AirportRepository extends JpaRepository<Airport, UUID> {
    Optional<Airport> findByCodeIgnoreCase(String code);
}
