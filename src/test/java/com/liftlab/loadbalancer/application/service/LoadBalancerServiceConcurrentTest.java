package com.liftlab.loadbalancer.application.service;

import com.liftlab.loadbalancer.adapters.out.repository.MapBasedBackendConfigRepository;
import com.liftlab.loadbalancer.application.factory.RoutingStrategyFactory;
import com.liftlab.loadbalancer.domain.model.BackendServer;
import com.liftlab.loadbalancer.domain.strategy.RoundRobinStrategy;
import com.liftlab.loadbalancer.port.out.MonitoringPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LoadBalancerServiceConcurrentTest {

    private RoutingStrategyFactory routingStrategyFactory;
    private MonitoringPort monitoringPort;
    private MapBasedBackendConfigRepository backendRepository;
    private LoadBalancerService loadBalancerService;

    @BeforeEach
    void setup() throws Exception {
        // Use the RoundRobinStrategy for predictable behavior.
        routingStrategyFactory = new RoutingStrategyFactory(new RoundRobinStrategy());
        // Mock the MonitoringPort.
        monitoringPort = mock(MonitoringPort.class);
        // Use the new map-based repository.
        backendRepository = new MapBasedBackendConfigRepository();
        // Clear any potential pre-registered servers.
        backendRepository.removeBackend(new BackendServer("http://localhost:8081"));
        backendRepository.removeBackend(new BackendServer("http://localhost:8082"));
        backendRepository.removeBackend(new BackendServer("http://localhost:8083"));
        // Register three backend servers.
        backendRepository.registerBackend(new BackendServer("http://localhost:8081"));
        backendRepository.registerBackend(new BackendServer("http://localhost:8082"));
        backendRepository.registerBackend(new BackendServer("http://localhost:8083"));
        // Create the load balancer service.
        loadBalancerService = new LoadBalancerService(routingStrategyFactory, monitoringPort, backendRepository);
    }

    @Test
    void testConcurrentForwardRequests() throws InterruptedException, ExecutionException {
        int numberOfRequests = 60;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numberOfRequests);

        List<Future<String>> futures = new ArrayList<>();

        // Submit concurrent tasks.
        for (int i = 0; i < numberOfRequests; i++) {
            final int index = i;
            futures.add(executorService.submit(() -> {
                // Wait until all tasks are ready.
                startLatch.await();
                try {
                    String response = loadBalancerService.forwardRequest("Concurrent Request " + index);
                    return response;
                } finally {
                    doneLatch.countDown();
                }
            }));
        }

        // Release all tasks at once.
        startLatch.countDown();
        // Wait for all tasks to complete.
        doneLatch.await();
        executorService.shutdown();

        // Collect responses.
        List<String> responses = new ArrayList<>();
        for (Future<String> future : futures) {
            responses.add(future.get());
        }

        // Verify that every response contains one of the registered URLs.
        for (String response : responses) {
            assertThat(response).matches(r ->
                            r.contains("http://localhost:8081") ||
                                    r.contains("http://localhost:8082") ||
                                    r.contains("http://localhost:8083"),
                    "Response should contain one of the registered backend URLs");
        }

        // Optionally, we can verify that the monitoringPort.report() was called the expected number of times.
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(monitoringPort, times(numberOfRequests)).report(captor.capture());

        // Verify that each monitoring report contains one of the registered URLs.
        for (String report : captor.getAllValues()) {
            assertThat(report).matches(r ->
                            r.contains("http://localhost:8081") ||
                                    r.contains("http://localhost:8082") ||
                                    r.contains("http://localhost:8083"),
                    "Monitoring report should contain one of the registered backend URLs");
        }
    }
}
