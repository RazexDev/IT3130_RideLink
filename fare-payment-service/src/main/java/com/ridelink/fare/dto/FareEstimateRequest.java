package com.ridelink.fare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class FareEstimateRequest {

    @NotBlank(message = "Ride ID is required")
    private String rideId;

    @Positive(message = "Distance must be positive")
    private double distanceKm;

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;

    public FareEstimateRequest() {
    }

    public FareEstimateRequest(String rideId, double distanceKm, String vehicleType) {
        this.rideId = rideId;
        this.distanceKm = distanceKm;
        this.vehicleType = vehicleType;
    }

    public String getRideId() { return rideId; }
    public void setRideId(String rideId) { this.rideId = rideId; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
}
