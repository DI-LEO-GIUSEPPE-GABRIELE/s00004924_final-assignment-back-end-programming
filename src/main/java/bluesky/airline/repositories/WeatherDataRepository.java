package bluesky.airline.repositories;

import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bluesky.airline.entities.WeatherData;
import bluesky.airline.entities.Flight;

// Repository for WeatherData entities
@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, UUID> {
    Optional<WeatherData> findByFlight(Flight flight);
}
