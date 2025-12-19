package bluesky.airline.controllers;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import bluesky.airline.entities.Airport;
import bluesky.airline.services.AirportService;
import bluesky.airline.dto.airport.AirportReqDTO;
import jakarta.validation.Valid;

// Controller for airport management, only accessible by ADMIN and TOUR_OPERATOR roles
// Endpoint: /airports
@RestController
@RequestMapping("/airports")
@PreAuthorize("hasRole('ADMIN') or hasRole('TOUR_OPERATOR')")
public class AirportController {
    @Autowired
    private AirportService service;

    // List all airports with pagination
    // Endpoint: GET /airports
    @GetMapping
    public Page<Airport> list(Pageable pageable) {
        return service.findAll(pageable);
    }

    // Get details of a specific airport by ID
    // Endpoint: GET /airports/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Airport> get(@PathVariable UUID id) {
        Airport a = service.findById(id);
        if (a == null)
            throw new bluesky.airline.exceptions.NotFoundException("Airport not found: " + id);
        return ResponseEntity.ok(a);
    }

    // Create a new airport, only accessible by ADMIN role
    // Endpoint: POST /airports
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Airport> create(@RequestBody @Valid AirportReqDTO body) {
        Airport a = new Airport();
        updateAirportFromDTO(a, body);
        a = service.save(a);
        return ResponseEntity.created(java.net.URI.create("/airports/" + a.getId())).body(a);
    }

    // Update details of a specific airport by ID, only accessible by ADMIN role
    // Endpoint: PUT /airports/{id}
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Airport> update(@PathVariable UUID id, @RequestBody @Valid AirportReqDTO body) {
        Airport found = service.findById(id);
        if (found == null)
            throw new bluesky.airline.exceptions.NotFoundException("Airport not found: " + id);
        updateAirportFromDTO(found, body);
        return ResponseEntity.ok(service.save(found));
    }

    // Delete a specific airport by ID, only accessible by ADMIN role
    // Endpoint: DELETE /airports/{id}
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!service.existsById(id))
            throw new bluesky.airline.exceptions.NotFoundException("Airport not found: " + id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void updateAirportFromDTO(Airport a, AirportReqDTO body) {
        a.setCode(body.getCode());
        a.setName(body.getName());
        a.setCity(body.getCity());
        a.setCountry(body.getCountry());
    }
}
