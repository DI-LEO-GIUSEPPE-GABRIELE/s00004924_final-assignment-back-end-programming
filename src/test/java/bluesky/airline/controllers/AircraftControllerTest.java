package bluesky.airline.controllers;

import bluesky.airline.dto.aircraft.AircraftReqDTO;
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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AircraftControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateAndListAircraft() throws Exception {
        AircraftReqDTO req = new AircraftReqDTO();
        req.setBrand("Boeing");
        req.setModel("747");
        req.setType("PASSENGER");
        req.setTotalSeats(400);
        req.setMaxLoadCapacity(100000);

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

    @Test
    @WithMockUser(roles = "USER")
    void testCreateAircraftForbidden() throws Exception {
        AircraftReqDTO req = new AircraftReqDTO();
        req.setBrand("Boeing");
        req.setModel("747");
        req.setType("PASSENGER");
        req.setTotalSeats(400);
        req.setMaxLoadCapacity(100000);

        mockMvc.perform(post("/aircrafts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }
}
