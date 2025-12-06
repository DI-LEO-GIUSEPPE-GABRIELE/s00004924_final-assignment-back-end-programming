package bluesky.airline.repositories;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bluesky.airline.entities.Reservation;
import bluesky.airline.entities.enums.ReservationStatus;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);
}
