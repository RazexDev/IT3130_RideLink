package com.ridelink.fare.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "fare_estimates")
public class FareEstimate {

    @Id
    private String id;

    private String rideId;

    private double baseFare;

    private double perKmRate;

    private double distanceKm;

    private double estimatedTotal;

    private String currency = "LKR";

    @CreatedDate
    private Instant createdAt;

    public FareEstimate() {
    }

    public FareEstimate(String rideId, double baseFare, double perKmRate,
                        double distanceKm, double estimatedTotal) {
        this.rideId = rideId;
        this.baseFare = baseFare;
        this.perKmRate = perKmRate;
        this.distanceKm = distanceKm;
        this.estimatedTotal = estimatedTotal;
    }

    // Getters and Setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRideId() { return rideId; }
    public void setRideId(String rideId) { this.rideId = rideId; }

    public double getBaseFare() { return baseFare; }
    public void setBaseFare(double baseFare) { this.baseFare = baseFare; }

    public double getPerKmRate() { return perKmRate; }
    public void setPerKmRate(double perKmRate) { this.perKmRate = perKmRate; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public double getEstimatedTotal() { return estimatedTotal; }
    public void setEstimatedTotal(double estimatedTotal) { this.estimatedTotal = estimatedTotal; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
