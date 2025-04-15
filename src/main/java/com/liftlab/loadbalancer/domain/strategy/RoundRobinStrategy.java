package com.liftlab.loadbalancer.domain.strategy;

import com.liftlab.loadbalancer.domain.exception.LoadBalancerException;
import com.liftlab.loadbalancer.domain.model.BackendServer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Load balancing strategy that cycles through servers in a round-robin manner.
 */
public final class RoundRobinStrategy implements LoadBalancingStrategy {

    private final AtomicInteger index = new AtomicInteger(0);

    /**
     * Selects the next backend server in round-robin fashion.
     *
     * @param servers list of available backend servers
     * @return the selected backend server
     * @throws LoadBalancerException if the list is empty
     */
    @Override
    public BackendServer select(List<BackendServer> servers) throws LoadBalancerException {
        if (servers == null || servers.isEmpty()) {
            throw new LoadBalancerException("No backend servers available");
        }
        int currentIndex = Math.abs(index.getAndIncrement());
        return servers.get(currentIndex % servers.size());
    }
}
