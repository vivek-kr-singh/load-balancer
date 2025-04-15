package com.liftlab.loadbalancer.domain.model;

/**
 * Represents a backend server.
 *
 * <p>Implemented as a record to ensure immutability.</p>
 */
public record BackendServer(String url) { }
