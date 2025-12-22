package bluesky.airline.repositories;

import org.springframework.data.jpa.repository.Query;
import bluesky.airline.entities.enums.FlightStatus;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import bluesky.airline.entities.Flight;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import java.util.UUID;
import org.springframework.data.domain.Page;

// Repository for Flight entities
@Repository
public interface FlightRepository extends JpaRepository<Flight, UUID> {
    Page<Flight> findByStatus(FlightStatus status, Pageable pageable);

    @Query("select f from Flight f where (:code is null or lower(f.flightCode) like lower(concat('%', :code, '%'))) and (:from is null or f.departureDate >= :from) and (:to is null or f.departureDate <= :to)")
    Page<Flight> search(@Param("code") String code, @Param("from") Instant from, @Param("to") Instant to,
            Pageable pageable);
}
