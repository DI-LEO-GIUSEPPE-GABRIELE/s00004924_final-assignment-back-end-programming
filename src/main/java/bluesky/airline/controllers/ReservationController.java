package bluesky.airline.controllers;

import bluesky.airline.entities.enums.ReservationStatus;
import bluesky.airline.exceptions.NotFoundException;
import bluesky.airline.dto.reservation.ReservationReqDTO;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.UUID;
import bluesky.airline.entities.Reservation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import jakarta.validation.Valid;
import bluesky.airline.dto.reservation.ReservationRespDTO;
import org.springframework.http.ResponseEntity;
import bluesky.airline.services.ReservationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;

// Controller for reservation management, accessible by ADMIN and TOUR_OPERATOR roles
// Endpoint: /reservations
@RestController
@RequestMapping("/reservations")
@PreAuthorize("hasRole('ADMIN') or hasRole('TOUR_OPERATOR')")
public class ReservationController {
    @Autowired
    private ReservationService reservations;

    // List reservations endpoint
    // Endpoint: GET /reservations
    @GetMapping
    public Page<ReservationRespDTO> list(@RequestParam(required = false) ReservationStatus status, Pageable pageable) {
        Page<Reservation> page;
        if (status != null)
            page = reservations.findByStatus(status, pageable);
        else
            page = reservations.findAll(pageable);
        return page.map(reservations::toDTO);
    }

    // Get reservation details endpoint
    // Endpoint: GET /reservations/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ReservationRespDTO> get(@PathVariable UUID id) {
        Reservation r = reservations.findById(id);
        if (r == null)
            throw new NotFoundException("Reservation not found: " + id);
        return ResponseEntity.ok(reservations.toDTO(r));
    }

    // Create reservation endpoint
    // Endpoint: POST /reservations
    @PostMapping
    public ResponseEntity<ReservationRespDTO> create(@RequestBody @Valid ReservationReqDTO body) {
        Reservation r = reservations.create(body);
        return ResponseEntity.created(java.net.URI.create("/reservations/" + r.getId())).body(reservations.toDTO(r));
    }

    // Update reservation status endpoint, only accessible by ADMIN and
    // TOUR_OPERATOR roles
    // Endpoint: PUT /reservations/{id}/status
    @PutMapping("/{id}/status")
    public ResponseEntity<ReservationRespDTO> updateStatus(@PathVariable UUID id,
            @RequestParam ReservationStatus status) {
        Reservation r = reservations.findById(id);
        if (r == null)
            throw new NotFoundException("Reservation not found: " + id);
        r.setStatus(status);
        return ResponseEntity.ok(reservations.toDTO(reservations.save(r)));
    }

    // Delete reservation endpoint, only accessible by ADMIN role
    // Endpoint: DELETE /reservations/{id}
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!reservations.existsById(id))
            throw new NotFoundException("Reservation not found: " + id);
        reservations.delete(id);
        return ResponseEntity.noContent().build();
    }
}
