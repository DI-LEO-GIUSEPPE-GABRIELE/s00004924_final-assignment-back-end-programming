package bluesky.airline.controllers;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import bluesky.airline.services.WeatherService;
import bluesky.airline.dto.flight.FlightReqDTO;
import bluesky.airline.dto.weather.WeatherRespDTO;
import bluesky.airline.entities.enums.FlightStatus;
import bluesky.airline.entities.Airport;
import bluesky.airline.repositories.AircraftRepository;
import bluesky.airline.repositories.AirportRepository;
import bluesky.airline.entities.PassengerAircraft;
import bluesky.airline.entities.WeatherData;
import bluesky.airline.services.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.time.Instant;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Test class for FlightController
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

        // Test for creating and listing flights by admin users
        @Test
        @WithMockUser(roles = "ADMIN")
        void testCreateAndListFlight() throws Exception {
                Airport dep = new Airport();
                dep.setCode("JFK");
                dep.setName("John F. Kennedy International Airport");
                dep.setCity("New York");
                dep.setCountry("USA");
                dep = airportRepository.save(dep);

                Airport arr = new Airport();
                arr.setCode("LGW");
                arr.setName("Gatwick Airport");
                arr.setCity("London");
                arr.setCountry("UK");
                arr = airportRepository.save(arr);

                PassengerAircraft aircraft = new PassengerAircraft();
                aircraft.setBrand("Boeing");
                aircraft.setModel("747");
                aircraft.setTotalSeats(150);
                aircraft = aircraftRepository.save(aircraft);

                FlightReqDTO req = new FlightReqDTO();
                req.setFlightCode("BS123");
                req.setDepartureDate(Instant.now().plus(1, ChronoUnit.DAYS));
                req.setArrivalDate(Instant.now().plus(1, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));
                req.setBasePrice(new BigDecimal("150"));
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

        // Test for updating and getting a flight by admin users
        @Test
        @WithMockUser(roles = "ADMIN")
        void testUpdateAndGetFlight() throws Exception {
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

                FlightReqDTO req = new FlightReqDTO();
                req.setFlightCode("BS999");
                req.setDepartureDate(Instant.now().plus(1, ChronoUnit.DAYS));
                req.setArrivalDate(Instant.now().plus(1, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));
                req.setBasePrice(new BigDecimal("100.00"));
                req.setStatus(FlightStatus.SCHEDULED);
                req.setDepartureAirportId(dep.getId());
                req.setArrivalAirportId(arr.getId());
                req.setAircraftId(aircraft.getId());
                req.setCompartmentCodes(List.of("ECONOMY"));

                String response = mockMvc.perform(post("/flights")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString();

                String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

                req.setFlightCode("BS999-update");
                mockMvc.perform(put("/flights/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.flightCode").value("BS999-update"));

                mockMvc.perform(get("/flights/" + id))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(id));
        }

        // Test for deleting a flight by admin users
        @Test
        @WithMockUser(roles = "ADMIN")
        void testDeleteFlight() throws Exception {
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

                FlightReqDTO req = new FlightReqDTO();
                req.setFlightCode("BS888");
                req.setDepartureDate(Instant.now().plus(1, ChronoUnit.DAYS));
                req.setArrivalDate(Instant.now().plus(1, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));
                req.setBasePrice(new BigDecimal("100.00"));
                req.setStatus(FlightStatus.SCHEDULED);
                req.setDepartureAirportId(dep.getId());
                req.setArrivalAirportId(arr.getId());
                req.setAircraftId(aircraft.getId());
                req.setCompartmentCodes(List.of("ECONOMY"));

                String response = mockMvc.perform(post("/flights")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString();

                String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

                mockMvc.perform(delete("/flights/" + id))
                                .andExpect(status().isNoContent());

                mockMvc.perform(get("/flights/" + id))
                                .andExpect(status().isNotFound());
        }

        // Test for getting all flight statuses by admin users
        @Test
        @WithMockUser(roles = "ADMIN")
        void testGetFlightStatuses() throws Exception {
                mockMvc.perform(get("/flights/statuses"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray());
        }

        // Test for converting price by admin users
        @Test
        @WithMockUser(roles = "ADMIN")
        void testConvertPrice() throws Exception {
                Airport dep = new Airport();
                dep.setCode("JFK");
                dep.setName("John F. Kennedy International Airport");
                dep.setCity("New York");
                dep.setCountry("USA");
                dep = airportRepository.save(dep);

                Airport arr = new Airport();
                arr.setCode("LGW");
                arr.setName("Gatwick Airport");
                arr.setCity("London");
                arr.setCountry("UK");
                arr = airportRepository.save(arr);

                PassengerAircraft aircraft = new PassengerAircraft();
                aircraft.setBrand("Boeing");
                aircraft.setModel("747");
                aircraft.setTotalSeats(150);
                aircraft = aircraftRepository.save(aircraft);

                FlightReqDTO req = new FlightReqDTO();
                req.setFlightCode("BS777");
                req.setDepartureDate(Instant.now().plus(1, ChronoUnit.DAYS));
                req.setArrivalDate(Instant.now().plus(1, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));
                req.setBasePrice(new BigDecimal("100"));
                req.setStatus(FlightStatus.SCHEDULED);
                req.setDepartureAirportId(dep.getId());
                req.setArrivalAirportId(arr.getId());
                req.setAircraftId(aircraft.getId());
                req.setCompartmentCodes(List.of("ECONOMY"));

                String response = mockMvc.perform(post("/flights")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString();

                String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

                Mockito
                                .when(exchangeRateService.convert(ArgumentMatchers.any(),
                                                ArgumentMatchers.anyString(),
                                                ArgumentMatchers.anyString()))
                                .thenReturn(new BigDecimal("110.00"));

                mockMvc.perform(get("/flights/" + id + "/price/convert")
                                .param("target", "USD"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").value(110.00));
        }

        // Test for checking weather by admin users
        @Test
        @WithMockUser(roles = "ADMIN")
        void testCheckWeather() throws Exception {
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

                FlightReqDTO req = new FlightReqDTO();
                req.setFlightCode("BS-W");
                req.setDepartureDate(Instant.now().plus(1, ChronoUnit.DAYS));
                req.setArrivalDate(Instant.now().plus(1, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));
                req.setBasePrice(new BigDecimal("100.00"));
                req.setStatus(FlightStatus.SCHEDULED);
                req.setDepartureAirportId(dep.getId());
                req.setArrivalAirportId(arr.getId());
                req.setAircraftId(aircraft.getId());
                req.setCompartmentCodes(List.of("ECONOMY"));

                String response = mockMvc.perform(post("/flights")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString();

                String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

                WeatherData wd = new WeatherData();
                wd.setTemperature(25.0);
                wd.setDescription("Sunny");

                WeatherRespDTO dto = new WeatherRespDTO();
                dto.setTemperature(25.0);
                dto.setDescription("Sunny");

                Mockito
                                .when(weatherService.refreshForFlight(ArgumentMatchers.any(), ArgumentMatchers.any()))
                                .thenReturn(wd);

                Mockito
                                .when(weatherService.toDTO(ArgumentMatchers.any()))
                                .thenReturn(dto);

                mockMvc.perform(post("/flights/" + id + "/weather/refresh"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.temperature").value(25.0))
                                .andExpect(jsonPath("$.description").value("Sunny"));
        }

        // Test for creating a flight by tour operator users
        @Test
        @WithMockUser(roles = "TOUR_OPERATOR")
        void testCreateFlightForbidden() throws Exception {
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

                FlightReqDTO req = new FlightReqDTO();
                req.setFlightCode("BS123");
                req.setDepartureDate(Instant.now().plus(1, ChronoUnit.DAYS));
                req.setArrivalDate(Instant.now().plus(1, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));
                req.setBasePrice(new BigDecimal("100.00"));
                req.setStatus(FlightStatus.SCHEDULED);
                req.setDepartureAirportId(dep.getId());
                req.setArrivalAirportId(arr.getId());
                req.setAircraftId(aircraft.getId());
                req.setCompartmentCodes(List.of("BUSINESS"));

                mockMvc.perform(post("/flights")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isForbidden());
        }
}
