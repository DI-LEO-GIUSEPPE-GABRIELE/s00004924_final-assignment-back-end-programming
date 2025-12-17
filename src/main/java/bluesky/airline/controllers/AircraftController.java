package bluesky.airline.controllers;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import bluesky.airline.entities.Aircraft;
import bluesky.airline.entities.PassengerAircraft;
import bluesky.airline.entities.CargoAircraft;

@RestController
@RequestMapping("/aircrafts")
@org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN') or hasRole('TOUR_OPERATOR')")
public class AircraftController {
    @org.springframework.beans.factory.annotation.Autowired
    private bluesky.airline.services.AircraftService service;

    @GetMapping
    public Page<Aircraft> list(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aircraft> get(@PathVariable UUID id) {
        Aircraft a = service.findById(id);
        if (a == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(a);
    }

    @PostMapping("/passenger")
    public ResponseEntity<PassengerAircraft> createPassenger(@RequestBody PassengerAircraft body) {
        PassengerAircraft a = (PassengerAircraft) service.save(body);
        return ResponseEntity.created(java.net.URI.create("/aircrafts/" + a.getId())).body(a);
    }

    @PostMapping("/cargo")
    public ResponseEntity<CargoAircraft> createCargo(@RequestBody CargoAircraft body) {
        CargoAircraft a = (CargoAircraft) service.save(body);
        return ResponseEntity.created(java.net.URI.create("/aircrafts/" + a.getId())).body(a);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!service.existsById(id))
            return ResponseEntity.notFound().build();
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
