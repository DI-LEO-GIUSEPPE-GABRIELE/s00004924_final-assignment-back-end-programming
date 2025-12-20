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

    public Page<Compartment> findAll(Pageable pageable) {
        return compartments.findAll(pageable);
    }

    public java.util.List<Compartment> findAll() {
        return compartments.findAll();
    }

    public Compartment findById(UUID id) {
        return compartments.findById(id)
                .orElseThrow(() -> new bluesky.airline.exceptions.NotFoundException("Compartment not found: " + id));
    }

    public Compartment create(CompartmentReqDTO body) {
        Compartment c = new Compartment();
        c.setCompartmentCode(body.getCompartmentCode());
        c.setDescription(body.getDescription());
        return compartments.save(c);
    }

    public Compartment update(UUID id, CompartmentReqDTO body) {
        Compartment c = findById(id);
        c.setCompartmentCode(body.getCompartmentCode());
        c.setDescription(body.getDescription());
        return compartments.save(c);
    }

    public void delete(UUID id) {
        compartments.deleteById(id);
    }

    public boolean existsById(UUID id) {
        return compartments.existsById(id);
    }
}
