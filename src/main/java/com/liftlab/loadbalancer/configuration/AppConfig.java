package com.liftlab.loadbalancer.configuration;

import com.liftlab.loadbalancer.adapters.out.monitoring.ConsoleMonitoringAdapter;
import com.liftlab.loadbalancer.adapters.out.repository.MapBasedBackendConfigRepository;
import com.liftlab.loadbalancer.application.factory.RoutingStrategyFactory;
import com.liftlab.loadbalancer.application.service.LoadBalancerService;
import com.liftlab.loadbalancer.domain.strategy.LoadBalancingStrategy;
import com.liftlab.loadbalancer.domain.strategy.RandomStrategy;
import com.liftlab.loadbalancer.domain.strategy.RoundRobinStrategy;
import com.liftlab.loadbalancer.port.out.MonitoringPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Spring configuration class for application beans.
 */
@Configuration
public class AppConfig {

    @Bean
    @Profile({"prod", "default"})
    public LoadBalancingStrategy roundRobinStrategy() {
        return new RoundRobinStrategy();
    }

    @Bean
    @Profile("dev")
    public LoadBalancingStrategy randomStrategy() {
        return new RandomStrategy();
    }

    @Bean
    public RoutingStrategyFactory routingStrategyFactory(LoadBalancingStrategy loadBalancingStrategy) {
        return new RoutingStrategyFactory(loadBalancingStrategy);
    }

    @Bean
    public MonitoringPort monitoringPort() {
        return new ConsoleMonitoringAdapter();
    }

    @Bean
    public LoadBalancerService loadBalancerService(RoutingStrategyFactory routingStrategyFactory,
                                                   MonitoringPort monitoringPort,
                                                   MapBasedBackendConfigRepository backendConfigRepository) {
        return new LoadBalancerService(routingStrategyFactory, monitoringPort, backendConfigRepository);
    }
}
