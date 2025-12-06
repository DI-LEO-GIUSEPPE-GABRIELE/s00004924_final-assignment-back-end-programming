package bluesky.airline.controllers;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import bluesky.airline.entities.Airport;
import bluesky.airline.repositories.AirportRepository;

@RestController
@RequestMapping("/airports")
public class AirportController {
    private final AirportRepository airports;

    public AirportController(AirportRepository airports) {
        this.airports = airports;
    }

    @GetMapping
    public Page<Airport> list(Pageable pageable) {
        return airports.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Airport> get(@PathVariable UUID id) {
        return airports.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Airport> create(@RequestBody Airport body) {
        Airport a = airports.save(body);
        return ResponseEntity.created(java.net.URI.create("/airports/" + a.getId())).body(a);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Airport> update(@PathVariable UUID id, @RequestBody Airport body) {
        return airports.findById(id).map(found -> {
            body.setId(found.getId());
            return ResponseEntity.ok(airports.save(body));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!airports.existsById(id))
            return ResponseEntity.notFound().build();
        airports.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
