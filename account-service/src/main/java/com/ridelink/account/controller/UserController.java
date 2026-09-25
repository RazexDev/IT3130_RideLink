package com.ridelink.account.controller;

import com.ridelink.account.dto.ProfileUpdateRequest;
import com.ridelink.account.dto.UserProfileResponse;
import com.ridelink.account.security.JwtAuthenticationDetails;
import com.ridelink.account.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User profile management endpoints")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserProfileResponse> getCurrentUser(Authentication authentication) {
        JwtAuthenticationDetails details =
                (JwtAuthenticationDetails) authentication.getDetails();
        UserProfileResponse profile = userService.getUserById(details.getUserId());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            Authentication authentication,
            @RequestBody ProfileUpdateRequest request) {
        JwtAuthenticationDetails details =
                (JwtAuthenticationDetails) authentication.getDetails();
        UserProfileResponse profile = userService.updateProfile(details.getUserId(), request);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID (service-to-service)")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable String id) {
        UserProfileResponse profile = userService.getUserById(id);
        return ResponseEntity.ok(profile);
    }
}
