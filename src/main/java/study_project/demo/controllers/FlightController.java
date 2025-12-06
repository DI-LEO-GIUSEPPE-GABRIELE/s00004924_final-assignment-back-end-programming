package study_project.demo.controllers;

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
import study_project.demo.entities.Airport;
import study_project.demo.entities.Flight;
import study_project.demo.entities.WeatherData;
import study_project.demo.entities.enums.FlightStatus;
import study_project.demo.repositories.AirportRepository;
import study_project.demo.repositories.FlightRepository;
import study_project.demo.services.ExchangeRateService;
import study_project.demo.services.WeatherService;

@RestController
@RequestMapping("/flights")
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
    public Page<Flight> list(@RequestParam(required = false) FlightStatus status, Pageable pageable) {
        if (status != null)
            return flights.findByStatus(status, pageable);
        return flights.findAll(pageable);
    }

    @GetMapping("/search")
    public Page<Flight> search(@RequestParam(required = false) String code,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            Pageable pageable) {
        return flights.search(code, from, to, pageable);
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
