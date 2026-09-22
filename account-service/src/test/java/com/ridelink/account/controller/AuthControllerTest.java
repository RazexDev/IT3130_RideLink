package com.ridelink.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridelink.account.dto.AuthResponse;
import com.ridelink.account.dto.LoginRequest;
import com.ridelink.account.dto.RegisterRequest;
import com.ridelink.account.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("POST /api/auth/register — should register a new user and return 201")
    void register_validRequest_returnsCreated() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "john@example.com", "password123", "John", "Doe", "+94771234567", "PASSENGER"
        );

        AuthResponse response = new AuthResponse(
                "jwt-token-here", "john@example.com", "PASSENGER", "user-id-123"
        );

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token-here"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("PASSENGER"))
                .andExpect(jsonPath("$.userId").value("user-id-123"));
    }

    @Test
    @DisplayName("POST /api/auth/register — duplicate email returns 400")
    void register_duplicateEmail_returnsBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "john@example.com", "password123", "John", "Doe", "+94771234567", "PASSENGER"
        );

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Email is already registered"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email is already registered"));
    }

    @Test
    @DisplayName("POST /api/auth/register — invalid email returns 400")
    void register_invalidEmail_returnsBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "not-an-email", "password123", "John", "Doe", "+94771234567", "PASSENGER"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login — valid credentials return 200 with token")
    void login_validCredentials_returnsOk() throws Exception {
        LoginRequest request = new LoginRequest("john@example.com", "password123");

        AuthResponse response = new AuthResponse(
                "jwt-token-here", "john@example.com", "PASSENGER", "user-id-123"
        );

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-here"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @DisplayName("POST /api/auth/login — wrong password returns 400")
    void login_wrongPassword_returnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest("john@example.com", "wrongpassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid email or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }
}
