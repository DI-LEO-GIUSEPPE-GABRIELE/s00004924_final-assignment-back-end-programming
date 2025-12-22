package bluesky.airline.controllers;

import bluesky.airline.dto.users.CreateUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

// Test class for UserController
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Test for creating and listing users by admin users
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateAndListUser() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setName("Admin");
        req.setSurname("User");
        req.setUsername("adminuser_test");
        req.setEmail("admin_test@example.com");
        req.setPassword("password");
        req.setRoleCode(2); // ADMIN

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // Test for creating users by non-admin users
    @Test
    @WithMockUser(roles = "TOUR_OPERATOR")
    void testCreateUserForbidden() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setName("Admin");
        req.setSurname("User");
        req.setUsername("adminuser_forbidden");
        req.setEmail("admin_forbidden@example.com");
        req.setPassword("password");
        req.setRoleCode(2);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateAndGetUser() throws Exception {
        // Create a user first
        CreateUserRequest req = new CreateUserRequest();
        req.setName("ToUpdate");
        req.setSurname("User");
        req.setUsername("update_user");
        req.setEmail("update@example.com");
        req.setPassword("password");
        req.setRoleCode(1); // USER

        String response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extract ID
        String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        // Update
        bluesky.airline.dto.users.UpdateUserRequest updateReq = new bluesky.airline.dto.users.UpdateUserRequest();
        updateReq.setName("UpdatedName");
        updateReq.setSurname("UpdatedSurname");
        updateReq.setUsername("updated_user");
        updateReq.setEmail("update@example.com"); // Email remains same or changed
        updateReq.setRoleCode(1); // USER

        mockMvc.perform(put("/users/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedName"));

        // Get Single
        mockMvc.perform(get("/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser() throws Exception {
        // Create a user
        CreateUserRequest req = new CreateUserRequest();
        req.setName("ToDelete");
        req.setSurname("User");
        req.setUsername("delete_user");
        req.setEmail("delete@example.com");
        req.setPassword("password");
        req.setRoleCode(1);

        String response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        // Delete
        mockMvc.perform(delete("/users/" + id))
                .andExpect(status().isNoContent());

        // Verify deleted (should be 404 or empty list when searching specifically, but
        // get by id might not be exposed or throw 404)
        // Since Get by ID is not explicitly in the controller snippet I saw earlier (it
        // only had list),
        // I should check if Get by ID exists in UserController.
        // Re-reading UserController.java snippet:
        // It has list (GET /users). It does NOT seem to have GET /users/{id} in the
        // snippet I saw (lines 1-42).
        // Let me verify if GET /users/{id} exists before adding the test.
    }
}
