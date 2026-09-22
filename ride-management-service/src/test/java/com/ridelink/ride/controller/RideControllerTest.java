package com.ridelink.ride.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridelink.ride.dto.CreateRideRequest;
import com.ridelink.ride.model.Ride;
import com.ridelink.ride.model.RideStatus;
import com.ridelink.ride.security.JwtUtil;
import com.ridelink.ride.service.RideService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RideService rideService;

    private String generateToken(String email, String role, String userId) {
        return io.jsonwebtoken.Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("userId", userId)
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date(System.currentTimeMillis() + 86400000))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                        "RideLinkSuperSecretKeyForJWTTokenGeneration2024MustBeLongEnough"
                                .getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                .compact();
    }

    @Test
    @DisplayName("POST /api/rides — Passenger can request a ride")
    void requestRide_passenger_returnsCreated() throws Exception {
        CreateRideRequest request = new CreateRideRequest(
                6.9271, 79.8612, 6.8649, 79.8997,
                "Colombo Fort", "Nugegoda", 10.5, "SEDAN"
        );

        Ride ride = new Ride();
        ride.setId("ride-123");
        ride.setPassengerId("passenger-123");
        ride.setStatus(RideStatus.REQUESTED);
        ride.setVehicleType("SEDAN");

        when(rideService.createRide(eq("passenger-123"), any(CreateRideRequest.class))).thenReturn(ride);

        mockMvc.perform(post("/api/rides")
                        .header("Authorization", "Bearer " + generateToken("pass@example.com", "PASSENGER", "passenger-123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("REQUESTED"));
    }

    @Test
    @DisplayName("PUT /api/rides/{id}/complete — Driver can complete a ride")
    void completeRide_driver_returnsOk() throws Exception {
        Ride ride = new Ride();
        ride.setId("ride-123");
        ride.setDriverId("driver-456");
        ride.setStatus(RideStatus.COMPLETED);

        when(rideService.completeRide("ride-123", "driver-456")).thenReturn(ride);

        mockMvc.perform(put("/api/rides/ride-123/complete")
                        .header("Authorization", "Bearer " + generateToken("driver@example.com", "DRIVER", "driver-456")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("PUT /api/rides/{id}/complete — State transition exception returns 409 Conflict")
    void completeRide_invalidState_returnsConflict() throws Exception {
        when(rideService.completeRide("ride-123", "driver-456"))
                .thenThrow(new IllegalStateException("Invalid transition from REQUESTED to COMPLETED"));

        mockMvc.perform(put("/api/rides/ride-123/complete")
                        .header("Authorization", "Bearer " + generateToken("driver@example.com", "DRIVER", "driver-456")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Invalid transition from REQUESTED to COMPLETED"));
    }
}
