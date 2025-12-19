package bluesky.airline.controllers;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import bluesky.airline.entities.Reservation;
import bluesky.airline.entities.enums.ReservationStatus;
import bluesky.airline.dto.reservation.ReservationReqDTO;
import jakarta.validation.Valid;

// Controller for reservation management, accessible by ADMIN and TOUR_OPERATOR roles
// Endpoint: /reservations
@RestController
@RequestMapping("/reservations")
@PreAuthorize("hasRole('ADMIN') or hasRole('TOUR_OPERATOR')")
public class ReservationController {
    @org.springframework.beans.factory.annotation.Autowired
    private bluesky.airline.services.ReservationService reservations;

    // List reservations endpoint
    // Endpoint: GET /reservations
    @GetMapping
    public Page<Reservation> list(@RequestParam(required = false) ReservationStatus status, Pageable pageable) {
        if (status != null)
            return reservations.findByStatus(status, pageable);
        return reservations.findAll(pageable);
    }

    // Get reservation details endpoint
    // Endpoint: GET /reservations/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> get(@PathVariable UUID id) {
        Reservation r = reservations.findById(id);
        if (r == null)
            throw new bluesky.airline.exceptions.NotFoundException("Reservation not found: " + id);
        return ResponseEntity.ok(r);
    }

    // Create reservation endpoint
    // Endpoint: POST /reservations
    @PostMapping
    public ResponseEntity<Reservation> create(@RequestBody @Valid ReservationReqDTO body) {
        Reservation r = reservations.create(body);
        return ResponseEntity.created(java.net.URI.create("/reservations/" + r.getId())).body(r);
    }

    // Update reservation status endpoint, only accessible by ADMIN and
    // TOUR_OPERATOR roles
    // Endpoint: PUT /reservations/{id}/status
    @PreAuthorize("hasRole('ADMIN') or hasRole('TOUR_OPERATOR')")
    @PutMapping("/{id}/status")
    public ResponseEntity<Reservation> updateStatus(@PathVariable UUID id, @RequestParam ReservationStatus status) {
        Reservation r = reservations.findById(id);
        if (r == null)
            throw new bluesky.airline.exceptions.NotFoundException("Reservation not found: " + id);
        r.setStatus(status);
        return ResponseEntity.ok(reservations.save(r));
    }

    // Delete reservation endpoint, only accessible by ADMIN role
    // Endpoint: DELETE /reservations/{id}
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!reservations.existsById(id))
            throw new bluesky.airline.exceptions.NotFoundException("Reservation not found: " + id);
        reservations.delete(id);
        return ResponseEntity.noContent().build();
    }
}
