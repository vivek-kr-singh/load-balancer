package com.liftlab.loadbalancer.adapters.in.rest;

import com.liftlab.loadbalancer.adapters.out.repository.MapBasedBackendConfigRepository;
import com.liftlab.loadbalancer.domain.model.BackendServer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller that exposes an endpoint to retrieve the status of
 * all registered backend servers.
 */
@RestController
@RequestMapping("/api/loadbalancer/backend")
public class BackendStatusController {

    private final MapBasedBackendConfigRepository backendRepository;

    public BackendStatusController(MapBasedBackendConfigRepository backendRepository) {
        this.backendRepository = backendRepository;
    }

    /**
     * Returns the status of all registered backend servers, divided into active and inactive.
     *
     * @return a JSON object containing the lists of active and inactive backend servers.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, List<BackendServer>>> getBackendStatus() {
        Map<String, List<BackendServer>> statusMap = new HashMap<>();
        List<BackendServer> activeServers = backendRepository.getActiveServers();
        List<BackendServer> inactiveServers = backendRepository.getInactiveServers();
        statusMap.put("activeServers", activeServers);
        statusMap.put("inactiveServers", inactiveServers);
        return ResponseEntity.ok(statusMap);
    }
}
