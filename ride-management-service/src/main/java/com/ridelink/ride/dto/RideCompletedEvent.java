package com.ridelink.ride.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * Event published to RabbitMQ when a ride is completed.
 * Consumed by fare-payment-service.
 */
public class RideCompletedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String rideId;
    private String passengerId;
    private String driverId;
    private double distanceKm;
    private String pickupAddress;
    private String dropoffAddress;
    private String vehicleType;
    private Instant completedAt;

    public RideCompletedEvent() {
    }

    public RideCompletedEvent(String rideId, String passengerId, String driverId,
                               double distanceKm, String pickupAddress, String dropoffAddress,
                               String vehicleType, Instant completedAt) {
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.distanceKm = distanceKm;
        this.pickupAddress = pickupAddress;
        this.dropoffAddress = dropoffAddress;
        this.vehicleType = vehicleType;
        this.completedAt = completedAt;
    }

    public String getRideId() { return rideId; }
    public void setRideId(String rideId) { this.rideId = rideId; }

    public String getPassengerId() { return passengerId; }
    public void setPassengerId(String passengerId) { this.passengerId = passengerId; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public String getDropoffAddress() { return dropoffAddress; }
    public void setDropoffAddress(String dropoffAddress) { this.dropoffAddress = dropoffAddress; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}
