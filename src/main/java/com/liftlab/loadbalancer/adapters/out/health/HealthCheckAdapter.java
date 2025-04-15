package com.liftlab.loadbalancer.adapters.out.health;

import com.liftlab.loadbalancer.adapters.out.repository.MapBasedBackendConfigRepository;
import com.liftlab.loadbalancer.domain.exception.LoadBalancerException;
import com.liftlab.loadbalancer.domain.model.BackendServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * HealthCheckAdapter periodically checks the health of registered backend servers.
 * Unhealthy servers are moved out of the active list, and recovered servers are added back.
 */
@Component
public class HealthCheckAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckAdapter.class);
    private final MapBasedBackendConfigRepository backendRepository;

    public HealthCheckAdapter(MapBasedBackendConfigRepository backendRepository) {
        this.backendRepository = backendRepository;
    }

    /**
     * Checks the health of active and inactive backend servers every 10 seconds.
     * If an active server fails its health check, it is marked unhealthy. Inactive servers
     * are checked to see if they have recovered.
     */
    @Scheduled(fixedRate = 10000)
    public void checkHealth() {
        // Check active servers for failures.
        List<BackendServer> activeServers = backendRepository.getActiveServers();
        for (BackendServer server : activeServers) {
            if (!isServerHealthy(server)) {
                logger.warn("Health check FAILED for server {}. Marking as unhealthy.", server.url());
                try {
                    backendRepository.markUnhealthy(server);
                } catch (LoadBalancerException e) {
                    logger.error("Error marking server {} as unhealthy: {} - {}",
                            server.url(), e.getClass().getSimpleName(), e.getMessage(), e);
                }
            }
        }
        // Check inactive servers to see if any have recovered.
        List<BackendServer> inactiveServers = backendRepository.getInactiveServers();
        for (BackendServer server : inactiveServers) {
            if (isServerHealthy(server)) {
                logger.info("Health check OK for server {}. Marking as healthy.", server.url());
                try {
                    backendRepository.markHealthy(server);
                } catch (LoadBalancerException e) {
                    logger.error("Error marking server {} as healthy: {} - {}",
                            server.url(), e.getClass().getSimpleName(), e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Checks whether the specified backend server is healthy by issuing an HTTP GET request.
     *
     * @param server the backend server to check
     * @return true if the server responds with HTTP 200; false otherwise
     */
    private boolean isServerHealthy(BackendServer server) {
        try {
            URL url = new URL(server.url());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000); // 3-second timeout
            connection.connect();
            int responseCode = connection.getResponseCode();
            return responseCode == 200;
        } catch (ConnectException ce) {
            // Handle connection-specific errors gracefully.
            logger.error("Health check error for server {}: Connection refused", server.url());
            return false;
        } catch (Exception e) {
            logger.error("Health check error for server {}: {} - {}",
                    server.url(), e.getClass().getSimpleName(), e.getMessage(), e);
            return false;
        }
    }
}
