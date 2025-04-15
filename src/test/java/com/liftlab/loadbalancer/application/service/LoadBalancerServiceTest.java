package com.liftlab.loadbalancer.application.service;

import com.liftlab.loadbalancer.adapters.out.repository.MapBasedBackendConfigRepository;
import com.liftlab.loadbalancer.application.factory.RoutingStrategyFactory;
import com.liftlab.loadbalancer.domain.exception.LoadBalancerException;
import com.liftlab.loadbalancer.domain.model.BackendServer;
import com.liftlab.loadbalancer.domain.strategy.RoundRobinStrategy;
import com.liftlab.loadbalancer.port.out.MonitoringPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoadBalancerServiceTest {

    private RoutingStrategyFactory routingStrategyFactory;
    private MonitoringPort monitoringPort;
    private MapBasedBackendConfigRepository backendRepository;
    private LoadBalancerService loadBalancerService;

    @BeforeEach
    void setup() throws LoadBalancerException {
        // Use RoundRobinStrategy.
        routingStrategyFactory = new RoutingStrategyFactory(new RoundRobinStrategy());
        // Mock the MonitoringPort.
        monitoringPort = mock(MonitoringPort.class);
        // Use the new MapBasedBackendConfigRepository.
        backendRepository = new MapBasedBackendConfigRepository();
        // Clear any existing backend entries and register a fixed backend "http://localhost:8081".
        // (If defaults exist, remove them for predictable behavior.)
        backendRepository.removeBackend(new BackendServer("http://localhost:8081"));
        backendRepository.removeBackend(new BackendServer("http://localhost:8082"));
        backendRepository.registerBackend(new BackendServer("http://localhost:8081"));
        loadBalancerService = new LoadBalancerService(routingStrategyFactory, monitoringPort, backendRepository);
    }

    @Test
    void testForwardRequestSuccess() {
        String request = "Test Request";
        String response = loadBalancerService.forwardRequest(request);

        // We registered http://localhost:8081, so we expect this URL in the response.
        assertTrue(response.contains("http://localhost:8081"),
                "Response should contain the backend URL 'http://localhost:8081'.");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(monitoringPort).report(captor.capture());
        assertTrue(captor.getValue().contains("http://localhost:8081"),
                "Monitoring report should contain the backend URL 'http://localhost:8081'.");
    }

    @Test
    void testDynamicRegistration() throws LoadBalancerException {
        // Remove the initial server.
        backendRepository.removeBackend(new BackendServer("http://localhost:8081"));
        // Register a new backend dynamically.
        backendRepository.registerBackend(new BackendServer("http://localhost:8082"));

        // Re-create the service to pick up updated repository state.
        loadBalancerService = new LoadBalancerService(routingStrategyFactory, monitoringPort, backendRepository);

        String request = "Dynamic Test";
        String response = loadBalancerService.forwardRequest(request);

        // Expect the response to now contain the new backend URL.
        assertTrue(response.contains("http://localhost:8082"),
                "Response should contain the dynamically registered backend URL 'http://localhost:8082'.");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(monitoringPort).report(captor.capture());
        assertTrue(captor.getValue().contains("http://localhost:8082"),
                "Monitoring report should contain the backend URL 'http://localhost:8082'.");
    }
}
