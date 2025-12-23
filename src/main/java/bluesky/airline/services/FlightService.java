package bluesky.airline.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import bluesky.airline.dto.flight.FlightReqDTO;
import bluesky.airline.dto.flight.FlightRespDTO;
import bluesky.airline.repositories.CompartmentRepository;
import bluesky.airline.entities.Airport;
import bluesky.airline.entities.Flight;
import bluesky.airline.entities.Aircraft;
import bluesky.airline.entities.enums.FlightStatus;
import bluesky.airline.exceptions.NotFoundException;
import bluesky.airline.exceptions.ValidationException;
import bluesky.airline.entities.Compartment;
import bluesky.airline.repositories.FlightRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

// Service for Flight entities
@Service
public class FlightService {
    @Autowired
    private FlightRepository flights;
    @Autowired
    private CompartmentRepository compartments;
    @Autowired
    private AirportService airportService;
    @Autowired
    private AircraftService aircraftService;

    // Find all flights with pagination
    public Page<Flight> findAll(Pageable pageable) {
        return flights.findAll(pageable);
    }

    // Create a new flight
    @Transactional
    public Flight create(FlightReqDTO body) {
        Flight f = new Flight();
        updateFlightFromDTO(f, body);
        f = flights.save(f);

        if (body.getCompartmentCodes() != null && !body.getCompartmentCodes().isEmpty()) {
            Set<String> uniqueCodes = new HashSet<>(body.getCompartmentCodes());
            Set<Compartment> validCompartments = new HashSet<>();
            Set<String> invalidCodes = new HashSet<>();

            for (String code : uniqueCodes) {
                Optional<Compartment> c = compartments.findByCompartmentCode(code);
                if (c.isPresent()) {
                    validCompartments.add(c.get());
                } else {
                    invalidCodes.add(code);
                }
            }

            if (!invalidCodes.isEmpty()) {
                String available = compartments.findAll().stream()
                        .map(Compartment::getCompartmentCode)
                        .collect(Collectors.joining(", "));
                throw new ValidationException(
                        List.of("Invalid compartment codes: " + invalidCodes + ". Available: " + available));
            }
            f.setCompartments(validCompartments);
            f = flights.save(f);
        }
        return f;
    }

    // Update an existing flight
    @Transactional
    public Flight update(UUID id, FlightReqDTO body) {
        Flight f = findById(id);
        if (f == null) {
            throw new NotFoundException("Flight not found: " + id);
        }
        updateFlightFromDTO(f, body);
        Flight saved = flights.save(f);

        if (body.getCompartmentCodes() != null) {
            Set<String> uniqueCodes = new HashSet<>(body.getCompartmentCodes());
            Set<Compartment> validCompartments = new HashSet<>();
            List<String> invalidCodes = new ArrayList<>();

            for (String code : uniqueCodes) {
                Optional<Compartment> c = compartments.findByCompartmentCode(code);
                if (c.isPresent()) {
                    validCompartments.add(c.get());
                } else {
                    invalidCodes.add(code);
                }
            }

            if (!invalidCodes.isEmpty()) {
                String available = compartments.findAll().stream()
                        .map(Compartment::getCompartmentCode)
                        .collect(Collectors.joining(", "));
                throw new ValidationException(
                        List.of("Invalid compartment codes: " + invalidCodes + ". Available: " + available));
            }
            f.setCompartments(validCompartments);
            saved = flights.save(f);
        }
        return saved;
    }

    // Update flight fields from DTO
    private void updateFlightFromDTO(Flight f, FlightReqDTO body) {
        f.setFlightCode(body.getFlightCode());
        f.setDepartureDate(body.getDepartureDate());
        f.setArrivalDate(body.getArrivalDate());
        f.setBasePrice(body.getBasePrice());
        f.setStatus(body.getStatus());

        Airport dep = airportService.findById(body.getDepartureAirportId());
        if (dep == null)
            throw new NotFoundException(
                    "Departure Airport not found: " + body.getDepartureAirportId());
        f.setDepartureAirport(dep);

        Airport arr = airportService.findById(body.getArrivalAirportId());
        if (arr == null)
            throw new NotFoundException(
                    "Arrival Airport not found: " + body.getArrivalAirportId());
        f.setArrivalAirport(arr);

        Aircraft aircraft = aircraftService.findById(body.getAircraftId());
        if (aircraft == null)
            throw new NotFoundException("Aircraft not found: " + body.getAircraftId());
        f.setAircraft(aircraft);
    }

    // Find a flight by its ID
    public Flight findById(UUID id) {
        return flights.findById(id).orElse(null);
    }

    // Search flights by flight code, date range, and pagination
    public Page<Flight> search(String code, Instant from, Instant to, Pageable pageable) {
        return flights.search(code, from, to, pageable);
    }

    // Find all flights with a specific status and pagination
    public Page<Flight> findByStatus(FlightStatus status, Pageable pageable) {
        return flights.findByStatus(status, pageable);
    }

    // Save a flight (create or update)
    public Flight save(Flight flight) {
        return flights.save(flight);
    }

    // Delete a flight by its ID
    public void delete(UUID id) {
        flights.deleteById(id);
    }

    // Check if a flight exists by its ID
    public boolean existsById(UUID id) {
        return flights.existsById(id);
    }

    // Convert a Flight entity to a FlightRespDTO
    public FlightRespDTO toDTO(Flight f) {
        FlightRespDTO dto = new FlightRespDTO();
        dto.setId(f.getId());
        dto.setFlightCode(f.getFlightCode());
        dto.setDepartureDate(f.getDepartureDate());
        dto.setArrivalDate(f.getArrivalDate());
        dto.setBasePrice(f.getBasePrice());
        dto.setStatus(f.getStatus());
        if (f.getDepartureAirport() != null) {
            dto.setDepartureAirport(airportService.toDTO(f.getDepartureAirport()));
        }
        if (f.getArrivalAirport() != null) {
            dto.setArrivalAirport(airportService.toDTO(f.getArrivalAirport()));
        }
        if (f.getAircraft() != null) {
            dto.setAircraft(aircraftService.toDTO(f.getAircraft()));
        }
        if (f.getCompartments() != null) {
            dto.setCompartmentCodes(f.getCompartments().stream()
                    .map(c -> c.getCompartmentCode())
                    .toList());
        }
        return dto;
    }
}
