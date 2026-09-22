# RideLink

RideLink is a backend microservices-based ride-sharing platform developed with Java 17, Spring Boot 3, MongoDB, and RabbitMQ, implementing the Database-per-Service pattern.

## Microservices Architecture

- **account-service**: User authentication, registration, JWT issuance, and profile management.
- **driver-vehicle-service**: Driver profile management, vehicle details, and real-time geospatial location tracking.
- **ride-management-service**: Ride booking lifecycle, state machine transitions, driver matching, and messaging.
- **fare-payment-service**: Fare calculation and payment transaction processing.

## Running the Platform

Ensure Docker is installed and running, then execute:

```bash
docker-compose up -d
```
