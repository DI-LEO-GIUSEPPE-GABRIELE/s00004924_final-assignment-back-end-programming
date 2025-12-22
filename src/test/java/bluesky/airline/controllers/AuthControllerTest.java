package bluesky.airline.controllers;

import bluesky.airline.dto.auth.AuthRegisterRequest;
import bluesky.airline.dto.auth.AuthLoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

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
                registerRequest.setName("Test");
                registerRequest.setSurname("User");
                registerRequest.setUsername("testuser_auth");
                registerRequest.setEmail("test_auth@example.com");
                registerRequest.setPassword("password123");
                registerRequest.setRoleCode(1); // USER

                mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").exists());

                AuthLoginRequest loginRequest = new AuthLoginRequest();
                loginRequest.setEmail("test_auth@example.com");
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
                loginRequest.setEmail("wrong@example.com");
                loginRequest.setPassword("wrongpass");

                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isUnauthorized());
        }

        // Test for registration failure (e.g. existing user)
        @Test
        void testRegisterFailure() throws Exception {
                AuthRegisterRequest registerRequest = new AuthRegisterRequest();
                registerRequest.setName("Duplicate");
                registerRequest.setSurname("User");
                registerRequest.setUsername("dup_user");
                registerRequest.setEmail("dup@example.com");
                registerRequest.setPassword("password123");
                registerRequest.setRoleCode(1);

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
