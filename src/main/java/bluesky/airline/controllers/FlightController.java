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
import bluesky.airline.entities.WeatherData;
import bluesky.airline.entities.enums.FlightStatus;
import bluesky.airline.services.AirportService;
import bluesky.airline.services.FlightService;
import bluesky.airline.services.ExchangeRateService;
import bluesky.airline.services.WeatherService;

@RestController
@RequestMapping("/flights")
@org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_MANAGER')")
public class FlightController {
    @Autowired
    private FlightService flights;
    @Autowired
    private AirportService airportService;
    @Autowired
    private WeatherService weatherService;
    @Autowired
    private ExchangeRateService rateService;

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
        Flight f = flights.findById(id);
        if (f == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(f);
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
        Flight found = flights.findById(id);
        if (found == null)
            return ResponseEntity.notFound().build();
        body.setId(found.getId());
        return ResponseEntity.ok(flights.save(body));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!flights.existsById(id))
            return ResponseEntity.notFound().build();
        flights.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('FLIGHT_MANAGER')")
    @PostMapping("/{id}/weather/refresh")
    public ResponseEntity<?> refreshWeather(@PathVariable UUID id) {
        Flight f = flights.findById(id);
        if (f == null)
            return ResponseEntity.notFound().build();
        Airport dep = f.getDepartureAirport();
        if (dep == null)
            return ResponseEntity.badRequest().build();
        Airport found = airportService.findById(dep.getId());
        WeatherData wd = weatherService.refreshForFlight(f, found != null ? found : dep);
        return ResponseEntity.ok(wd);
    }

    @GetMapping("/{id}/price/convert")
    public ResponseEntity<?> convertPrice(@PathVariable UUID id,
            @RequestParam String target,
            @RequestParam(defaultValue = "EUR") String base) {
        Flight f = flights.findById(id);
        if (f == null)
            return ResponseEntity.notFound().build();
        BigDecimal converted = rateService.convert(f.getBasePrice(), base, target);
        return ResponseEntity
                .ok(Map.of("base", base, "target", target, "amount", f.getBasePrice(), "converted", converted));
    }
}
