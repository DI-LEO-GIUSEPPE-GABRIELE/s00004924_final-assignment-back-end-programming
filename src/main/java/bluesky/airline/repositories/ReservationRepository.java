package bluesky.airline.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import bluesky.airline.entities.enums.ReservationStatus;
import bluesky.airline.entities.Reservation;
import java.util.UUID;

// Repository for Reservation entities
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);
}
