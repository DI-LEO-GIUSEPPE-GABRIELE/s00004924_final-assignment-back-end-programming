package bluesky.airline.services;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import bluesky.airline.entities.Airport;
import bluesky.airline.repositories.AirportRepository;

@Service
public class AirportService {
    @Autowired
    private AirportRepository airports;

    public Page<Airport> findAll(Pageable pageable) {
        return airports.findAll(pageable);
    }

    public Airport findById(UUID id) {
        return airports.findById(id).orElse(null);
    }

    public Airport save(Airport airport) {
        return airports.save(airport);
    }

    public void delete(UUID id) {
        airports.deleteById(id);
    }

    public boolean existsById(UUID id) {
        return airports.existsById(id);
    }
}
