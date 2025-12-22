package bluesky.airline.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import bluesky.airline.entities.Reservation;
import org.springframework.stereotype.Repository;
import bluesky.airline.entities.enums.ReservationStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Repository for Reservation entities
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);
}
