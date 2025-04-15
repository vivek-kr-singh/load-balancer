package com.liftlab.loadbalancer.adapters.in.rest;

import com.liftlab.loadbalancer.application.service.LoadBalancerService;
import com.liftlab.loadbalancer.port.in.LoadBalancerPort;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that handles incoming load balancer requests.
 */
@RestController
@RequestMapping("/api/loadbalancer")
public class LoadBalancerController implements LoadBalancerPort {

    private final LoadBalancerService loadBalancerService;

    public LoadBalancerController(LoadBalancerService loadBalancerService) {
        this.loadBalancerService = loadBalancerService;
    }

    /**
     * Endpoint to forward a request to a backend server.
     *
     * @param request the client request payload
     * @return a response message indicating the result
     */
    @Override
    @PostMapping("/forward")
    public String handleRequest(@RequestBody String request) {
        return loadBalancerService.forwardRequest(request);
    }
}
