package bluesky.airline.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.util.UUID;
import bluesky.airline.entities.Flight;
import bluesky.airline.services.FlightService;

// Controller for GraphQL flights queries
@Controller
public class FlightGraphqlController {
    @Autowired
    private FlightService flightService;

    @QueryMapping
    public Page<Flight> flights(@Argument int page, @Argument int size) {
        return flightService.findAll(org.springframework.data.domain.PageRequest.of(page, size));
    }

    @QueryMapping
    public Flight flight(@Argument UUID id) {
        return flightService.findById(id);
    }
}
