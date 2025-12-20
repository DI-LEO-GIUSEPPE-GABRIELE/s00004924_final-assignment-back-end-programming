package bluesky.airline.services;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import bluesky.airline.entities.Compartment;
import bluesky.airline.entities.Flight;
import bluesky.airline.repositories.CompartmentRepository;
import bluesky.airline.dto.compartment.CompartmentReqDTO;
import bluesky.airline.exceptions.NotFoundException;

// Service for Compartment entities
@Service
public class CompartmentService {
    @Autowired
    private CompartmentRepository compartments;
    @Autowired
    private FlightService flights;

    public Page<Compartment> findAll(Pageable pageable) {
        return compartments.findAll(pageable);
    }

    public Page<Compartment> findByFlightId(UUID flightId, Pageable pageable) {
        return compartments.findByFlightId(flightId, pageable);
    }

    public Compartment create(CompartmentReqDTO body) {
        Compartment c = new Compartment();
        updateCompartmentFromDTO(c, body);
        return compartments.save(c);
    }

    public Compartment update(UUID id, CompartmentReqDTO body) {
        Compartment c = findById(id);
        if (c == null) {
            throw new NotFoundException("Compartment not found: " + id);
        }
        updateCompartmentFromDTO(c, body);
        return compartments.save(c);
    }

    private void updateCompartmentFromDTO(Compartment c, CompartmentReqDTO body) {
        c.setCompartmentCode(body.getCompartmentCode());
        
        Flight f = flights.findById(body.getFlightId());
        if (f == null) {
            throw new NotFoundException("Flight not found: " + body.getFlightId());
        }
        c.setFlight(f);
    }

    public Compartment findById(UUID id) {
        return compartments.findById(id).orElse(null);
    }

    public void delete(UUID id) {
        compartments.deleteById(id);
    }

    public boolean existsById(UUID id) {
        return compartments.existsById(id);
    }
}
