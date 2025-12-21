package bluesky.airline.controllers;

import bluesky.airline.dto.compartment.CompartmentReqDTO;
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

// Test class for CompartmentController
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CompartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Test for listing compartments by non-admin users
    @Test
    @WithMockUser(roles = "TOUR_OPERATOR")
    void testListCompartments() throws Exception {
        mockMvc.perform(get("/compartments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // Test for creating compartments by admin users
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateCompartment() throws Exception {
        CompartmentReqDTO req = new CompartmentReqDTO();
        req.setCompartmentCode("NEW_COMPARTMENT");
        req.setDescription("New Compartment Description");

        mockMvc.perform(post("/compartments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.compartmentCode").value("NEW_COMPARTMENT"));
    }
}
