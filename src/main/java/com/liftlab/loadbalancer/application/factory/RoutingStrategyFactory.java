package com.liftlab.loadbalancer.application.factory;

import com.liftlab.loadbalancer.domain.strategy.LoadBalancingStrategy;

/**
 * Factory to provide the current load balancing strategy.
 */
public class RoutingStrategyFactory {

    private final LoadBalancingStrategy defaultStrategy;

    /**
     * Constructor.
     *
     * @param defaultStrategy the active load balancing strategy
     */
    public RoutingStrategyFactory(LoadBalancingStrategy defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
    }

    /**
     * Returns the active load balancing strategy.
     *
     * @return the load balancing strategy
     */
    public LoadBalancingStrategy getStrategy() {
        return defaultStrategy;
    }
}
