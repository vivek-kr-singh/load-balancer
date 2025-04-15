package com.liftlab.loadbalancer.port.out;

/**
 * Outbound port interface for reporting and monitoring operations.
 */
public interface MonitoringPort {

    /**
     * Reports a monitoring message.
     *
     * @param message the message to report
     */
    void report(String message);
}
