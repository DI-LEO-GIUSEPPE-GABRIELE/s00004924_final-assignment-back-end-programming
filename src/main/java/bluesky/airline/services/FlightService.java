package bluesky.airline.services;

import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import bluesky.airline.entities.Flight;
import bluesky.airline.entities.enums.FlightStatus;
import bluesky.airline.repositories.FlightRepository;

@Service
public class FlightService {
    @Autowired
    private FlightRepository flights;

    public Page<Flight> findAll(Pageable pageable) {
        return flights.findAll(pageable);
    }

    public Flight findById(UUID id) {
        return flights.findById(id).orElse(null);
    }

    public Page<Flight> search(String code, Instant from, Instant to, Pageable pageable) {
        return flights.search(code, from, to, pageable);
    }

    public Page<Flight> findByStatus(FlightStatus status, Pageable pageable) {
        return flights.findByStatus(status, pageable);
    }

    public Flight save(Flight flight) {
        return flights.save(flight);
    }

    public void delete(UUID id) {
        flights.deleteById(id);
    }

    public boolean existsById(UUID id) {
        return flights.existsById(id);
    }
}
