package bluesky.airline.controllers;

import bluesky.airline.dto.airport.AirportReqDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

// Test class for AirportController
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AirportControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        // Test for creating and listing airports
        @Test
        @WithMockUser(roles = "ADMIN")
        void testCreateAndListAirport() throws Exception {
                AirportReqDTO req = new AirportReqDTO();
                req.setCode("JFK");
                req.setName("John F. Kennedy International Airport");
                req.setCity("New York");
                req.setCountry("USA");

                mockMvc.perform(post("/airports")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").exists())
                                .andExpect(jsonPath("$.code").value("JFK"));

                mockMvc.perform(get("/airports"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray());
        }

        // Test for creating airports by non-admin users
        @Test
        @WithMockUser(roles = "TOUR_OPERATOR")
        void testCreateAirportForbiddenForTourOperator() throws Exception {
                AirportReqDTO req = new AirportReqDTO();
                req.setCode("LHR");
                req.setName("Heathrow Airport");
                req.setCity("London");
                req.setCountry("UK");

                mockMvc.perform(post("/airports")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isForbidden());
        }

        // Test for updating and getting airports
        @Test
        @WithMockUser(roles = "ADMIN")
        void testUpdateAndGetAirport() throws Exception {
                AirportReqDTO req = new AirportReqDTO();
                req.setCode("MUC");
                req.setName("Munich Airport");
                req.setCity("Munich");
                req.setCountry("Germany");

                String response = mockMvc.perform(post("/airports")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString();

                String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

                req.setName("Munich Franz Josef Strauss");
                mockMvc.perform(put("/airports/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Munich Franz Josef Strauss"));

                mockMvc.perform(get("/airports/" + id))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(id));
        }

        // Test for deleting airports
        @Test
        @WithMockUser(roles = "ADMIN")
        void testDeleteAirport() throws Exception {
                AirportReqDTO req = new AirportReqDTO();
                req.setCode("TXL");
                req.setName("Tegel");
                req.setCity("Berlin");
                req.setCountry("Germany");

                String response = mockMvc.perform(post("/airports")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString();

                String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

                mockMvc.perform(delete("/airports/" + id))
                                .andExpect(status().isNoContent());

                mockMvc.perform(get("/airports/" + id))
                                .andExpect(status().isNotFound());
        }
}
