package com.ridelink.ride.security;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

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

    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public WebAuthenticationDetails getWebDetails() { return webDetails; }
}
