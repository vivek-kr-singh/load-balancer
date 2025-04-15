package com.liftlab.loadbalancer.domain.strategy;

import com.liftlab.loadbalancer.domain.exception.LoadBalancerException;
import com.liftlab.loadbalancer.domain.model.BackendServer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoundRobinStrategyTest {

    @Test
    void testSelectRoundRobin() throws LoadBalancerException {
        RoundRobinStrategy strategy = new RoundRobinStrategy();
        List<BackendServer> servers = List.of(
                new BackendServer("http://backend1.liftlab.com"),
                new BackendServer("http://backend2.liftlab.com")
        );
        BackendServer first = strategy.select(servers);
        BackendServer second = strategy.select(servers);
        BackendServer third = strategy.select(servers);

        // When there are two servers, the round robin should alternate.
        assertNotEquals(first, second);
        assertEquals(first, third, "Should cycle back to the first server.");
    }

    @Test
    void testSelectWithEmptyList() {
        RoundRobinStrategy strategy = new RoundRobinStrategy();
        Exception exception = assertThrows(LoadBalancerException.class, () -> strategy.select(List.of()));
        assertEquals("No backend servers available", exception.getMessage());
    }
}
