package bluesky.airline.services;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import bluesky.airline.entities.Flight;
import bluesky.airline.repositories.FlightRepository;

@Service
public class FlightService {
    private final FlightRepository flights;

    public FlightService(FlightRepository flights) {
        this.flights = flights;
    }

    public Page<Flight> findAll(Pageable pageable) {
        return flights.findAll(pageable);
    }

    public Flight findById(UUID id) {
        return flights.findById(id).orElse(null);
    }
}
