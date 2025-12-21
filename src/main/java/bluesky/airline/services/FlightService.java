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
import bluesky.airline.entities.Airport;
import bluesky.airline.entities.Aircraft;
import bluesky.airline.repositories.CompartmentRepository;
import bluesky.airline.entities.Compartment;
import bluesky.airline.dto.flight.FlightRespDTO;
import org.springframework.transaction.annotation.Transactional;

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

    public Page<Flight> findAll(Pageable pageable) {
        return flights.findAll(pageable);
    }

    @Transactional
    public Flight create(bluesky.airline.dto.flight.FlightReqDTO body) {
        Flight f = new Flight();
        updateFlightFromDTO(f, body);
        f = flights.save(f);

        if (body.getCompartmentCodes() != null && !body.getCompartmentCodes().isEmpty()) {
            java.util.Set<String> uniqueCodes = new java.util.HashSet<>(body.getCompartmentCodes());
            java.util.Set<Compartment> validCompartments = new java.util.HashSet<>();
            java.util.List<String> invalidCodes = new java.util.ArrayList<>();

            for (String code : uniqueCodes) {
                java.util.Optional<Compartment> c = compartments.findByCompartmentCode(code);
                if (c.isPresent()) {
                    validCompartments.add(c.get());
                } else {
                    invalidCodes.add(code);
                }
            }

            if (!invalidCodes.isEmpty()) {
                String available = compartments.findAll().stream()
                        .map(Compartment::getCompartmentCode)
                        .collect(java.util.stream.Collectors.joining(", "));
                throw new bluesky.airline.exceptions.ValidationException(
                        java.util.List.of("Invalid compartment codes: " + invalidCodes + ". Available: " + available));
            }
            f.setCompartments(validCompartments);
            f = flights.save(f);
        }
        return f;
    }

    @Transactional
    public Flight update(UUID id, bluesky.airline.dto.flight.FlightReqDTO body) {
        Flight f = findById(id);
        if (f == null) {
            throw new bluesky.airline.exceptions.NotFoundException("Flight not found: " + id);
        }
        updateFlightFromDTO(f, body);
        Flight saved = flights.save(f);

        if (body.getCompartmentCodes() != null) {
            java.util.Set<String> uniqueCodes = new java.util.HashSet<>(body.getCompartmentCodes());
            java.util.Set<Compartment> validCompartments = new java.util.HashSet<>();
            java.util.List<String> invalidCodes = new java.util.ArrayList<>();

            for (String code : uniqueCodes) {
                java.util.Optional<Compartment> c = compartments.findByCompartmentCode(code);
                if (c.isPresent()) {
                    validCompartments.add(c.get());
                } else {
                    invalidCodes.add(code);
                }
            }

            if (!invalidCodes.isEmpty()) {
                String available = compartments.findAll().stream()
                        .map(Compartment::getCompartmentCode)
                        .collect(java.util.stream.Collectors.joining(", "));
                throw new bluesky.airline.exceptions.ValidationException(
                        java.util.List.of("Invalid compartment codes: " + invalidCodes + ". Available: " + available));
            }
            f.setCompartments(validCompartments);
            saved = flights.save(f);
        }
        return saved;
    }

    private void updateFlightFromDTO(Flight f, bluesky.airline.dto.flight.FlightReqDTO body) {
        f.setFlightCode(body.getFlightCode());
        f.setDepartureDate(body.getDepartureDate());
        f.setArrivalDate(body.getArrivalDate());
        f.setBasePrice(body.getBasePrice());
        f.setStatus(body.getStatus());

        Airport dep = airportService.findById(body.getDepartureAirportId());
        if (dep == null)
            throw new bluesky.airline.exceptions.NotFoundException(
                    "Departure Airport not found: " + body.getDepartureAirportId());
        f.setDepartureAirport(dep);

        Airport arr = airportService.findById(body.getArrivalAirportId());
        if (arr == null)
            throw new bluesky.airline.exceptions.NotFoundException(
                    "Arrival Airport not found: " + body.getArrivalAirportId());
        f.setArrivalAirport(arr);

        Aircraft aircraft = aircraftService.findById(body.getAircraftId());
        if (aircraft == null)
            throw new bluesky.airline.exceptions.NotFoundException("Aircraft not found: " + body.getAircraftId());
        f.setAircraft(aircraft);
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
