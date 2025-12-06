package study_project.demo.repositories;

import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study_project.demo.entities.WeatherData;
import study_project.demo.entities.Flight;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, UUID> {
    Optional<WeatherData> findByFlight(Flight flight);
}
