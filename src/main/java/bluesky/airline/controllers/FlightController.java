package bluesky.airline.controllers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
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
import bluesky.airline.repositories.AirportRepository;
import bluesky.airline.repositories.FlightRepository;
import bluesky.airline.services.ExchangeRateService;
import bluesky.airline.services.WeatherService;

@RestController
@RequestMapping("/flights")
@org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_MANAGER')")
public class FlightController {
    private final FlightRepository flights;
    private final AirportRepository airports;
    private final WeatherService weatherService;
    private final ExchangeRateService rateService;

    public FlightController(FlightRepository flights, AirportRepository airports, WeatherService weatherService,
            ExchangeRateService rateService) {
        this.flights = flights;
        this.airports = airports;
        this.weatherService = weatherService;
        this.rateService = rateService;
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<Flight> get(@PathVariable UUID id) {
        return flights.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_MANAGER')")
    @PostMapping
    public ResponseEntity<Flight> create(@RequestBody Flight body) {
        Flight f = flights.save(body);
        return ResponseEntity.created(java.net.URI.create("/flights/" + f.getId())).body(f);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<Flight> update(@PathVariable UUID id, @RequestBody Flight body) {
        return flights.findById(id).map(found -> {
            body.setId(found.getId());
            return ResponseEntity.ok(flights.save(body));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!flights.existsById(id))
            return ResponseEntity.notFound().build();
        flights.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_MANAGER')")
    @PostMapping("/{id}/weather/refresh")
    public ResponseEntity<?> refreshWeather(@PathVariable UUID id) {
        return flights.findById(id).map(f -> {
            Airport dep = f.getDepartureAirport();
            if (dep == null)
                return ResponseEntity.badRequest().build();
            WeatherData wd = weatherService.refreshForFlight(f, airports.findById(dep.getId()).orElse(dep));
            return ResponseEntity.ok(wd);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/price/convert")
    public ResponseEntity<?> convertPrice(@PathVariable UUID id,
            @RequestParam String target,
            @RequestParam(defaultValue = "EUR") String base) {
        return flights.findById(id).map(f -> {
            BigDecimal converted = rateService.convert(f.getBasePrice(), base, target);
            return ResponseEntity
                    .ok(Map.of("base", base, "target", target, "amount", f.getBasePrice(), "converted", converted));
        }).orElse(ResponseEntity.notFound().build());
    }
}
