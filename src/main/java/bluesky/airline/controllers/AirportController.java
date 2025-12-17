package bluesky.airline.controllers;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import bluesky.airline.entities.Airport;
import bluesky.airline.repositories.AirportRepository;

@RestController
@RequestMapping("/airports")
@PreAuthorize("hasRole('ADMIN') or hasRole('TOUR_OPERATOR')")
public class AirportController {
    @org.springframework.beans.factory.annotation.Autowired
    private bluesky.airline.services.AirportService service;

    @GetMapping
    public Page<Airport> list(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Airport> get(@PathVariable UUID id) {
        Airport a = service.findById(id);
        if (a == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(a);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Airport> create(@RequestBody Airport body) {
        Airport a = service.save(body);
        return ResponseEntity.created(java.net.URI.create("/airports/" + a.getId())).body(a);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Airport> update(@PathVariable UUID id, @RequestBody Airport body) {
        Airport found = service.findById(id);
        if (found == null)
            return ResponseEntity.notFound().build();
        body.setId(found.getId());
        return ResponseEntity.ok(service.save(body));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!service.existsById(id))
            return ResponseEntity.notFound().build();
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
