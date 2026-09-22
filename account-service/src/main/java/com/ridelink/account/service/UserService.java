package com.ridelink.account.service;

import com.ridelink.account.dto.ProfileUpdateRequest;
import com.ridelink.account.dto.UserProfileResponse;
import com.ridelink.account.model.User;
import com.ridelink.account.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * User profile management service.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get a user profile by ID.
     */
    public UserProfileResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toProfileResponse(user);
    }

    /**
     * Get a user profile by email.
     */
    public UserProfileResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toProfileResponse(user);
    }

    /**
     * Update a user's profile fields (firstName, lastName, phoneNumber).
     */
    public UserProfileResponse updateProfile(String userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        user = userRepository.save(user);
        return toProfileResponse(user);
    }

    private UserProfileResponse toProfileResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getRole().name()
        );
    }
}
