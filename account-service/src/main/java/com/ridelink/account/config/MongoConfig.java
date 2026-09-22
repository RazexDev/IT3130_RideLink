package com.ridelink.account.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Enables MongoDB auditing support for @CreatedDate and @LastModifiedDate annotations.
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
}
