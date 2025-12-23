package bluesky.airline.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import bluesky.airline.entities.Compartment;
import java.util.Optional;
import java.util.UUID;

public interface CompartmentRepository extends JpaRepository<Compartment, UUID> {
    Optional<Compartment> findByCompartmentCode(String compartmentCode);
}
