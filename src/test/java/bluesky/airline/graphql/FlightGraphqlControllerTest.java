package bluesky.airline.graphql;

import bluesky.airline.entities.Flight;
import bluesky.airline.entities.enums.FlightStatus;
import bluesky.airline.repositories.FlightRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FlightGraphqlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FlightRepository flightRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListFlights() throws Exception {
        // Prepare data
        Flight flight = new Flight();
        flight.setFlightCode("GQ100");
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
                .andExpect(jsonPath("$.data.flights.content[0].flightCode").value("GQ100"));
    }
}
