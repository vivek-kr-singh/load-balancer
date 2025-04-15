package com.liftlab.loadbalancer.application.service;

import com.liftlab.loadbalancer.adapters.out.repository.MapBasedBackendConfigRepository;
import com.liftlab.loadbalancer.application.factory.RoutingStrategyFactory;
import com.liftlab.loadbalancer.domain.exception.LoadBalancerException;
import com.liftlab.loadbalancer.domain.model.BackendServer;
import com.liftlab.loadbalancer.domain.strategy.LoadBalancingStrategy;
import com.liftlab.loadbalancer.port.out.MonitoringPort;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service that forwards client requests to backend servers using a selected load-balancing strategy.
 * It uses a map-based repository to obtain dynamically managed healthy backend servers.
 */
public class LoadBalancerService {

    private final RoutingStrategyFactory routingStrategyFactory;
    private final MonitoringPort monitoringPort;
    private final MapBasedBackendConfigRepository backendRepository;
    // Use virtual threads for asynchronous processing.
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public LoadBalancerService(RoutingStrategyFactory routingStrategyFactory, MonitoringPort monitoringPort,
                               MapBasedBackendConfigRepository backendRepository) {
        this.routingStrategyFactory = routingStrategyFactory;
        this.monitoringPort = monitoringPort;
        this.backendRepository = backendRepository;
    }

    /**
     * Forwards a client request to a healthy backend server.
     *
     * @param request the client request payload
     * @return a response message indicating which server the request was forwarded to, or an error message
     */
    public String forwardRequest(String request) {
        try {
            LoadBalancingStrategy strategy = routingStrategyFactory.getStrategy();
            List<BackendServer> servers = backendRepository.getActiveServers();
            final BackendServer server = strategy.select(servers);
            executor.submit(() -> {
                // Simulated request forwarding; replace with actual HTTP forwarding logic.
                System.out.println("Forwarding request '" + request + "' to " + server.url());
            });
            String message = "Request forwarded to " + server.url();
            monitoringPort.report(message);
            return message;
        } catch (LoadBalancerException e) {
            String errorMessage = "Failed to forward request: " + e.getMessage();
            monitoringPort.report(errorMessage);
            e.printStackTrace();
            return errorMessage;
        }
    }
}
