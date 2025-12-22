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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import bluesky.airline.services.WeatherService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.UUID;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import bluesky.airline.services.ExchangeRateService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.boot.test.context.SpringBootTest;

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
        user.setUsername("touroperator");
        user.setEmail("touroperator@example.com");
        user.setPassword("password123");
        user.setRoleCode(1); // TOUR_OPERATOR
        user = userRepository.save(user);

        Airport dep = new Airport();
        dep.setCode("JFK");
        dep.setName("John F. Kennedy International Airport");
        dep.setCity("New York");
        dep.setCountry("USA");
        dep = airportRepository.save(dep);

        Airport arr = new Airport();
        dep.setCode("LGW");
        dep.setName("Gatwick Airport");
        dep.setCity("London");
        dep.setCountry("UK");
        arr = airportRepository.save(arr);

        PassengerAircraft aircraft = new PassengerAircraft();
        aircraft.setBrand("Boeing");
        aircraft.setModel("747");
        aircraft.setTotalSeats(150);
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

    // Test for updating reservation status by admin users
    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateStatusAndGetReservation() throws Exception {
        Role role = roleRepository.findByNameIgnoreCase("TOUR_OPERATOR")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("TOUR_OPERATOR");
                    r.setRoleCode(1);
                    return roleRepository.save(r);
                });

        User user = new User();
        user.setName("Admin");
        user.setSurname("User");
        user.setUsername("adminuser");
        user.setEmail("adminuser@example.com");
        user.setPassword("password123");
        user.setRoleCode(0); // ADMIN
        user = userRepository.save(user);

        Airport dep = new Airport();
        dep.setCode("MXP");
        dep.setName("Milano Malpensa Airport");
        dep.setCity("Milan");
        dep.setCountry("Italy");
        dep = airportRepository.save(dep);

        Airport arr = new Airport();
        arr.setCode("SSH");
        arr.setName("Sharm el-Sheikh Airport");
        arr.setCity("Sharm el-Sheikh");
        arr.setCountry("Egypt");
        arr = airportRepository.save(arr);

        PassengerAircraft aircraft = new PassengerAircraft();
        aircraft.setBrand("Airbus");
        aircraft.setModel("A320");
        aircraft.setTotalSeats(180);
        aircraft = aircraftRepository.save(aircraft);

        Flight f = new Flight();
        f.setFlightCode("BS999");
        f.setDepartureDate(Instant.now().plus(1, ChronoUnit.DAYS));
        f.setArrivalDate(Instant.now().plus(1, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));
        f.setBasePrice(new BigDecimal("100"));
        f.setStatus(FlightStatus.SCHEDULED);
        f.setDepartureAirport(dep);
        f.setArrivalAirport(arr);
        f.setAircraft(aircraft);
        f = flightRepository.save(f);

        ReservationReqDTO req = new ReservationReqDTO();
        req.setUserId(user.getId());
        req.setFlightIds(List.of(f.getId()));
        req.setStatus(ReservationStatus.PENDING);

        String response = mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        mockMvc.perform(put("/reservations/" + id + "/status")
                .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        mockMvc.perform(get("/reservations/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // Test for deleting a reservation by admin users
    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteReservation() throws Exception {
        Role role = roleRepository.findByNameIgnoreCase("TOUR_OPERATOR")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("TOUR_OPERATOR");
                    r.setRoleCode(2);
                    return roleRepository.save(r);
                });

        User user = new User();
        user.setName("Admin");
        user.setSurname("User");
        user.setUsername("adminuser");
        user.setEmail("adminuser@example.com");
        user.setPassword("password123");
        user.setRoleCode(0); // ADMIN
        user = userRepository.save(user);

        Airport dep = new Airport();
        dep.setCode("JFK");
        dep.setName("John F. Kennedy International Airport");
        dep.setCity("New York");
        dep.setCountry("USA");
        dep = airportRepository.save(dep);

        Airport arr = new Airport();
        arr.setCode("MXP");
        arr.setName("Milano Malpensa Airport");
        arr.setCity("Milan");
        arr.setCountry("Italy");
        arr = airportRepository.save(arr);

        PassengerAircraft aircraft = new PassengerAircraft();
        aircraft.setBrand("Boeing");
        aircraft.setModel("747");
        aircraft.setTotalSeats(150);
        aircraft = aircraftRepository.save(aircraft);

        Flight f = new Flight();
        f.setFlightCode("BS888");
        f.setDepartureDate(Instant.now().plus(1, ChronoUnit.DAYS));
        f.setArrivalDate(Instant.now().plus(1, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));
        f.setBasePrice(new BigDecimal("100"));
        f.setStatus(FlightStatus.SCHEDULED);
        f.setDepartureAirport(dep);
        f.setArrivalAirport(arr);
        f.setAircraft(aircraft);
        f = flightRepository.save(f);

        ReservationReqDTO req = new ReservationReqDTO();
        req.setUserId(user.getId());
        req.setFlightIds(List.of(f.getId()));
        req.setStatus(ReservationStatus.PENDING);

        String response = mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        mockMvc.perform(delete("/reservations/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/reservations/" + id))
                .andExpect(status().isNotFound());
    }

    // Test for creating a reservation by flight managers
    @Test
    @WithMockUser(roles = "FLIGHT_MANAGER")
    void testCreateReservationForbidden() throws Exception {
        ReservationReqDTO req = new ReservationReqDTO();
        req.setStatus(ReservationStatus.PENDING);
        req.setUserId(UUID.randomUUID());
        req.setFlightIds(List.of(UUID.randomUUID()));

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }
}
