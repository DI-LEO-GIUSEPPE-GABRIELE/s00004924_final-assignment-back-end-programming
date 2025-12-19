package bluesky.airline.services;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import bluesky.airline.entities.Airport;
import bluesky.airline.repositories.AirportRepository;

// Service for Airport entities
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

    public Airport create(bluesky.airline.dto.airport.AirportReqDTO body) {
        Airport a = new Airport();
        a.setCode(body.getCode());
        a.setName(body.getName());
        a.setCity(body.getCity());
        a.setCountry(body.getCountry());
        return airports.save(a);
    }

    public Airport update(UUID id, bluesky.airline.dto.airport.AirportReqDTO body) {
        Airport found = findById(id);
        if (found == null) {
            throw new bluesky.airline.exceptions.NotFoundException("Airport not found: " + id);
        }
        found.setCode(body.getCode());
        found.setName(body.getName());
        found.setCity(body.getCity());
        found.setCountry(body.getCountry());
        return airports.save(found);
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
