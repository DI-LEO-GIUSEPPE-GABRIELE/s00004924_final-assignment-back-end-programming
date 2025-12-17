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
import bluesky.airline.repositories.FlightRepository;
import bluesky.airline.repositories.ReservationRepository;
import bluesky.airline.repositories.TourOperatorRepository;

@RestController
@RequestMapping("/reservations")
@org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN') or hasRole('TOUR_OPERATOR')")
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
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(r);
    }

    @PostMapping
    public ResponseEntity<Reservation> create(@RequestParam UUID flightId, @RequestParam UUID operatorId,
            @RequestBody Reservation body) {
        Flight f = flights.findById(flightId);
        TourOperator op = operators.findById(operatorId);
        if (f == null || op == null)
            return ResponseEntity.badRequest().build();
        body.setFlight(f);
        body.setTourOperator(op);
        if (body.getReservationDate() == null)
            body.setReservationDate(Instant.now());
        if (body.getStatus() == null)
            body.setStatus(ReservationStatus.PENDING);
        Reservation r = reservations.save(body);
        return ResponseEntity.created(java.net.URI.create("/reservations/" + r.getId())).body(r);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TOUR_OPERATOR')")
    @PutMapping("/{id}/status")
    public ResponseEntity<Reservation> updateStatus(@PathVariable UUID id, @RequestParam ReservationStatus status) {
        Reservation r = reservations.findById(id);
        if (r == null)
            return ResponseEntity.notFound().build();
        r.setStatus(status);
        return ResponseEntity.ok(reservations.save(r));
    }
}
