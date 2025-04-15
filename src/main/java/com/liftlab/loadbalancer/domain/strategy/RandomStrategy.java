package com.liftlab.loadbalancer.domain.strategy;

import com.liftlab.loadbalancer.domain.exception.LoadBalancerException;
import com.liftlab.loadbalancer.domain.model.BackendServer;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Load balancing strategy that randomly selects a backend server.
 */
public final class RandomStrategy implements LoadBalancingStrategy {

    /**
     * Randomly selects a backend server from the list.
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
        int randomIndex = ThreadLocalRandom.current().nextInt(servers.size());
        return servers.get(randomIndex);
    }
}
