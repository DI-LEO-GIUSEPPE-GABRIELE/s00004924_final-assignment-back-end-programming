package bluesky.airline.services;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import bluesky.airline.entities.Reservation;
import bluesky.airline.entities.enums.ReservationStatus;
import bluesky.airline.repositories.ReservationRepository;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservations;

    public Page<Reservation> findAll(Pageable pageable) {
        return reservations.findAll(pageable);
    }

    public Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable) {
        return reservations.findByStatus(status, pageable);
    }

    public Reservation findById(UUID id) {
        return reservations.findById(id).orElse(null);
    }

    public Reservation save(Reservation reservation) {
        return reservations.save(reservation);
    }
}
