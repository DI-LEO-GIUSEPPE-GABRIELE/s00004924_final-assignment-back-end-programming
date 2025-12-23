package bluesky.airline.graphql;

import bluesky.airline.entities.enums.FlightStatus;
import bluesky.airline.repositories.FlightRepository;
import bluesky.airline.entities.Flight;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import java.time.temporal.ChronoUnit;
import java.time.Instant;
import java.math.BigDecimal;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

// Test class for FlightGraphqlController
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FlightGraphqlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlightRepository flightRepository;

    // Test for listing flights
    @Test
    @WithMockUser(roles = "ADMIN")
    void testListFlights() throws Exception {
        Flight flight = new Flight();
        flight.setFlightCode("BS123");
        flight.setDepartureDate(Instant.now().plus(1, ChronoUnit.DAYS));
        flight.setArrivalDate(Instant.now().plus(1, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));
        flight.setBasePrice(new BigDecimal("200.00"));
        flight.setStatus(FlightStatus.SCHEDULED);
        flightRepository.save(flight);

        String query = "{ \"query\": \"query { flights(page: 0, size: 10) { content { flightCode status basePrice } } }\" }";

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.flights.content").isArray())
                .andExpect(jsonPath("$.data.flights.content[0].flightCode").value("BS123"));
    }
}
