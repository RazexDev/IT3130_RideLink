package com.ridelink.ride.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class CreateRideRequest {

    private double pickupLatitude;
    private double pickupLongitude;
    private double dropoffLatitude;
    private double dropoffLongitude;

    @NotBlank(message = "Pickup address is required")
    private String pickupAddress;

    @NotBlank(message = "Dropoff address is required")
    private String dropoffAddress;

    @Positive(message = "Distance must be positive")
    private double distanceKm;

    private String vehicleType;

    public CreateRideRequest() {
    }

    public CreateRideRequest(double pickupLatitude, double pickupLongitude,
                              double dropoffLatitude, double dropoffLongitude,
                              String pickupAddress, String dropoffAddress,
                              double distanceKm, String vehicleType) {
        this.pickupLatitude = pickupLatitude;
        this.pickupLongitude = pickupLongitude;
        this.dropoffLatitude = dropoffLatitude;
        this.dropoffLongitude = dropoffLongitude;
        this.pickupAddress = pickupAddress;
        this.dropoffAddress = dropoffAddress;
        this.distanceKm = distanceKm;
        this.vehicleType = vehicleType;
    }

    public double getPickupLatitude() { return pickupLatitude; }
    public void setPickupLatitude(double pickupLatitude) { this.pickupLatitude = pickupLatitude; }

    public double getPickupLongitude() { return pickupLongitude; }
    public void setPickupLongitude(double pickupLongitude) { this.pickupLongitude = pickupLongitude; }

    public double getDropoffLatitude() { return dropoffLatitude; }
    public void setDropoffLatitude(double dropoffLatitude) { this.dropoffLatitude = dropoffLatitude; }

    public double getDropoffLongitude() { return dropoffLongitude; }
    public void setDropoffLongitude(double dropoffLongitude) { this.dropoffLongitude = dropoffLongitude; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public String getDropoffAddress() { return dropoffAddress; }
    public void setDropoffAddress(String dropoffAddress) { this.dropoffAddress = dropoffAddress; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
}
