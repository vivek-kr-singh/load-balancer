package com.liftlab.loadbalancer.domain.strategy;

import com.liftlab.loadbalancer.domain.exception.LoadBalancerException;
import com.liftlab.loadbalancer.domain.model.BackendServer;

import java.util.List;

/**
 * Defines the contract for load balancing strategies.
 * This is a sealed interface permitting only approved implementations.
 */
public sealed interface LoadBalancingStrategy permits RoundRobinStrategy, RandomStrategy {

    /**
     * Selects a backend server from the list.
     *
     * @param servers list of available backend servers
     * @return the selected backend server
     * @throws LoadBalancerException if no server is available
     */
    BackendServer select(List<BackendServer> servers) throws LoadBalancerException;
}
