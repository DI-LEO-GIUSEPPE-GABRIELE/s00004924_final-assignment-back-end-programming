package bluesky.airline.controllers;

import bluesky.airline.services.WeatherService;
import org.springframework.security.access.prepost.PreAuthorize;
import bluesky.airline.services.AirportService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import bluesky.airline.dto.flight.FlightRespDTO;
import bluesky.airline.dto.weather.WeatherRespDTO;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.time.Instant;
import org.springframework.http.ResponseEntity;
import bluesky.airline.entities.enums.FlightStatus;
import bluesky.airline.exceptions.NotFoundException;
import bluesky.airline.exceptions.ValidationException;
import org.springframework.data.domain.Pageable;
import bluesky.airline.dto.common.EnumRespDTO;
import bluesky.airline.dto.flight.FlightReqDTO;
import bluesky.airline.services.ExchangeRateService;
import bluesky.airline.entities.Flight;
import org.springframework.format.annotation.DateTimeFormat;
import bluesky.airline.entities.WeatherData;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import bluesky.airline.services.FlightService;
import bluesky.airline.entities.Airport;

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
        return page.map(flights::toDTO);
    }

    // Get flight by ID endpoint
    // Endpoint: GET /flights/{id}
    @GetMapping("/{id}")
    public ResponseEntity<FlightRespDTO> get(@PathVariable UUID id) {
        Flight f = flights.findById(id);
        if (f == null)
            throw new NotFoundException("Flight not found: " + id);
        return ResponseEntity.ok(flights.toDTO(f));
    }

    // Create flight endpoint
    // Endpoint: POST /flights
    @PostMapping
    public ResponseEntity<FlightRespDTO> create(@RequestBody @Valid FlightReqDTO body) {
        Flight f = flights.create(body);
        return ResponseEntity.created(java.net.URI.create("/flights/" + f.getId())).body(flights.toDTO(f));
    }

    // Update flight endpoint
    // Endpoint: PUT /flights/{id}
    @PutMapping("/{id}")
    public ResponseEntity<FlightRespDTO> update(@PathVariable UUID id, @RequestBody @Valid FlightReqDTO body) {
        return ResponseEntity.ok(flights.toDTO(flights.update(id, body)));
    }

    // Delete flight endpoint
    // Endpoint: DELETE /flights/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!flights.existsById(id))
            throw new NotFoundException("Flight not found: " + id);
        flights.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Refresh flight weather endpoint, only accessible by ADMIN and FLIGHT_MANAGER
    // roles
    // Endpoint: POST /flights/{id}/weather/refresh
    @PostMapping("/{id}/weather/refresh")
    public ResponseEntity<WeatherRespDTO> refreshWeather(@PathVariable UUID id) {
        Flight f = flights.findById(id);
        if (f == null)
            throw new NotFoundException("Flight not found: " + id);
        Airport dep = f.getDepartureAirport();
        if (dep == null)
            throw new ValidationException(
                    List.of("Flight has no departure airport"));
        Airport found = airportService.findById(dep.getId());
        WeatherData wd = weatherService.refreshForFlight(f, found != null ? found : dep);
        return ResponseEntity.ok(weatherService.toDTO(wd));
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
            throw new NotFoundException("Flight not found: " + id);
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
    public ResponseEntity<List<EnumRespDTO>> getStatuses() {
        return ResponseEntity.ok(Arrays.stream(FlightStatus.values())
                .map(s -> new EnumRespDTO(s.name(), s.name()))
                .toList());
    }
}
