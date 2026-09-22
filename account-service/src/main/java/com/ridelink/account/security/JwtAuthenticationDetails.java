package com.ridelink.account.security;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * Custom authentication details that carry userId, email, and role
 * extracted from the JWT token for downstream controller access.
 */
public class JwtAuthenticationDetails {

    private final String userId;
    private final String email;
    private final String role;
    private final WebAuthenticationDetails webDetails;

    public JwtAuthenticationDetails(String userId, String email, String role,
                                     WebAuthenticationDetails webDetails) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.webDetails = webDetails;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public WebAuthenticationDetails getWebDetails() {
        return webDetails;
    }
}
