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
    private final ReservationRepository reservations;
    private final FlightRepository flights;
    private final TourOperatorRepository operators;

    public ReservationController(ReservationRepository reservations, FlightRepository flights,
            TourOperatorRepository operators) {
        this.reservations = reservations;
        this.flights = flights;
        this.operators = operators;
    }

    @GetMapping
    public Page<Reservation> list(@RequestParam(required = false) ReservationStatus status, Pageable pageable) {
        if (status != null)
            return reservations.findByStatus(status, pageable);
        return reservations.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> get(@PathVariable UUID id) {
        return reservations.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Reservation> create(@RequestParam UUID flightId, @RequestParam UUID operatorId,
            @RequestBody Reservation body) {
        Flight f = flights.findById(flightId).orElse(null);
        TourOperator op = operators.findById(operatorId).orElse(null);
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
        return reservations.findById(id).map(r -> {
            r.setStatus(status);
            return ResponseEntity.ok(reservations.save(r));
        }).orElse(ResponseEntity.notFound().build());
    }
}
