package com.liftlab.loadbalancer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Load Balancer application.
 * Starts the Spring Boot application and enables scheduling.
 */
@SpringBootApplication
@EnableScheduling // Enables scheduled tasks, for example health checks.
public class LoadBalancerApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoadBalancerApplication.class, args);
    }
}
