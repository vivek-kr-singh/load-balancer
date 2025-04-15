package com.liftlab.loadbalancer.adapters.out.repository;

import com.liftlab.loadbalancer.domain.exception.LoadBalancerException;
import com.liftlab.loadbalancer.domain.model.BackendServer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Repository for managing backend server configurations using a map-based approach.
 * Each backend server is stored along with its health status.
 */
@Repository
public class MapBasedBackendConfigRepository {

    /**
     * Internal map storing backend servers by URL along with their health status.
     */
    private final Map<String, BackendStatus> serverMap = new ConcurrentHashMap<>();

    /**
     * Registers a new backend server.
     * If the server is already registered, it does nothing.
     *
     * @param server the backend server to register
     * @throws LoadBalancerException if the provided server is null or invalid
     */
    public void registerBackend(BackendServer server) throws LoadBalancerException {
        if (server == null || server.url() == null) {
            throw new LoadBalancerException("Invalid backend server provided");
        }
        serverMap.putIfAbsent(server.url(), new BackendStatus(server, true));
    }

    /**
     * Removes a backend server.
     *
     * @param server the backend server to remove
     * @throws LoadBalancerException if the provided server is null or invalid
     */
    public void removeBackend(BackendServer server) throws LoadBalancerException {
        if (server == null || server.url() == null) {
            throw new LoadBalancerException("Invalid backend server provided");
        }
        serverMap.remove(server.url());
    }

    /**
     * Marks a backend server as unhealthy.
     *
     * @param server the backend server to mark as unhealthy
     * @throws LoadBalancerException if the provided server is null or invalid
     */
    public void markUnhealthy(BackendServer server) throws LoadBalancerException {
        if (server == null || server.url() == null) {
            throw new LoadBalancerException("Invalid backend server provided");
        }
        serverMap.computeIfPresent(server.url(), (k, v) -> new BackendStatus(v.server(), false));
    }

    /**
     * Marks a backend server as healthy.
     *
     * @param server the backend server to mark as healthy
     * @throws LoadBalancerException if the provided server is null or invalid
     */
    public void markHealthy(BackendServer server) throws LoadBalancerException {
        if (server == null || server.url() == null) {
            throw new LoadBalancerException("Invalid backend server provided");
        }
        serverMap.computeIfPresent(server.url(), (k, v) -> new BackendStatus(v.server(), true));
    }

    /**
     * Returns a list of active (healthy) backend servers.
     *
     * @return list of healthy backend servers
     */
    public List<BackendServer> getActiveServers() {
        return serverMap.values().stream()
                .filter(BackendStatus::healthy)
                .map(BackendStatus::server)
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of inactive (unhealthy) backend servers.
     *
     * @return list of unhealthy backend servers
     */
    public List<BackendServer> getInactiveServers() {
        return serverMap.values().stream()
                .filter(status -> !status.healthy())
                .map(BackendStatus::server)
                .collect(Collectors.toList());
    }

    /**
     * Internal record that stores the backend server and its health status.
     */
    public record BackendStatus(BackendServer server, boolean healthy) { }
}
