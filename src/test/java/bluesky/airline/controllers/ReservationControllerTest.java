package bluesky.airline.controllers;

import bluesky.airline.dto.reservation.ReservationReqDTO;
import bluesky.airline.entities.Airport;
import bluesky.airline.entities.Flight;
import bluesky.airline.entities.PassengerAircraft;
import bluesky.airline.entities.Role;
import bluesky.airline.entities.User;
import bluesky.airline.entities.enums.FlightStatus;
import bluesky.airline.entities.enums.ReservationStatus;
import bluesky.airline.repositories.AircraftRepository;
import bluesky.airline.repositories.AirportRepository;
import bluesky.airline.repositories.FlightRepository;
import bluesky.airline.repositories.RoleRepository;
import bluesky.airline.repositories.UserRepository;
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
import bluesky.airline.services.WeatherService;
import bluesky.airline.services.ExchangeRateService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Test class for ReservationController
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private AircraftRepository aircraftRepository;

    @Autowired
    private RoleRepository roleRepository;

    @MockitoBean
    private WeatherService weatherService;

    @MockitoBean
    private ExchangeRateService exchangeRateService;

    // Test for creating reservations by admin users
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateReservation() throws Exception {
        Role tourOpRole = roleRepository.findByNameIgnoreCase("TOUR_OPERATOR")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("TOUR_OPERATOR");
                    r.setRoleCode(2);
                    return roleRepository.save(r);
                });
        User user = new User();
        user.setName("Tour");
        user.setSurname("Operator");
        user.setUsername("tour_op");
        user.setEmail("tour@example.com");
        user.setPassword("password");
        user.setRoles(java.util.Set.of(tourOpRole));
        user = userRepository.save(user);

        Airport dep = new Airport();
        dep.setCode("DEP2");
        dep.setName("Departure 2");
        dep.setCity("City C");
        dep.setCountry("Country C");
        dep = airportRepository.save(dep);

        Airport arr = new Airport();
        arr.setCode("ARR2");
        arr.setName("Arrival 2");
        arr.setCity("City D");
        arr.setCountry("Country D");
        arr = airportRepository.save(arr);

        PassengerAircraft aircraft = new PassengerAircraft();
        aircraft.setBrand("Boeing");
        aircraft.setModel("777");
        aircraft.setTotalSeats(300);
        aircraft = aircraftRepository.save(aircraft);

        Flight flight = new Flight();
        flight.setFlightCode("BS456");
        flight.setDepartureDate(Instant.now().plus(2, ChronoUnit.DAYS));
        flight.setArrivalDate(Instant.now().plus(2, ChronoUnit.DAYS).plus(4, ChronoUnit.HOURS));
        flight.setBasePrice(new BigDecimal("200.00"));
        flight.setStatus(FlightStatus.SCHEDULED);
        flight.setDepartureAirport(dep);
        flight.setArrivalAirport(arr);
        flight.setAircraft(aircraft);
        flight = flightRepository.save(flight);

        ReservationReqDTO req = new ReservationReqDTO();
        req.setUserId(user.getId());
        req.setFlightIds(List.of(flight.getId()));
        req.setStatus(ReservationStatus.CONFIRMED);

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }
}
