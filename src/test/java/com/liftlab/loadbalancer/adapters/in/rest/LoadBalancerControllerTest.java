package com.liftlab.loadbalancer.adapters.in.rest;

import com.liftlab.loadbalancer.application.service.LoadBalancerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for LoadBalancerController.
 * Verifies that the controller delegates to LoadBalancerService and returns the expected response.
 */
@WebMvcTest(LoadBalancerController.class)
@Import(LoadBalancerControllerTest.TestConfig.class)
public class LoadBalancerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoadBalancerService loadBalancerService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public LoadBalancerService loadBalancerService() {
            // Return a mock LoadBalancerService for testing the controller.
            return mock(LoadBalancerService.class);
        }
    }

    @Test
    public void testForwardRequest() throws Exception {
        String requestPayload = "Sample Request";
        String expectedResponse = "Request forwarded to http://backend1.liftlab.com";

        // Stub the forwardRequest method to return the expected response.
        when(loadBalancerService.forwardRequest(requestPayload)).thenReturn(expectedResponse);

        // Perform a POST request to the /api/loadbalancer/forward endpoint.
        mockMvc.perform(post("/api/loadbalancer/forward")
                        .content(requestPayload))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }
}
