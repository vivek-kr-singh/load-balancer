package com.liftlab.loadbalancer.adapters.out.health;

import com.liftlab.loadbalancer.adapters.out.repository.MapBasedBackendConfigRepository;
import com.liftlab.loadbalancer.domain.exception.LoadBalancerException;
import com.liftlab.loadbalancer.domain.model.BackendServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for HealthCheckAdapter using MapBasedBackendConfigRepository.
 * This test registers a backend server with an unreachable URL and verifies that
 * the health check marks it as unhealthy. Additionally, it tests the exception handling
 * in the repository when invalid backends are registered.
 */
class HealthCheckAdapterTest {

    private MapBasedBackendConfigRepository backendRepository;
    private HealthCheckAdapter healthCheckAdapter;

    @BeforeEach
    void setup() throws LoadBalancerException {
        backendRepository = new MapBasedBackendConfigRepository();
        // Register a backend server on a port that is unlikely to have any service running.
        backendRepository.registerBackend(new BackendServer("http://localhost:9999"));
        healthCheckAdapter = new HealthCheckAdapter(backendRepository);
    }

    @Test
    void testUnhealthyServerIsMarked() throws InterruptedException {
        // Run the health check.
        healthCheckAdapter.checkHealth();
        // Sleep briefly if needed (here direct call should suffice, so sleep is optional).
        Thread.sleep(2000);
        // Assert that the active server list is now empty.
        assertThat(backendRepository.getActiveServers())
                .as("Active servers list should be empty after failed health check")
                .isEmpty();
        // And that the unreachable server is in the inactive list.
        assertThat(backendRepository.getInactiveServers())
                .as("The unreachable server should be marked inactive")
                .contains(new BackendServer("http://localhost:9999"));
    }

    @Test
    void testRegisterInvalidBackendThrowsException() {
        // Attempt to register an invalid backend (null URL) and expect a LoadBalancerException.
        Exception exception = assertThrows(LoadBalancerException.class, () ->
                backendRepository.registerBackend(new BackendServer(null))
        );
        assertThat(exception.getMessage()).contains("Invalid backend server provided");
    }
}
