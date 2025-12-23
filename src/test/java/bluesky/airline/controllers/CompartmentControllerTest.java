package bluesky.airline.controllers;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import bluesky.airline.dto.compartment.CompartmentReqDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

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
                req.setCompartmentCode("PREMIUM PLUS");
                req.setDescription("Premium Plus Compartment");

                mockMvc.perform(post("/compartments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").exists())
                                .andExpect(jsonPath("$.compartmentCode").value("PREMIUM PLUS"));
        }

        // Test for updating and getting compartments by admin users
        @Test
        @WithMockUser(roles = "ADMIN")
        void testUpdateAndGetCompartment() throws Exception {
                CompartmentReqDTO req = new CompartmentReqDTO();
                req.setCompartmentCode("PREMIUM PLUS");
                req.setDescription("Premium Plus Compartment");

                String response = mockMvc.perform(post("/compartments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString();

                String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

                req.setDescription("Premium Plus Compartment (Updated)");
                mockMvc.perform(put("/compartments/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.description").value("Premium Plus Compartment (Updated)"));

                mockMvc.perform(get("/compartments/" + id))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(id));
        }

        // Test for deleting compartments by admin users
        @Test
        @WithMockUser(roles = "ADMIN")
        void testDeleteCompartment() throws Exception {
                CompartmentReqDTO req = new CompartmentReqDTO();
                req.setCompartmentCode("PREMIUM PLUS");
                req.setDescription("Premium Plus Compartment");

                String response = mockMvc.perform(post("/compartments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString();

                String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

                mockMvc.perform(delete("/compartments/" + id))
                                .andExpect(status().isNoContent());

                mockMvc.perform(get("/compartments/" + id))
                                .andExpect(status().isNotFound());
        }

        // Test for listing compartment codes by admin users
        @Test
        @WithMockUser(roles = "ADMIN")
        void testGetCompartmentCodes() throws Exception {
                mockMvc.perform(get("/compartments/codes"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray());
        }

        // Test for creating compartments by non-admin users
        @Test
        @WithMockUser(roles = "TOUR_OPERATOR")
        void testCreateCompartmentForbidden() throws Exception {
                CompartmentReqDTO req = new CompartmentReqDTO();
                req.setCompartmentCode("PREMIUM PLUS");
                req.setDescription("Premium Plus Compartment");

                mockMvc.perform(post("/compartments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isForbidden());
        }
}
