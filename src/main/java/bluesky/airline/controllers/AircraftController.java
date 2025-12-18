package bluesky.airline.controllers;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import bluesky.airline.entities.Aircraft;
import bluesky.airline.entities.PassengerAircraft;
import bluesky.airline.entities.CargoAircraft;
import bluesky.airline.services.AircraftService;
import bluesky.airline.dto.aircraft.AircraftReqDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/aircrafts")
@PreAuthorize("hasRole('ADMIN') or hasRole('TOUR_OPERATOR')")
public class AircraftController {
    @Autowired
    private AircraftService service;

    @GetMapping
    public Page<Aircraft> list(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aircraft> get(@PathVariable UUID id) {
        Aircraft a = service.findById(id);
        if (a == null)
            throw new bluesky.airline.exceptions.NotFoundException("Aircraft not found: " + id);
        return ResponseEntity.ok(a);
    }

    @PostMapping
    public ResponseEntity<Aircraft> create(@RequestBody @Valid AircraftReqDTO body) {
        Aircraft a = createAircraftFromDTO(body);
        a = service.save(a);
        return ResponseEntity.created(java.net.URI.create("/aircrafts/" + a.getId())).body(a);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aircraft> update(@PathVariable UUID id, @RequestBody @Valid AircraftReqDTO body) {
        Aircraft found = service.findById(id);
        if (found == null)
            throw new bluesky.airline.exceptions.NotFoundException("Aircraft not found: " + id);

        if (found instanceof PassengerAircraft && !"PASSENGER".equalsIgnoreCase(body.getType())) {
            throw new bluesky.airline.exceptions.ValidationException(
                    java.util.List.of("type: Cannot change aircraft type from PASSENGER"));
        }
        if (found instanceof CargoAircraft && !"CARGO".equalsIgnoreCase(body.getType())) {
            throw new bluesky.airline.exceptions.ValidationException(
                    java.util.List.of("type: Cannot change aircraft type from CARGO"));
        }

        updateAircraftFromDTO(found, body);
        return ResponseEntity.ok(service.save(found));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!service.existsById(id))
            throw new bluesky.airline.exceptions.NotFoundException("Aircraft not found: " + id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Aircraft createAircraftFromDTO(AircraftReqDTO body) {
        Aircraft a;
        if ("PASSENGER".equalsIgnoreCase(body.getType())) {
            PassengerAircraft p = new PassengerAircraft();
            p.setTotalSeats(body.getTotalSeats());
            a = p;
        } else if ("CARGO".equalsIgnoreCase(body.getType())) {
            CargoAircraft c = new CargoAircraft();
            c.setMaxLoadCapacity(body.getMaxLoadCapacity());
            a = c;
        } else {
            throw new bluesky.airline.exceptions.ValidationException(
                    java.util.List.of("type: Invalid aircraft type (PASSENGER/CARGO)"));
        }
        a.setBrand(body.getBrand());
        a.setModel(body.getModel());
        return a;
    }

    private void updateAircraftFromDTO(Aircraft a, AircraftReqDTO body) {
        a.setBrand(body.getBrand());
        a.setModel(body.getModel());
        if (a instanceof PassengerAircraft p && body.getTotalSeats() != null) {
            p.setTotalSeats(body.getTotalSeats());
        }
        if (a instanceof CargoAircraft c && body.getMaxLoadCapacity() != null) {
            c.setMaxLoadCapacity(body.getMaxLoadCapacity());
        }
    }
}
