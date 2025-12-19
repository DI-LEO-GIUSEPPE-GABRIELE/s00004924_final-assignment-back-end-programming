package bluesky.airline.services;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import bluesky.airline.entities.Aircraft;
import bluesky.airline.repositories.AircraftRepository;

@Service
public class AircraftService {
    @Autowired
    private AircraftRepository aircrafts;

    public Page<Aircraft> findAll(Pageable pageable) {
        return aircrafts.findAll(pageable);
    }

    public Aircraft findById(UUID id) {
        return aircrafts.findById(id).orElse(null);
    }

    public Aircraft create(bluesky.airline.dto.aircraft.AircraftReqDTO body) {
        Aircraft a;
        if ("PASSENGER".equalsIgnoreCase(body.getType())) {
            bluesky.airline.entities.PassengerAircraft p = new bluesky.airline.entities.PassengerAircraft();
            p.setTotalSeats(body.getTotalSeats());
            a = p;
        } else if ("CARGO".equalsIgnoreCase(body.getType())) {
            bluesky.airline.entities.CargoAircraft c = new bluesky.airline.entities.CargoAircraft();
            c.setMaxLoadCapacity(body.getMaxLoadCapacity());
            a = c;
        } else {
            throw new bluesky.airline.exceptions.ValidationException(
                    java.util.List.of("type: Invalid aircraft type (PASSENGER/CARGO)"));
        }
        a.setBrand(body.getBrand());
        a.setModel(body.getModel());
        return aircrafts.save(a);
    }

    public Aircraft update(UUID id, bluesky.airline.dto.aircraft.AircraftReqDTO body) {
        Aircraft found = findById(id);
        if (found == null) {
            throw new bluesky.airline.exceptions.NotFoundException("Aircraft not found: " + id);
        }

        if (found instanceof bluesky.airline.entities.PassengerAircraft && !"PASSENGER".equalsIgnoreCase(body.getType())) {
            throw new bluesky.airline.exceptions.ValidationException(
                    java.util.List.of("type: Cannot change aircraft type from PASSENGER"));
        }
        if (found instanceof bluesky.airline.entities.CargoAircraft && !"CARGO".equalsIgnoreCase(body.getType())) {
            throw new bluesky.airline.exceptions.ValidationException(
                    java.util.List.of("type: Cannot change aircraft type from CARGO"));
        }

        // Update fields
        if ("PASSENGER".equalsIgnoreCase(body.getType()) && found instanceof bluesky.airline.entities.PassengerAircraft p) {
            p.setTotalSeats(body.getTotalSeats());
        } else if ("CARGO".equalsIgnoreCase(body.getType()) && found instanceof bluesky.airline.entities.CargoAircraft c) {
            c.setMaxLoadCapacity(body.getMaxLoadCapacity());
        }
        
        found.setBrand(body.getBrand());
        found.setModel(body.getModel());
        
        return aircrafts.save(found);
    }

    public Aircraft save(Aircraft aircraft) {
        return aircrafts.save(aircraft);
    }

    public void delete(UUID id) {
        aircrafts.deleteById(id);
    }

    public boolean existsById(UUID id) {
        return aircrafts.existsById(id);
    }
}
