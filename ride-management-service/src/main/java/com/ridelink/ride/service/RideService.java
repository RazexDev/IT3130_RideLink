package com.ridelink.ride.service;

import com.ridelink.ride.dto.AssignDriverRequest;
import com.ridelink.ride.dto.CreateRideRequest;
import com.ridelink.ride.dto.RideCompletedEvent;
import com.ridelink.ride.messaging.RideEventPublisher;
import com.ridelink.ride.model.Ride;
import com.ridelink.ride.model.RideStatus;
import com.ridelink.ride.repository.RideRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Core ride lifecycle management with strict state machine enforcement.
 *
 * State transitions:
 *   REQUESTED  → ASSIGNED, CANCELLED
 *   ASSIGNED   → ACCEPTED, CANCELLED
 *   ACCEPTED   → IN_PROGRESS
 *   IN_PROGRESS → COMPLETED, CANCELLED
 */
@Service
public class RideService {

    private static final Logger log = LoggerFactory.getLogger(RideService.class);

    private final RideRepository rideRepository;
    private final RideEventPublisher rideEventPublisher;

    /** Valid state transitions map: from-status → set of allowed to-statuses */
    private static final Map<RideStatus, Set<RideStatus>> VALID_TRANSITIONS = Map.of(
            RideStatus.REQUESTED, Set.of(RideStatus.ASSIGNED, RideStatus.CANCELLED),
            RideStatus.ASSIGNED, Set.of(RideStatus.ACCEPTED, RideStatus.CANCELLED),
            RideStatus.ACCEPTED, Set.of(RideStatus.IN_PROGRESS),
            RideStatus.IN_PROGRESS, Set.of(RideStatus.COMPLETED, RideStatus.CANCELLED)
    );

    public RideService(RideRepository rideRepository,
                       RideEventPublisher rideEventPublisher) {
        this.rideRepository = rideRepository;
        this.rideEventPublisher = rideEventPublisher;
    }

    /**
     * Create a new ride request from a passenger.
     */
    public Ride createRide(String passengerId, CreateRideRequest request) {
        Ride ride = new Ride();
        ride.setPassengerId(passengerId);
        ride.setPickupLatitude(request.getPickupLatitude());
        ride.setPickupLongitude(request.getPickupLongitude());
        ride.setDropoffLatitude(request.getDropoffLatitude());
        ride.setDropoffLongitude(request.getDropoffLongitude());
        ride.setPickupAddress(request.getPickupAddress());
        ride.setDropoffAddress(request.getDropoffAddress());
        ride.setDistanceKm(request.getDistanceKm());
        ride.setVehicleType(request.getVehicleType() != null ? request.getVehicleType() : "SEDAN");
        ride.setStatus(RideStatus.REQUESTED);

        ride = rideRepository.save(ride);
        log.info("Ride created: id={}, passengerId={}", ride.getId(), passengerId);
        return ride;
    }

    /**
     * Get ride by ID.
     */
    public Ride getRideById(String rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found"));
    }

    /**
     * Get rides for a user (passenger or driver).
     */
    public List<Ride> getRidesForUser(String userId) {
        return rideRepository.findByPassengerIdOrDriverId(userId, userId);
    }

    /**
     * Assign a driver to a ride.
     * Transition: REQUESTED → ASSIGNED
     */
    public Ride assignDriver(String rideId, AssignDriverRequest request) {
        Ride ride = getRideById(rideId);
        validateTransition(ride.getStatus(), RideStatus.ASSIGNED);

        ride.setDriverId(request.getDriverId());
        ride.setStatus(RideStatus.ASSIGNED);
        ride.setAssignedAt(Instant.now());

        ride = rideRepository.save(ride);
        log.info("Driver assigned: rideId={}, driverId={}", rideId, request.getDriverId());
        return ride;
    }

    /**
     * Driver accepts an assigned ride.
     * Transition: ASSIGNED → ACCEPTED
     */
    public Ride acceptRide(String rideId, String driverId) {
        Ride ride = getRideById(rideId);
        validateDriverOwnership(ride, driverId);
        validateTransition(ride.getStatus(), RideStatus.ACCEPTED);

        ride.setStatus(RideStatus.ACCEPTED);
        ride.setAcceptedAt(Instant.now());

        ride = rideRepository.save(ride);
        log.info("Ride accepted: rideId={}, driverId={}", rideId, driverId);
        return ride;
    }

    /**
     * Driver starts the ride.
     * Transition: ACCEPTED → IN_PROGRESS
     */
    public Ride startRide(String rideId, String driverId) {
        Ride ride = getRideById(rideId);
        validateDriverOwnership(ride, driverId);
        validateTransition(ride.getStatus(), RideStatus.IN_PROGRESS);

        ride.setStatus(RideStatus.IN_PROGRESS);
        ride.setStartedAt(Instant.now());

        ride = rideRepository.save(ride);
        log.info("Ride started: rideId={}", rideId);
        return ride;
    }

    /**
     * Driver completes the ride.
     * Transition: IN_PROGRESS → COMPLETED
     * Publishes ride.completed event to RabbitMQ.
     */
    public Ride completeRide(String rideId, String driverId) {
        Ride ride = getRideById(rideId);
        validateDriverOwnership(ride, driverId);
        validateTransition(ride.getStatus(), RideStatus.COMPLETED);

        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(Instant.now());

        ride = rideRepository.save(ride);
        log.info("Ride completed: rideId={}", rideId);

        // Publish ride.completed event
        RideCompletedEvent event = new RideCompletedEvent(
                ride.getId(),
                ride.getPassengerId(),
                ride.getDriverId(),
                ride.getDistanceKm(),
                ride.getPickupAddress(),
                ride.getDropoffAddress(),
                ride.getVehicleType(),
                ride.getCompletedAt()
        );
        rideEventPublisher.publishRideCompleted(event);

        return ride;
    }

    /**
     * Cancel a ride.
     * Valid from: REQUESTED, ASSIGNED, IN_PROGRESS
     */
    public Ride cancelRide(String rideId, String userId) {
        Ride ride = getRideById(rideId);

        // Allow either passenger or assigned driver to cancel
        if (!ride.getPassengerId().equals(userId) &&
                (ride.getDriverId() == null || !ride.getDriverId().equals(userId))) {
            throw new IllegalArgumentException("Only the ride passenger or assigned driver can cancel");
        }

        validateTransition(ride.getStatus(), RideStatus.CANCELLED);

        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancelledAt(Instant.now());

        ride = rideRepository.save(ride);
        log.info("Ride cancelled: rideId={}, cancelledBy={}", rideId, userId);
        return ride;
    }

    /**
     * Enforce state machine: only valid transitions are allowed.
     */
    private void validateTransition(RideStatus currentStatus, RideStatus targetStatus) {
        Set<RideStatus> allowed = VALID_TRANSITIONS.get(currentStatus);
        if (allowed == null || !allowed.contains(targetStatus)) {
            throw new IllegalStateException(
                    String.format("Invalid transition from %s to %s", currentStatus, targetStatus));
        }
    }

    /**
     * Verify the requesting driver is the one assigned to the ride.
     */
    private void validateDriverOwnership(Ride ride, String driverId) {
        if (ride.getDriverId() == null || !ride.getDriverId().equals(driverId)) {
            throw new IllegalArgumentException("Driver is not assigned to this ride");
        }
    }
}
