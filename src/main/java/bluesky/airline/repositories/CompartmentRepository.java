package bluesky.airline.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import bluesky.airline.entities.Compartment;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompartmentRepository extends JpaRepository<Compartment, UUID> {
    Page<Compartment> findByFlightId(UUID flightId, Pageable pageable);
}
