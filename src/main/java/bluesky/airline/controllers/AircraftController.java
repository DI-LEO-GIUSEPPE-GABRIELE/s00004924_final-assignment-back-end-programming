package bluesky.airline.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import bluesky.airline.dto.aircraft.AircraftReqDTO;
import bluesky.airline.dto.common.EnumRespDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import bluesky.airline.entities.Aircraft;
import bluesky.airline.exceptions.NotFoundException;
import bluesky.airline.services.AircraftService;

// Controller for aircraft management, only accessible by ADMIN and TOUR_OPERATOR roles
// Endpoint: /aircrafts
@RestController
@RequestMapping("/aircrafts")
@PreAuthorize("hasRole('ADMIN') or hasRole('TOUR_OPERATOR')")
public class AircraftController {
    @Autowired
    private AircraftService service;

    // List all aircrafts with pagination
    // Endpoint: GET /aircrafts
    @GetMapping
    public Page<Aircraft> list(Pageable pageable) {
        return service.findAll(pageable);
    }

    // Get details of a specific aircraft by ID
    // Endpoint: GET /aircrafts/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Aircraft> get(@PathVariable UUID id) {
        Aircraft a = service.findById(id);
        if (a == null)
            throw new NotFoundException("Aircraft not found: " + id);
        return ResponseEntity.ok(a);
    }

    // Create a new aircraft
    // Endpoint: POST /aircrafts
    @PostMapping
    public ResponseEntity<Aircraft> create(@RequestBody @Valid AircraftReqDTO body) {
        Aircraft a = service.create(body);
        return ResponseEntity.created(java.net.URI.create("/aircrafts/" + a.getId())).body(a);
    }

    // Update details of a specific aircraft by ID
    // Endpoint: PUT /aircrafts/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Aircraft> update(@PathVariable UUID id, @RequestBody @Valid AircraftReqDTO body) {
        return ResponseEntity.ok(service.update(id, body));
    }

    // Delete a specific aircraft by ID
    // Endpoint: DELETE /aircrafts/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!service.existsById(id))
            throw new NotFoundException("Aircraft not found: " + id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Get all aircraft types
    // Endpoint: GET /aircrafts/types
    @GetMapping("/types")
    @PreAuthorize("permitAll()")
    public ResponseEntity<java.util.List<EnumRespDTO>> getTypes() {
        return ResponseEntity.ok(java.util.List.of(
                new EnumRespDTO("PASSENGER", "PASSENGER"),
                new EnumRespDTO("CARGO", "CARGO")));
    }
}
