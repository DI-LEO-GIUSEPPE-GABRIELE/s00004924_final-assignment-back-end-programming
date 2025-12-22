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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

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

        // Test for updating and getting a flight by admin users
        @Test
        @WithMockUser(roles = "ADMIN")
        void testUpdateAndGetFlight() throws Exception {
                Airport dep = new Airport();
                dep.setCode("D1");
                dep.setName("D1");
                dep.setCity("C1");
                dep.setCountry("C1");
                dep = airportRepository.save(dep);

                Airport arr = new Airport();
                arr.setCode("A1");
                arr.setName("A1");
                arr.setCity("C2");
                arr.setCountry("C2");
                arr = airportRepository.save(arr);

                PassengerAircraft aircraft = new PassengerAircraft();
                aircraft.setBrand("B");
                aircraft.setModel("M");
                aircraft.setTotalSeats(100);
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

                req.setFlightCode("BS999-UPD");
                mockMvc.perform(put("/flights/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.flightCode").value("BS999-UPD"));

                mockMvc.perform(get("/flights/" + id))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(id));
        }

        // Test for deleting a flight by admin users
        @Test
        @WithMockUser(roles = "ADMIN")
        void testDeleteFlight() throws Exception {
                Airport dep = new Airport();
                dep.setCode("D2");
                dep.setName("D2");
                dep.setCity("C1");
                dep.setCountry("C1");
                dep = airportRepository.save(dep);

                Airport arr = new Airport();
                arr.setCode("A2");
                arr.setName("A2");
                arr.setCity("C2");
                arr.setCountry("C2");
                arr = airportRepository.save(arr);

                PassengerAircraft aircraft = new PassengerAircraft();
                aircraft.setBrand("B");
                aircraft.setModel("M2");
                aircraft.setTotalSeats(100);
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
                dep.setCode("D3");
                dep.setName("D3");
                dep.setCity("C1");
                dep.setCountry("C1");
                dep = airportRepository.save(dep);

                Airport arr = new Airport();
                arr.setCode("A3");
                arr.setName("A3");
                arr.setCity("C2");
                arr.setCountry("C2");
                arr = airportRepository.save(arr);

                PassengerAircraft aircraft = new PassengerAircraft();
                aircraft.setBrand("B");
                aircraft.setModel("M3");
                aircraft.setTotalSeats(100);
                aircraft = aircraftRepository.save(aircraft);

                FlightReqDTO req = new FlightReqDTO();
                req.setFlightCode("BS777");
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

                org.mockito.Mockito
                                .when(exchangeRateService.convert(org.mockito.ArgumentMatchers.any(),
                                                org.mockito.ArgumentMatchers.anyString(),
                                                org.mockito.ArgumentMatchers.anyString()))
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
                dep.setCode("DW");
                dep.setName("DW");
                dep.setCity("CW");
                dep.setCountry("CW");
                dep = airportRepository.save(dep);

                Airport arr = new Airport();
                arr.setCode("AW");
                arr.setName("AW");
                arr.setCity("CW2");
                arr.setCountry("CW2");
                arr = airportRepository.save(arr);

                PassengerAircraft aircraft = new PassengerAircraft();
                aircraft.setBrand("B");
                aircraft.setModel("M");
                aircraft.setTotalSeats(100);
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

                bluesky.airline.entities.WeatherData wd = new bluesky.airline.entities.WeatherData();
                wd.setTemperature(25.0);
                wd.setDescription("Sunny");

                bluesky.airline.dto.weather.WeatherRespDTO dto = new bluesky.airline.dto.weather.WeatherRespDTO();
                dto.setTemperature(25.0);
                dto.setDescription("Sunny");

                org.mockito.Mockito
                                .when(weatherService.refreshForFlight(org.mockito.ArgumentMatchers.any(),
                                                org.mockito.ArgumentMatchers.any()))
                                .thenReturn(wd);

                org.mockito.Mockito
                                .when(weatherService.toDTO(org.mockito.ArgumentMatchers.any()))
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
                dep.setCode("D_F");
                dep.setName("D_F");
                dep.setCity("C_F");
                dep.setCountry("C_F");
                dep = airportRepository.save(dep);

                Airport arr = new Airport();
                arr.setCode("A_F");
                arr.setName("A_F");
                arr.setCity("C_F");
                arr.setCountry("C_F");
                arr = airportRepository.save(arr);

                PassengerAircraft aircraft = new PassengerAircraft();
                aircraft.setBrand("B");
                aircraft.setModel("M");
                aircraft.setTotalSeats(100);
                aircraft = aircraftRepository.save(aircraft);

                FlightReqDTO req = new FlightReqDTO();
                req.setFlightCode("FORBIDDEN");
                req.setDepartureDate(Instant.now().plus(1, ChronoUnit.DAYS));
                req.setArrivalDate(Instant.now().plus(1, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));
                req.setBasePrice(new BigDecimal("100.00"));
                req.setStatus(FlightStatus.SCHEDULED);
                req.setDepartureAirportId(dep.getId());
                req.setArrivalAirportId(arr.getId());
                req.setAircraftId(aircraft.getId());
                req.setCompartmentCodes(List.of("ECONOMY"));

                mockMvc.perform(post("/flights")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isForbidden());
        }
}
