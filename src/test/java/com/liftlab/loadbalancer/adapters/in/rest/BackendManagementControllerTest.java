package com.liftlab.loadbalancer.adapters.in.rest;

import com.liftlab.loadbalancer.adapters.out.repository.MapBasedBackendConfigRepository;
import com.liftlab.loadbalancer.domain.model.BackendServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BackendManagementController.class)
@Import(BackendManagementControllerTest.TestConfig.class)
class BackendManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MapBasedBackendConfigRepository backendRepository;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public MapBasedBackendConfigRepository backendRepository() {
            return new MapBasedBackendConfigRepository();
        }
    }

    @Test
    void testAddBackend() throws Exception {
        String url = "http://new.liftlab.com";
        mockMvc.perform(post("/api/loadbalancer/backend").param("url", url))
                .andExpect(status().isOk())
                .andExpect(content().string("Backend server added: " + url));
        assertThat(backendRepository.getActiveServers()).contains(new BackendServer(url));
    }

    @Test
    void testRemoveBackend() throws Exception {
        String url = "http://remove.liftlab.com";
        backendRepository.registerBackend(new BackendServer(url));
        mockMvc.perform(delete("/api/loadbalancer/backend").param("url", url))
                .andExpect(status().isOk())
                .andExpect(content().string("Backend server removed: " + url));
        assertThat(backendRepository.getActiveServers()).doesNotContain(new BackendServer(url));
    }
}
