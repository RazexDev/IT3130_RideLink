package com.ridelink.ride.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${service.driver-url}")
    private String driverServiceUrl;

    @Value("${service.fare-url}")
    private String fareServiceUrl;

    @Bean
    public WebClient driverServiceWebClient() {
        return WebClient.builder()
                .baseUrl(driverServiceUrl)
                .build();
    }

    @Bean
    public WebClient fareServiceWebClient() {
        return WebClient.builder()
                .baseUrl(fareServiceUrl)
                .build();
    }
}
