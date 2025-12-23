package bluesky.airline.controllers;

import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import bluesky.airline.dto.auth.AuthLoginRequest;
import bluesky.airline.dto.auth.AuthRegisterRequest;
import org.junit.jupiter.api.Test;

// Test class for AuthController
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        // Test for user registration and login
        @Test
        void testRegisterAndLogin() throws Exception {
                AuthRegisterRequest registerRequest = new AuthRegisterRequest();
                registerRequest.setName("Admin");
                registerRequest.setSurname("User");
                registerRequest.setUsername("adminuser");
                registerRequest.setEmail("adminuser@example.com");
                registerRequest.setPassword("password123");
                registerRequest.setRoleCode(0); // ADMIN

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").exists());

                AuthLoginRequest loginRequest = new AuthLoginRequest();
                loginRequest.setEmail("adminuser@example.com");
                loginRequest.setPassword("password123");

                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").isNotEmpty());
        }

        // Test for login failure with wrong credentials
        @Test
        void testLoginFailure() throws Exception {
                AuthLoginRequest loginRequest = new AuthLoginRequest();
                loginRequest.setEmail("adminuser@example.com");
                loginRequest.setPassword("password12345");

                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isUnauthorized());
        }

        // Test for registration failure for existing user
        @Test
        void testRegisterFailure() throws Exception {
                AuthRegisterRequest registerRequest = new AuthRegisterRequest();
                registerRequest.setName("Tour");
                registerRequest.setSurname("Operator");
                registerRequest.setUsername("touroperator");
                registerRequest.setEmail("touroperator@example.com");
                registerRequest.setPassword("password123");
                registerRequest.setRoleCode(1); // TOUR_OPERATOR

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isCreated());

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isBadRequest());
        }
}
