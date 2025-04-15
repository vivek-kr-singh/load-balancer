package com.liftlab.loadbalancer.adapters.in.rest;

import com.liftlab.loadbalancer.adapters.out.repository.MapBasedBackendConfigRepository;
import com.liftlab.loadbalancer.domain.exception.LoadBalancerException;
import com.liftlab.loadbalancer.domain.model.BackendServer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for dynamically managing backend server registrations.
 * Uses a map-based repository to add or remove backend servers.
 */
@RestController
@RequestMapping("/api/loadbalancer/backend")
public class BackendManagementController {

    private final MapBasedBackendConfigRepository backendRepository;

    public BackendManagementController(MapBasedBackendConfigRepository backendRepository) {
        this.backendRepository = backendRepository;
    }

    /**
     * Registers a new backend server.
     *
     * @param url the URL of the backend server to register.
     * @return a response indicating successful registration or an error message.
     */
    @PostMapping
    public ResponseEntity<String> addBackend(@RequestParam("url") String url) {
        try {
            BackendServer server = new BackendServer(url);
            backendRepository.registerBackend(server);
            return ResponseEntity.ok("Backend server added: " + url);
        } catch (LoadBalancerException e) {
            return ResponseEntity.badRequest().body("Error registering backend: " + e.getMessage());
        }
    }

    /**
     * Removes an existing backend server.
     *
     * @param url the URL of the backend server to remove.
     * @return a response indicating successful removal or an error message.
     */
    @DeleteMapping
    public ResponseEntity<String> removeBackend(@RequestParam("url") String url) {
        try {
            BackendServer server = new BackendServer(url);
            backendRepository.removeBackend(server);
            return ResponseEntity.ok("Backend server removed: " + url);
        } catch (LoadBalancerException e) {
            return ResponseEntity.badRequest().body("Error removing backend: " + e.getMessage());
        }
    }
}
