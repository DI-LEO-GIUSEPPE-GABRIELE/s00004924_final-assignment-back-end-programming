package bluesky.airline.services;

import bluesky.airline.repositories.AirportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;
import bluesky.airline.dto.airport.AirportReqDTO;
import bluesky.airline.dto.airport.AirportRespDTO;
import bluesky.airline.entities.Airport;
import bluesky.airline.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

// Service for Airport entities
@Service
public class AirportService {
    @Autowired
    private AirportRepository airports;

    // Find all airports with pagination
    public Page<Airport> findAll(Pageable pageable) {
        return airports.findAll(pageable);
    }

    // Find an airport by its ID
    public Airport findById(UUID id) {
        return airports.findById(id).orElse(null);
    }

    // Create a new airport
    public Airport create(AirportReqDTO body) {
        Airport a = new Airport();
        a.setCode(body.getCode());
        a.setName(body.getName());
        a.setCity(body.getCity());
        a.setCountry(body.getCountry());
        return airports.save(a);
    }

    // Update an existing airport
    public Airport update(UUID id, AirportReqDTO body) {
        Airport found = findById(id);
        if (found == null) {
            throw new NotFoundException("Airport not found: " + id);
        }
        found.setCode(body.getCode());
        found.setName(body.getName());
        found.setCity(body.getCity());
        found.setCountry(body.getCountry());
        return airports.save(found);
    }

    // Save an airport (create or update)
    public Airport save(Airport airport) {
        return airports.save(airport);
    }

    // Delete an airport by its ID
    public void delete(UUID id) {
        airports.deleteById(id);
    }

    // Check if an airport exists by its ID
    public boolean existsById(UUID id) {
        return airports.existsById(id);
    }

    // Convert an Airport entity to an AirportRespDTO
    public AirportRespDTO toDTO(Airport a) {
        AirportRespDTO dto = new AirportRespDTO();
        dto.setId(a.getId());
        dto.setCode(a.getCode());
        dto.setName(a.getName());
        dto.setCity(a.getCity());
        dto.setCountry(a.getCountry());
        return dto;
    }
}
