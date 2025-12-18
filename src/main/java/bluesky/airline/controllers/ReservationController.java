package bluesky.airline.controllers;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import bluesky.airline.entities.Flight;
import bluesky.airline.entities.Reservation;
import bluesky.airline.entities.TourOperator;
import bluesky.airline.entities.enums.ReservationStatus;
import bluesky.airline.dto.reservation.ReservationReqDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/reservations")
@PreAuthorize("hasRole('ADMIN') or hasRole('TOUR_OPERATOR')")
public class ReservationController {
    @org.springframework.beans.factory.annotation.Autowired
    private bluesky.airline.services.ReservationService reservations;
    @org.springframework.beans.factory.annotation.Autowired
    private bluesky.airline.services.FlightService flights;
    @org.springframework.beans.factory.annotation.Autowired
    private bluesky.airline.services.TourOperatorService operators;

    @GetMapping
    public Page<Reservation> list(@RequestParam(required = false) ReservationStatus status, Pageable pageable) {
        if (status != null)
            return reservations.findByStatus(status, pageable);
        return reservations.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> get(@PathVariable UUID id) {
        Reservation r = reservations.findById(id);
        if (r == null)
            throw new bluesky.airline.exceptions.NotFoundException("Reservation not found: " + id);
        return ResponseEntity.ok(r);
    }

    @PostMapping
    public ResponseEntity<Reservation> create(@RequestBody @Valid ReservationReqDTO body) {
        Reservation r = new Reservation();
        updateReservationFromDTO(r, body);

        if (r.getReservationDate() == null)
            r.setReservationDate(Instant.now());
        if (r.getStatus() == null)
            r.setStatus(ReservationStatus.PENDING);

        r = reservations.save(r);
        return ResponseEntity.created(java.net.URI.create("/reservations/" + r.getId())).body(r);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TOUR_OPERATOR')")
    @PutMapping("/{id}/status")
    public ResponseEntity<Reservation> updateStatus(@PathVariable UUID id, @RequestParam ReservationStatus status) {
        Reservation r = reservations.findById(id);
        if (r == null)
            throw new bluesky.airline.exceptions.NotFoundException("Reservation not found: " + id);
        r.setStatus(status);
        return ResponseEntity.ok(reservations.save(r));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!reservations.existsById(id))
            throw new bluesky.airline.exceptions.NotFoundException("Reservation not found: " + id);
        reservations.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void updateReservationFromDTO(Reservation r, ReservationReqDTO body) {
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
}
