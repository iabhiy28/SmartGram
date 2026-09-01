package com.gramconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * GramConnect - Digital Help & Service Platform for Indian Villages.
 *
 * Core Spring Boot Application Entrypoint.
 *
 * Annotations:
 * - @SpringBootApplication: Autoconfiguration, Component Scan, Configuration
 * - @EnableJpaAuditing: Automatically populates @CreatedDate and @LastModifiedDate
 * - @EnableScheduling: Powers background SLA checking, overdue complaint escalations, token pruning
 * - @EnableCaching: Enables Spring Cache abstraction backed by Redis
 */
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableCaching
@EnableJpaRepositories(basePackages = "com.gramconnect")
@EntityScan(basePackages = "com.gramconnect")
public class GramConnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(GramConnectApplication.class, args);
    }
}
