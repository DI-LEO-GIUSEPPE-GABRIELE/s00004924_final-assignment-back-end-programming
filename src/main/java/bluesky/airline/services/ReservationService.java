package bluesky.airline.services;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import bluesky.airline.entities.Reservation;
import bluesky.airline.entities.enums.ReservationStatus;
import bluesky.airline.repositories.ReservationRepository;
import bluesky.airline.entities.Flight;
import bluesky.airline.entities.TourOperator;
import java.time.Instant;

// Service for Reservation entities
@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservations;
    @Autowired
    private FlightService flights;
    @Autowired
    private TourOperatorService operators;

    public Page<Reservation> findAll(Pageable pageable) {
        return reservations.findAll(pageable);
    }

    public Reservation create(bluesky.airline.dto.reservation.ReservationReqDTO body) {
        Reservation r = new Reservation();
        updateReservationFromDTO(r, body);

        if (r.getReservationDate() == null)
            r.setReservationDate(Instant.now());
        if (r.getStatus() == null)
            r.setStatus(ReservationStatus.PENDING);

        return reservations.save(r);
    }

    private void updateReservationFromDTO(Reservation r, bluesky.airline.dto.reservation.ReservationReqDTO body) {
        Flight f = flights.findById(body.getFlightId());
        if (f == null)
            throw new bluesky.airline.exceptions.NotFoundException("Flight not found: " + body.getFlightId());
        r.setFlight(f);

        TourOperator op = operators.findById(body.getTourOperatorId());
        if (op == null)
            throw new bluesky.airline.exceptions.NotFoundException(
                    "Tour Operator not found: " + body.getTourOperatorId());
        r.setTourOperator(op);

        r.setTotalPrice(body.getTotalPrice());
        if (body.getStatus() != null)
            r.setStatus(body.getStatus());
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

    public void delete(UUID id) {
        reservations.deleteById(id);
    }

    public boolean existsById(UUID id) {
        return reservations.existsById(id);
    }
}
