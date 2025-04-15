package com.liftlab.loadbalancer.port.in;

/**
 * Inbound port interface for handling client requests.
 */
public interface LoadBalancerPort {

    /**
     * Handles an incoming client request.
     *
     * @param request the client request payload
     * @return a response message
     */
    String handleRequest(String request);
}
