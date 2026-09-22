package com.ridelink.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridelink.account.dto.ProfileUpdateRequest;
import com.ridelink.account.dto.UserProfileResponse;
import com.ridelink.account.security.JwtUtil;
import com.ridelink.account.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @MockBean
    private UserService userService;

    private String generateTestToken() {
        return jwtUtil.generateToken("test@example.com", "PASSENGER", "test-user-id");
    }

    @Test
    @DisplayName("GET /api/users/me — authenticated user gets their profile")
    void getCurrentUser_authenticated_returnsProfile() throws Exception {
        UserProfileResponse profile = new UserProfileResponse(
                "test-user-id", "test@example.com", "Test", "User", "+94771234567", "PASSENGER"
        );

        when(userService.getUserById("test-user-id")).thenReturn(profile);

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + generateTestToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"));
    }

    @Test
    @DisplayName("GET /api/users/me — unauthenticated returns 401")
    void getCurrentUser_unauthenticated_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /api/users/me — authenticated user updates their profile")
    void updateProfile_authenticated_returnsUpdatedProfile() throws Exception {
        ProfileUpdateRequest request = new ProfileUpdateRequest("Updated", "Name", "+94779999999");

        UserProfileResponse updatedProfile = new UserProfileResponse(
                "test-user-id", "test@example.com", "Updated", "Name", "+94779999999", "PASSENGER"
        );

        when(userService.updateProfile(eq("test-user-id"), any(ProfileUpdateRequest.class)))
                .thenReturn(updatedProfile);

        mockMvc.perform(put("/api/users/me")
                        .header("Authorization", "Bearer " + generateTestToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.phoneNumber").value("+94779999999"));
    }

    @Test
    @DisplayName("GET /api/users/{id} — service-to-service lookup returns profile")
    void getUserById_returnsProfile() throws Exception {
        UserProfileResponse profile = new UserProfileResponse(
                "some-user-id", "other@example.com", "Other", "User", "+94771111111", "DRIVER"
        );

        when(userService.getUserById("some-user-id")).thenReturn(profile);

        mockMvc.perform(get("/api/users/some-user-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("some-user-id"))
                .andExpect(jsonPath("$.role").value("DRIVER"));
    }
}
