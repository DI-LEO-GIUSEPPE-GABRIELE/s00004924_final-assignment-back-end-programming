package bluesky.airline.controllers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
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
import bluesky.airline.entities.Aircraft;
import bluesky.airline.entities.WeatherData;
import bluesky.airline.entities.enums.FlightStatus;
import bluesky.airline.services.AirportService;
import bluesky.airline.services.FlightService;
import bluesky.airline.services.ExchangeRateService;
import bluesky.airline.services.WeatherService;
import bluesky.airline.services.AircraftService;
import bluesky.airline.dto.flight.FlightReqDTO;
import jakarta.validation.Valid;

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
    private ExchangeRateService rateService;
    @Autowired
    private AircraftService aircraftService;

    // List flights endpoint
    // Endpoint: GET /flights
    @GetMapping
    public Page<Flight> list(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) FlightStatus status,
            Pageable pageable) {
        if (code != null || from != null || to != null) {
            return flights.search(code, from, to, pageable);
        }
        if (status != null) {
            return flights.findByStatus(status, pageable);
        }
        return flights.findAll(pageable);
    }

    // Get flight by ID endpoint
    // Endpoint: GET /flights/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Flight> get(@PathVariable UUID id) {
        Flight f = flights.findById(id);
        if (f == null)
            throw new bluesky.airline.exceptions.NotFoundException("Flight not found: " + id);
        return ResponseEntity.ok(f);
    }

    // Create flight endpoint
    // Endpoint: POST /flights
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_MANAGER')")
    public ResponseEntity<Flight> create(@RequestBody @Valid FlightReqDTO body) {
        Flight f = new Flight();
        updateFlightFromDTO(f, body);
        f = flights.save(f);
        return ResponseEntity.created(java.net.URI.create("/flights/" + f.getId())).body(f);
    }

    // Update flight endpoint
    // Endpoint: PUT /flights/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_MANAGER')")
    public ResponseEntity<Flight> update(@PathVariable UUID id, @RequestBody @Valid FlightReqDTO body) {
        Flight f = flights.findById(id);
        if (f == null)
            throw new bluesky.airline.exceptions.NotFoundException("Flight not found: " + id);
        updateFlightFromDTO(f, body);
        return ResponseEntity.ok(flights.save(f));
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
        return ResponseEntity.ok(wd);
    }

    // Convert flight price endpoint
    // Endpoint: GET /flights/{id}/price/convert
    @GetMapping("/{id}/price/convert")
    public ResponseEntity<?> convertPrice(@PathVariable UUID id,
            @RequestParam String target,
            @RequestParam(defaultValue = "EUR") String base) {
        Flight f = flights.findById(id);
        if (f == null)
            throw new bluesky.airline.exceptions.NotFoundException("Flight not found: " + id);
        BigDecimal converted = rateService.convert(f.getBasePrice(), base, target);
        return ResponseEntity
                .ok(Map.of("base", base, "target", target, "amount", f.getBasePrice(), "converted", converted));
    }

    private void updateFlightFromDTO(Flight f, FlightReqDTO body) {
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
}
