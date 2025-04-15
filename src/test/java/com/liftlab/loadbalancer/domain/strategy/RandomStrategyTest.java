package com.liftlab.loadbalancer.domain.strategy;

import com.liftlab.loadbalancer.domain.exception.LoadBalancerException;
import com.liftlab.loadbalancer.domain.model.BackendServer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RandomStrategyTest {

    @Test
    void testSelectRandom() throws LoadBalancerException {
        RandomStrategy strategy = new RandomStrategy();
        List<BackendServer> servers = List.of(
                new BackendServer("http://backend1.liftlab.com"),
                new BackendServer("http://backend2.liftlab.com")
        );

        BackendServer selected = strategy.select(servers);
        assertNotNull(selected, "The selected backend should not be null.");
        assertTrue(servers.contains(selected), "The selected backend should be from the list.");
    }

    @Test
    void testSelectWithEmptyList() {
        RandomStrategy strategy = new RandomStrategy();
        Exception exception = assertThrows(LoadBalancerException.class, () -> strategy.select(List.of()));
        assertEquals("No backend servers available", exception.getMessage());
    }
}
