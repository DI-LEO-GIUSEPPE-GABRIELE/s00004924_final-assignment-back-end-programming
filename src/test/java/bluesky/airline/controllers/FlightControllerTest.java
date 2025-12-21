package bluesky.airline.controllers;

import bluesky.airline.dto.flight.FlightReqDTO;
import bluesky.airline.entities.Airport;
import bluesky.airline.entities.PassengerAircraft;
import bluesky.airline.entities.enums.FlightStatus;
import bluesky.airline.repositories.AircraftRepository;
import bluesky.airline.repositories.AirportRepository;
import bluesky.airline.services.ExchangeRateService;
import bluesky.airline.services.WeatherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private AircraftRepository aircraftRepository;

    @MockitoBean
    private WeatherService weatherService;

    @MockitoBean
    private ExchangeRateService exchangeRateService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateAndListFlight() throws Exception {
        // Setup dependencies
        Airport dep = new Airport();
        dep.setCode("DEP");
        dep.setName("Departure Airport");
        dep.setCity("City A");
        dep.setCountry("Country A");
        dep = airportRepository.save(dep);

        Airport arr = new Airport();
        arr.setCode("ARR");
        arr.setName("Arrival Airport");
        arr.setCity("City B");
        arr.setCountry("Country B");
        arr = airportRepository.save(arr);

        PassengerAircraft aircraft = new PassengerAircraft();
        aircraft.setBrand("Boeing");
        aircraft.setModel("737");
        aircraft.setTotalSeats(200);
        aircraft = aircraftRepository.save(aircraft);

        FlightReqDTO req = new FlightReqDTO();
        req.setFlightCode("BS123");
        req.setDepartureDate(Instant.now().plus(1, ChronoUnit.DAYS));
        req.setArrivalDate(Instant.now().plus(1, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));
        req.setBasePrice(new BigDecimal("150.00"));
        req.setStatus(FlightStatus.SCHEDULED);
        req.setDepartureAirportId(dep.getId());
        req.setArrivalAirportId(arr.getId());
        req.setAircraftId(aircraft.getId());
        req.setCompartmentCodes(List.of("ECONOMY", "BUSINESS"));

        mockMvc.perform(post("/flights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.flightCode").value("BS123"));

        mockMvc.perform(get("/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
