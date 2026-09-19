package com.ridelink.ride.model;

/**
 * Ride lifecycle state machine.
 * 
 * Valid transitions:
 *   REQUESTED  → ASSIGNED   (system assigns a driver)
 *   REQUESTED  → CANCELLED  (passenger cancels before assignment)
 *   ASSIGNED   → ACCEPTED   (driver accepts the ride)
 *   ASSIGNED   → CANCELLED  (driver rejects / timeout)
 *   ACCEPTED   → IN_PROGRESS (driver starts the ride)
 *   IN_PROGRESS → COMPLETED  (driver finishes the ride)
 *   IN_PROGRESS → CANCELLED  (emergency cancel)
 */
public enum RideStatus {
    REQUESTED,
    ASSIGNED,
    ACCEPTED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
