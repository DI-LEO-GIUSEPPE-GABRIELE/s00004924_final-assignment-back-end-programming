package bluesky.airline.services;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import bluesky.airline.entities.Compartment;
import bluesky.airline.repositories.CompartmentRepository;
import bluesky.airline.dto.compartment.CompartmentReqDTO;

// Service for Compartment entities
@Service
public class CompartmentService {
    @Autowired
    private CompartmentRepository compartments;

    // Find all compartments with pagination
    public Page<Compartment> findAll(Pageable pageable) {
        return compartments.findAll(pageable);
    }

    // Find all compartments
    public java.util.List<Compartment> findAll() {
        return compartments.findAll();
    }

    // Find a compartment by its ID
    public Compartment findById(UUID id) {
        return compartments.findById(id)
                .orElseThrow(() -> new bluesky.airline.exceptions.NotFoundException("Compartment not found: " + id));
    }

    // Create a new compartment
    public Compartment create(CompartmentReqDTO body) {
        Compartment c = new Compartment();
        c.setCompartmentCode(body.getCompartmentCode());
        c.setDescription(body.getDescription());
        return compartments.save(c);
    }

    // Update an existing compartment
    public Compartment update(UUID id, CompartmentReqDTO body) {
        Compartment c = findById(id);
        c.setCompartmentCode(body.getCompartmentCode());
        c.setDescription(body.getDescription());
        return compartments.save(c);
    }

    // Delete a compartment by its ID
    public void delete(UUID id) {
        compartments.deleteById(id);
    }

    // Check if a compartment exists by its ID
    public boolean existsById(UUID id) {
        return compartments.existsById(id);
    }
}
