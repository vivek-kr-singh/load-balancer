package com.liftlab.loadbalancer.adapters.out.monitoring;

import com.liftlab.loadbalancer.port.out.MonitoringPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Console-based monitoring adapter that logs monitoring messages.
 */
@Component
public class ConsoleMonitoringAdapter implements MonitoringPort {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleMonitoringAdapter.class);

    /**
     * Reports a monitoring message by logging it.
     *
     * @param message the monitoring message to report
     */
    @Override
    public void report(String message) {
        logger.info("Monitoring Report: {}", message);
    }
}
