package bluesky.airline.controllers;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import bluesky.airline.entities.Aircraft;
import bluesky.airline.entities.PassengerAircraft;
import bluesky.airline.entities.CargoAircraft;
import bluesky.airline.repositories.AircraftRepository;

@RestController
@RequestMapping("/aircrafts")
public class AircraftController {
    private final AircraftRepository aircrafts;

    public AircraftController(AircraftRepository aircrafts) {
        this.aircrafts = aircrafts;
    }

    @GetMapping
    public Page<Aircraft> list(Pageable pageable) {
        return aircrafts.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aircraft> get(@PathVariable UUID id) {
        return aircrafts.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_MANAGER')")
    @PostMapping("/passenger")
    public ResponseEntity<PassengerAircraft> createPassenger(@RequestBody PassengerAircraft body) {
        PassengerAircraft a = (PassengerAircraft) aircrafts.save(body);
        return ResponseEntity.created(java.net.URI.create("/aircrafts/" + a.getId())).body(a);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_MANAGER')")
    @PostMapping("/cargo")
    public ResponseEntity<CargoAircraft> createCargo(@RequestBody CargoAircraft body) {
        CargoAircraft a = (CargoAircraft) aircrafts.save(body);
        return ResponseEntity.created(java.net.URI.create("/aircrafts/" + a.getId())).body(a);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!aircrafts.existsById(id))
            return ResponseEntity.notFound().build();
        aircrafts.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
