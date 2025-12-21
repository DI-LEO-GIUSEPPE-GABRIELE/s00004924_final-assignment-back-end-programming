package bluesky.airline.controllers;

import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import bluesky.airline.entities.Airport;
import bluesky.airline.entities.Flight;
import bluesky.airline.entities.WeatherData;
import bluesky.airline.entities.enums.FlightStatus;
import bluesky.airline.services.AirportService;
import bluesky.airline.services.FlightService;
import bluesky.airline.services.WeatherService;
import bluesky.airline.services.ExchangeRateService;
import bluesky.airline.dto.flight.FlightReqDTO;
import bluesky.airline.dto.flight.FlightRespDTO;
import bluesky.airline.dto.airport.AirportRespDTO;
import bluesky.airline.dto.aircraft.AircraftRespDTO;
import bluesky.airline.dto.weather.WeatherRespDTO;
import jakarta.validation.Valid;
import bluesky.airline.entities.Aircraft;
import bluesky.airline.entities.PassengerAircraft;
import bluesky.airline.entities.CargoAircraft;

// Controller for flight management, accessible by ADMIN and FLIGHT_MANAGER roles
// Endpoint: /flights
@RestController
@RequestMapping("/flights")
@PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_MANAGER')")
public class FlightController {
    @Autowired
    private FlightService flights;
    @Autowired
    private AirportService airportService;
    @Autowired
    private WeatherService weatherService;
    @Autowired
    private ExchangeRateService exchangeRateService;

    // List flights endpoint
    // Endpoint: GET /flights
    @GetMapping
    public Page<FlightRespDTO> list(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) FlightStatus status,
            Pageable pageable) {
        Page<Flight> page;
        if (code != null || from != null || to != null) {
            page = flights.search(code, from, to, pageable);
        } else if (status != null) {
            page = flights.findByStatus(status, pageable);
        } else {
            page = flights.findAll(pageable);
        }
        return page.map(this::toDTO);
    }

    // Get flight by ID endpoint
    // Endpoint: GET /flights/{id}
    @GetMapping("/{id}")
    public ResponseEntity<FlightRespDTO> get(@PathVariable UUID id) {
        Flight f = flights.findById(id);
        if (f == null)
            throw new bluesky.airline.exceptions.NotFoundException("Flight not found: " + id);
        return ResponseEntity.ok(toDTO(f));
    }

    // Create flight endpoint
    // Endpoint: POST /flights
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_MANAGER')")
    public ResponseEntity<FlightRespDTO> create(@RequestBody @Valid FlightReqDTO body) {
        Flight f = flights.create(body);
        return ResponseEntity.created(java.net.URI.create("/flights/" + f.getId())).body(toDTO(f));
    }

    // Update flight endpoint
    // Endpoint: PUT /flights/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_MANAGER')")
    public ResponseEntity<FlightRespDTO> update(@PathVariable UUID id, @RequestBody @Valid FlightReqDTO body) {
        return ResponseEntity.ok(toDTO(flights.update(id, body)));
    }

    // Delete flight endpoint
    // Endpoint: DELETE /flights/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_MANAGER')")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!flights.existsById(id))
            throw new bluesky.airline.exceptions.NotFoundException("Flight not found: " + id);
        flights.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Refresh flight weather endpoint, only accessible by ADMIN and FLIGHT_MANAGER
    // roles
    // Endpoint: POST /flights/{id}/weather/refresh
    @PostMapping("/{id}/weather/refresh")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_MANAGER')")
    public ResponseEntity<?> refreshWeather(@PathVariable UUID id) {
        Flight f = flights.findById(id);
        if (f == null)
            throw new bluesky.airline.exceptions.NotFoundException("Flight not found: " + id);
        Airport dep = f.getDepartureAirport();
        if (dep == null)
            throw new bluesky.airline.exceptions.ValidationException(
                    java.util.List.of("Flight has no departure airport"));
        Airport found = airportService.findById(dep.getId());
        WeatherData wd = weatherService.refreshForFlight(f, found != null ? found : dep);
        return ResponseEntity.ok(toDTO(wd));
    }

    // Convert flight price endpoint
    // Endpoint: GET /flights/{id}/price/convert
    @GetMapping("/{id}/price/convert")
    @PreAuthorize("permitAll()")
    public ResponseEntity<java.math.BigDecimal> convertPrice(
            @PathVariable UUID id,
            @RequestParam String target,
            @RequestParam(defaultValue = "EUR") String base) {
        Flight f = flights.findById(id);
        if (f == null)
            throw new bluesky.airline.exceptions.NotFoundException("Flight not found: " + id);
        if (f.getBasePrice() == null) {
            return ResponseEntity.ok(java.math.BigDecimal.ZERO);
        }
        java.math.BigDecimal converted = exchangeRateService.convert(f.getBasePrice(), base, target);
        return ResponseEntity.ok(converted);
    }

    // Get all flight statuses
    // Endpoint: GET /flights/statuses
    @GetMapping("/statuses")
    @PreAuthorize("permitAll()")
    public ResponseEntity<java.util.List<bluesky.airline.dto.common.EnumRespDTO>> getStatuses() {
        return ResponseEntity.ok(java.util.Arrays.stream(FlightStatus.values())
                .map(s -> new bluesky.airline.dto.common.EnumRespDTO(s.name(), s.name()))
                .toList());
    }

    private FlightRespDTO toDTO(Flight f) {
        FlightRespDTO dto = new FlightRespDTO();
        dto.setId(f.getId());
        dto.setFlightCode(f.getFlightCode());
        dto.setDepartureDate(f.getDepartureDate());
        dto.setArrivalDate(f.getArrivalDate());
        dto.setBasePrice(f.getBasePrice());
        dto.setStatus(f.getStatus());
        if (f.getDepartureAirport() != null) {
            dto.setDepartureAirport(toDTO(f.getDepartureAirport()));
        }
        if (f.getArrivalAirport() != null) {
            dto.setArrivalAirport(toDTO(f.getArrivalAirport()));
        }
        if (f.getAircraft() != null) {
            dto.setAircraft(toDTO(f.getAircraft()));
        }
        if (f.getCompartments() != null) {
            dto.setCompartmentCodes(f.getCompartments().stream()
                    .map(c -> c.getCompartmentCode())
                    .toList());
        }
        return dto;
    }

    private AirportRespDTO toDTO(Airport a) {
        AirportRespDTO dto = new AirportRespDTO();
        dto.setId(a.getId());
        dto.setCode(a.getCode());
        dto.setName(a.getName());
        dto.setCity(a.getCity());
        dto.setCountry(a.getCountry());
        return dto;
    }

    private AircraftRespDTO toDTO(Aircraft a) {
        AircraftRespDTO dto = new AircraftRespDTO();
        dto.setId(a.getId());
        dto.setBrand(a.getBrand());
        dto.setModel(a.getModel());

        if (a instanceof PassengerAircraft) {
            dto.setType("PASSENGER");
            dto.setTotalSeats(((PassengerAircraft) a).getTotalSeats());
        } else if (a instanceof CargoAircraft) {
            dto.setType("CARGO");
            dto.setMaxLoadCapacity(((CargoAircraft) a).getMaxLoadCapacity());
        }

        return dto;
    }

    private WeatherRespDTO toDTO(WeatherData w) {
        WeatherRespDTO dto = new WeatherRespDTO();
        dto.setId(w.getId());
        if (w.getFlight() != null)
            dto.setFlightId(w.getFlight().getId());
        dto.setTemperature(w.getTemperature());
        dto.setDescription(w.getDescription());
        dto.setRetrievedAt(w.getRetrievedAt());
        return dto;
    }

}