package com.ridelink.ride.messaging;

import com.ridelink.ride.dto.RideCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Publishes ride lifecycle events to RabbitMQ.
 */
@Component
public class RideEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(RideEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String completedRoutingKey;

    public RideEventPublisher(RabbitTemplate rabbitTemplate,
                               @Value("${rabbitmq.exchange}") String exchange,
                               @Value("${rabbitmq.routing-key.completed}") String completedRoutingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.completedRoutingKey = completedRoutingKey;
    }

    /**
     * Publish a ride.completed event for the fare-payment-service to consume.
     */
    public void publishRideCompleted(RideCompletedEvent event) {
        log.info("Publishing ride.completed event for rideId={}", event.getRideId());
        rabbitTemplate.convertAndSend(exchange, completedRoutingKey, event);
    }
}
