package com.liftlab.loadbalancer.domain.exception;

/**
 * Custom exception for load balancer operations.
 */
public class LoadBalancerException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    public LoadBalancerException(String message) {
        super(message);
    }
}
