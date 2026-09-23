package com.ridelink.ride.controller;

import com.ridelink.ride.dto.AssignDriverRequest;
import com.ridelink.ride.dto.CreateRideRequest;
import com.ridelink.ride.model.Ride;
import com.ridelink.ride.security.JwtAuthenticationDetails;
import com.ridelink.ride.service.RideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
@Tag(name = "Ride Management", description = "Ride lifecycle management endpoints")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PASSENGER')")
    @Operation(summary = "Request a new ride")
    public ResponseEntity<Ride> requestRide(
            Authentication authentication,
            @Valid @RequestBody CreateRideRequest request) {
        String passengerId = getDetails(authentication).getUserId();
        Ride ride = rideService.createRide(passengerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ride);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ride details")
    public ResponseEntity<Ride> getRideById(@PathVariable String id) {
        Ride ride = rideService.getRideById(id);
        return ResponseEntity.ok(ride);
    }

    @GetMapping("/my-rides")
    @Operation(summary = "Get rides for the current user")
    public ResponseEntity<List<Ride>> getMyRides(Authentication authentication) {
        String userId = getDetails(authentication).getUserId();
        List<Ride> rides = rideService.getRidesForUser(userId);
        return ResponseEntity.ok(rides);
    }

    @PutMapping("/{id}/assign")
    @Operation(summary = "Assign a driver to a ride (Internal/System)")
    public ResponseEntity<Ride> assignDriver(
            @PathVariable String id,
            @Valid @RequestBody AssignDriverRequest request) {
        Ride ride = rideService.assignDriver(id, request);
        return ResponseEntity.ok(ride);
    }

    @PutMapping("/{id}/accept")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Driver accepts an assigned ride")
    public ResponseEntity<Ride> acceptRide(
            Authentication authentication,
            @PathVariable String id) {
        String driverId = getDetails(authentication).getUserId();
        Ride ride = rideService.acceptRide(id, driverId);
        return ResponseEntity.ok(ride);
    }

    @PutMapping("/{id}/start")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Driver starts the ride")
    public ResponseEntity<Ride> startRide(
            Authentication authentication,
            @PathVariable String id) {
        String driverId = getDetails(authentication).getUserId();
        Ride ride = rideService.startRide(id, driverId);
        return ResponseEntity.ok(ride);
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Driver completes the ride")
    public ResponseEntity<Ride> completeRide(
            Authentication authentication,
            @PathVariable String id) {
        String driverId = getDetails(authentication).getUserId();
        Ride ride = rideService.completeRide(id, driverId);
        return ResponseEntity.ok(ride);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel a ride")
    public ResponseEntity<Ride> cancelRide(
            Authentication authentication,
            @PathVariable String id) {
        String userId = getDetails(authentication).getUserId();
        Ride ride = rideService.cancelRide(id, userId);
        return ResponseEntity.ok(ride);
    }

    private JwtAuthenticationDetails getDetails(Authentication authentication) {
        return (JwtAuthenticationDetails) authentication.getDetails();
    }
}
