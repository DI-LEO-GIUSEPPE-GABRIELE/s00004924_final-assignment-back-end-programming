package bluesky.airline.controllers;

import bluesky.airline.dto.users.CreateUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import bluesky.airline.repositories.RoleRepository;
import bluesky.airline.entities.Role;

// Test class for UserController
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private RoleRepository roleRepository;

        // Test for creating and listing users by admin users
        @Test
        @WithMockUser(roles = "ADMIN")
        void testCreateAndListUser() throws Exception {
                if (roleRepository.findByNameIgnoreCase("ADMIN").isEmpty()) {
                        Role admin = new Role();
                        admin.setName("ADMIN");
                        admin.setRoleCode(0);
                        roleRepository.save(admin);
                }
                if (roleRepository.findByNameIgnoreCase("FLIGHT_MANAGER").isEmpty()) {
                        Role fm = new Role();
                        fm.setName("FLIGHT_MANAGER");
                        fm.setRoleCode(1);
                        roleRepository.save(fm);
                }
                if (roleRepository.findByNameIgnoreCase("TOUR_OPERATOR").isEmpty()) {
                        Role to = new Role();
                        to.setName("TOUR_OPERATOR");
                        to.setRoleCode(2);
                        roleRepository.save(to);
                }

                CreateUserRequest req = new CreateUserRequest();
                req.setName("Admin");
                req.setSurname("User");
                req.setUsername("adminuser");
                req.setEmail("adminuser@example.com");
                req.setPassword("password123");
                req.setRoleCode(0); // ADMIN

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
                req.setName("Tour");
                req.setSurname("Operator");
                req.setUsername("touroperator");
                req.setEmail("touroperator@example.com");
                req.setPassword("password123");
                req.setRoleCode(1); // TOUR_OPERATOR

                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isForbidden());
        }

        // Test for updating and getting users by admin users
        @Test
        @WithMockUser(roles = "ADMIN")
        void testUpdateAndGetUser() throws Exception {
                if (roleRepository.findByNameIgnoreCase("FLIGHT_MANAGER").isEmpty()) {
                        Role fm = new Role();
                        fm.setName("FLIGHT_MANAGER");
                        fm.setRoleCode(1);
                        roleRepository.save(fm);
                }
                if (roleRepository.findByNameIgnoreCase("TOUR_OPERATOR").isEmpty()) {
                        Role to = new Role();
                        to.setName("TOUR_OPERATOR");
                        to.setRoleCode(2);
                        roleRepository.save(to);
                }

                CreateUserRequest req = new CreateUserRequest();
                req.setName("Flight");
                req.setSurname("Manager");
                req.setUsername("flightmanager");
                req.setEmail("flightmanager@example.com");
                req.setPassword("password123");
                req.setRoleCode(1); // FLIGHT_MANAGER

                String response = mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString();

                String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

                bluesky.airline.dto.users.UpdateUserRequest updateReq = new bluesky.airline.dto.users.UpdateUserRequest();
                updateReq.setName("Flight (updated)");
                updateReq.setSurname("Manager (updated)");
                updateReq.setUsername("flightmanager");
                updateReq.setEmail("flightmanagerupdated@example.com");
                updateReq.setRoleCode(2); // TOUR_OPERATOR

                mockMvc.perform(put("/users/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateReq)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Flight (updated)"));

                mockMvc.perform(get("/users/" + id))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(id));
        }

        // Test for deleting users by admin users
        @Test
        @WithMockUser(roles = "ADMIN")
        void testDeleteUser() throws Exception {
                CreateUserRequest req = new CreateUserRequest();
                req.setName("Flight");
                req.setSurname("Manager");
                req.setUsername("flightmanager");
                req.setEmail("flightmanager@example.com");
                req.setPassword("password123");
                req.setRoleCode(2); // FLIGHT_MANAGER

                String response = mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getContentAsString();

                String id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

                mockMvc.perform(delete("/users/" + id))
                                .andExpect(status().isNoContent());
        }
}
