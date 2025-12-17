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
