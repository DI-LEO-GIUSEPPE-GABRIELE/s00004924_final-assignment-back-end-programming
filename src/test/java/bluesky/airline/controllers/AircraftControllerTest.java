package bluesky.airline.controllers;

import bluesky.airline.dto.aircraft.AircraftReqDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

// Test class for AircraftController
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AircraftControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        // Test for creating and listing aircrafts
        @Test
        @WithMockUser(roles = "ADMIN")
        void testCreateAndListAircraft() throws Exception {
                AircraftReqDTO req = new AircraftReqDTO();
                req.setBrand("Boeing");
                req.setModel("747");
                req.setType("PASSENGER");
                req.setTotalSeats(150);
                req.setMaxLoadCapacity(10000);

                mockMvc.perform(post("/aircrafts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").exists())
                                .andExpect(jsonPath("$.brand").value("Boeing"));

                mockMvc.perform(get("/aircrafts"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray());
        }

        // Test for creating aircrafts by non-admin users
        @Test
        @WithMockUser(roles = "FLIGHT_MANAGER")
        void testCreateAircraftForbidden() throws Exception {
                AircraftReqDTO req = new AircraftReqDTO();
                req.setBrand("Boeing");
                req.setModel("747");
                req.setType("PASSENGER");
                req.setTotalSeats(150);
                req.setMaxLoadCapacity(10000);

                mockMvc.perform(post("/aircrafts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isForbidden());
        }

        // Test for updating and getting aircrafts
        @Test
        @WithMockUser(roles = "ADMIN")
        void testUpdateAndGetAircraft() throws Exception {
                AircraftReqDTO req = new AircraftReqDTO();
                req.setBrand("Airbus");
                req.setModel("A320");
                req.setType("PASSENGER");
                req.setTotalSeats(180);
                req.setMaxLoadCapacity(5000);

                String response = mockMvc.perform(post("/aircrafts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString();

                String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

                req.setModel("A320neo");
                mockMvc.perform(put("/aircrafts/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.model").value("A320neo"));

                mockMvc.perform(get("/aircrafts/" + id))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(id));
        }

        // Test for deleting aircrafts
        @Test
        @WithMockUser(roles = "ADMIN")
        void testDeleteAircraft() throws Exception {
                AircraftReqDTO req = new AircraftReqDTO();
                req.setBrand("Airbus");
                req.setModel("A320");
                req.setType("PASSENGER");
                req.setTotalSeats(180);

                String response = mockMvc.perform(post("/aircrafts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString();

                String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

                mockMvc.perform(delete("/aircrafts/" + id))
                                .andExpect(status().isNoContent());

                mockMvc.perform(get("/aircrafts/" + id))
                                .andExpect(status().isNotFound());
        }

        // Test for getting all aircraft types
        @Test
        @WithMockUser(roles = "ADMIN")
        void testGetAircraftTypes() throws Exception {
                mockMvc.perform(get("/aircrafts/types"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].value").exists());
        }

        // Test for creating cargo aircrafts
        @Test
        @WithMockUser(roles = "ADMIN")
        void testCreateCargoAircraft() throws Exception {
                AircraftReqDTO req = new AircraftReqDTO();
                req.setBrand("Antonov");
                req.setModel("An-124");
                req.setType("CARGO");
                req.setMaxLoadCapacity(150000);

                mockMvc.perform(post("/aircrafts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.maxLoadCapacity").value(150000));
        }
}
