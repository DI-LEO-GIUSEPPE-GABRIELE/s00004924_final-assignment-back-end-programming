package bluesky.airline.graphql;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import bluesky.airline.entities.Flight;
import bluesky.airline.services.FlightService;
import bluesky.airline.services.ExchangeRateService;

@Controller
public class FlightGraphqlController {
    private final FlightService flightService;
    private final ExchangeRateService rates;

    public FlightGraphqlController(FlightService flightService, ExchangeRateService rates) {
        this.flightService = flightService;
        this.rates = rates;
    }

    @QueryMapping
    public Page<Flight> flights(@Argument int page, @Argument int size) {
        return flightService.findAll(org.springframework.data.domain.PageRequest.of(page, size));
    }

    @QueryMapping
    public Flight flight(@Argument UUID id) {
        return flightService.findById(id);
    }

    @QueryMapping
    public BigDecimal convertPrice(@Argument UUID flightId, @Argument String base, @Argument String target) {
        Flight f = flightService.findById(flightId);
        if (f == null) return null;
        return rates.convert(f.getBasePrice(), base == null ? "EUR" : base, target);
    }
}
