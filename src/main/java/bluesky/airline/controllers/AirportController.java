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

@RestController
@RequestMapping("/airports")
@PreAuthorize("hasRole('ADMIN') or hasRole('TOUR_OPERATOR')")
public class AirportController {
    @Autowired
    private AirportService service;

    @GetMapping
    public Page<Airport> list(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Airport> get(@PathVariable UUID id) {
        Airport a = service.findById(id);
        if (a == null)
            throw new bluesky.airline.exceptions.NotFoundException("Airport not found: " + id);
        return ResponseEntity.ok(a);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Airport> create(@RequestBody @Valid AirportReqDTO body) {
        Airport a = new Airport();
        updateAirportFromDTO(a, body);
        a = service.save(a);
        return ResponseEntity.created(java.net.URI.create("/airports/" + a.getId())).body(a);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Airport> update(@PathVariable UUID id, @RequestBody @Valid AirportReqDTO body) {
        Airport found = service.findById(id);
        if (found == null)
            throw new bluesky.airline.exceptions.NotFoundException("Airport not found: " + id);
        updateAirportFromDTO(found, body);
        return ResponseEntity.ok(service.save(found));
    }

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
