package bluesky.airline.graphql;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import bluesky.airline.entities.Flight;
import bluesky.airline.repositories.FlightRepository;
import bluesky.airline.services.ExchangeRateService;

@Controller
public class FlightGraphqlController {
    private final FlightRepository flights;
    private final ExchangeRateService rates;

    public FlightGraphqlController(FlightRepository flights, ExchangeRateService rates) {
        this.flights = flights;
        this.rates = rates;
    }

    @QueryMapping
    public List<Flight> flights(@Argument int page, @Argument int size) {
        return flights.findAll(org.springframework.data.domain.PageRequest.of(page, size)).getContent();
    }

    @QueryMapping
    public Flight flight(@Argument UUID id) {
        return flights.findById(id).orElse(null);
    }

    @QueryMapping
    public BigDecimal convertPrice(@Argument UUID flightId, @Argument String base, @Argument String target) {
        return flights.findById(flightId)
                .map(f -> rates.convert(f.getBasePrice(), base == null ? "EUR" : base, target))
                .orElse(null);
    }
}
