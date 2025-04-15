package com.liftlab.loadbalancer.domain.strategy;

import com.liftlab.loadbalancer.domain.exception.LoadBalancerException;
import com.liftlab.loadbalancer.domain.model.BackendServer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RoundRobinConcurrencyTest {

    /**
     * Tests that the RoundRobinStrategy works correctly under high concurrency.
     * The test creates a fixed list of backend servers and uses an ExecutorService
     * to simulate concurrent calls. It then asserts that each server is selected at least once,
     * indicating proper cyclic behavior.
     */
    @Test
    void testConcurrentSelect() throws InterruptedException, ExecutionException {
        // Prepare a list of backend servers.
        List<BackendServer> servers = List.of(
                new BackendServer("http://backend1.liftlab.com"),
                new BackendServer("http://backend2.liftlab.com"),
                new BackendServer("http://backend3.liftlab.com")
        );
        
        RoundRobinStrategy strategy = new RoundRobinStrategy();
        int numThreads = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        List<Callable<BackendServer>> tasks = new ArrayList<>();

        // Prepare tasks that wait for a latch and then call select().
        for (int i = 0; i < numThreads; i++) {
            tasks.add(() -> {
                startLatch.await();
                return strategy.select(servers);
            });
        }

        // Release all tasks concurrently.
        startLatch.countDown();
        List<Future<BackendServer>> futures = executorService.invokeAll(tasks);
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        // Collect the selected servers.
        List<BackendServer> selectedServers = new ArrayList<>();
        for (Future<BackendServer> future : futures) {
            selectedServers.add(future.get());
        }

        // Count the frequency of each server in the results.
        Map<String, Long> frequencyMap = selectedServers.stream()
                .collect(Collectors.groupingBy(BackendServer::url, Collectors.counting()));

        // Assert that every server is selected at least once.
        for (BackendServer server : servers) {
            assertThat(frequencyMap).as("Frequency of " + server.url())
                    .containsKey(server.url());
        }

        // Optionally: Assert that the selections are roughly evenly distributed.
        long minCount = frequencyMap.values().stream().mapToLong(Long::longValue).min().orElse(0);
        long maxCount = frequencyMap.values().stream().mapToLong(Long::longValue).max().orElse(0);
        assertThat(maxCount - minCount)
                .as("Difference between the most and least selected servers should be small")
                .isLessThanOrEqualTo(5);

        // For debugging, print the frequency map (optional).
        frequencyMap.forEach((url, count) -> System.out.println(url + " selected " + count + " times"));
    }

    /**
     * Tests that selecting a backend from an empty list throws a LoadBalancerException.
     */
    @Test
    void testSelectWithEmptyList() {
        RoundRobinStrategy strategy = new RoundRobinStrategy();
        Exception exception = assertThrows(LoadBalancerException.class, () -> strategy.select(List.of()));
        assertThat(exception.getMessage()).isEqualTo("No backend servers available");
    }
}
