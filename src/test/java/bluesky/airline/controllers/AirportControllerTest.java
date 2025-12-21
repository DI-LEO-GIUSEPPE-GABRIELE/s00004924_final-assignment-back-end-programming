package bluesky.airline.controllers;

import bluesky.airline.dto.airport.AirportReqDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
}
