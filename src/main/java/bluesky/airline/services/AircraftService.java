package bluesky.airline.services;

import bluesky.airline.repositories.AircraftRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import bluesky.airline.dto.aircraft.AircraftReqDTO;
import bluesky.airline.dto.aircraft.AircraftRespDTO;
import bluesky.airline.entities.Aircraft;
import bluesky.airline.entities.CargoAircraft;
import bluesky.airline.entities.PassengerAircraft;
import bluesky.airline.exceptions.NotFoundException;
import bluesky.airline.exceptions.ValidationException;
import java.util.List;
import java.util.UUID;

// Service for Aircraft entities
@Service
public class AircraftService {
    @Autowired
    private AircraftRepository aircrafts;

    // Find all aircrafts with pagination
    public Page<Aircraft> findAll(Pageable pageable) {
        return aircrafts.findAll(pageable);
    }

    // Find an aircraft by its ID
    public Aircraft findById(UUID id) {
        return aircrafts.findById(id).orElse(null);
    }

    // Create a new aircraft
    public Aircraft create(AircraftReqDTO body) {
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
            throw new ValidationException(
                    List.of("type: Invalid aircraft type (PASSENGER/CARGO)"));
        }
        a.setBrand(body.getBrand());
        a.setModel(body.getModel());
        return aircrafts.save(a);
    }

    // Update an existing aircraft
    public Aircraft update(UUID id, AircraftReqDTO body) {
        Aircraft found = findById(id);
        if (found == null) {
            throw new NotFoundException("Aircraft not found: " + id);
        }

        if (found instanceof PassengerAircraft
                && !"PASSENGER".equalsIgnoreCase(body.getType())) {
            throw new ValidationException(
                    List.of("type: Cannot change aircraft type from PASSENGER"));
        }
        if (found instanceof CargoAircraft && !"CARGO".equalsIgnoreCase(body.getType())) {
            throw new ValidationException(
                    List.of("type: Cannot change aircraft type from CARGO"));
        }

        if ("PASSENGER".equalsIgnoreCase(body.getType())
                && found instanceof PassengerAircraft p) {
            p.setTotalSeats(body.getTotalSeats());
        } else if ("CARGO".equalsIgnoreCase(body.getType())
                && found instanceof CargoAircraft c) {
            c.setMaxLoadCapacity(body.getMaxLoadCapacity());
        }

        found.setBrand(body.getBrand());
        found.setModel(body.getModel());

        return aircrafts.save(found);
    }

    // Save an aircraft (create or update)
    public Aircraft save(Aircraft aircraft) {
        return aircrafts.save(aircraft);
    }

    // Delete an aircraft by its ID
    public void delete(UUID id) {
        aircrafts.deleteById(id);
    }

    // Check if an aircraft exists by its ID
    public boolean existsById(UUID id) {
        return aircrafts.existsById(id);
    }

    // Convert an Aircraft entity to an AircraftRespDTO
    public AircraftRespDTO toDTO(Aircraft a) {
        AircraftRespDTO dto = new AircraftRespDTO();
        dto.setId(a.getId());
        dto.setBrand(a.getBrand());
        dto.setModel(a.getModel());

        if (a instanceof PassengerAircraft) {
            dto.setType("PASSENGER");
            dto.setTotalSeats(((PassengerAircraft) a).getTotalSeats());
        } else if (a instanceof CargoAircraft) {
            dto.setType("CARGO");
            dto.setMaxLoadCapacity(((CargoAircraft) a).getMaxLoadCapacity());
        }

        return dto;
    }
}
