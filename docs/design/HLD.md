# High-Level Design (HLD)

## Overview
- **Architecture:**  
  The load balancer employs a hexagonal (ports and adapters) architecture, effectively separating the core business logic from external integrations.

- **Dynamic Registration:**  
  Backend servers can be registered dynamically via REST endpoints. A map-based repository manages these servers along with their health status.

- **Load Balancing:**  
  Requests are distributed using a load balancing strategy, with the Round Robin algorithm selected by default.

- **Health Checks:**  
  Periodic health checks are implemented to continuously verify backend server availability. Servers that fail these checks are marked as unhealthy and removed from the active list until they recover.

- **Concurrency:**  
  Java 21 virtual threads are utilized to achieve high concurrency, allowing the load balancer to efficiently manage multiple requests.

- **Monitoring:**  
  Operational events are tracked using a `MonitoringPort` abstraction, with a `ConsoleMonitoringAdapter` handling the actual logging of these events.

## Key Components

### Domain Layer
- **BackendServer:**  
  A record representing a backend server.

- **LoadBalancerException:**  
  A custom exception to handle specific error conditions.

- **LoadBalancingStrategy:**  
  A sealed interface that defines the contract for load balancing strategies. The default implementation is the `RoundRobinStrategy`.

### Application Layer
- **LoadBalancerService:**  
  Coordinates the forwarding of requests. Key responsibilities include:
  - Selecting active backend servers from the map-based repository.
  - Asynchronously forwarding requests using Java virtual threads.
  - Reporting operational events through the `MonitoringPort`.

- **RoutingStrategyFactory:**  
  Manages the logic for selecting the appropriate load balancing strategy, configurable via Spring Profiles based on the environment.

### Ports and Adapters

#### Inbound Adapters
- **LoadBalancerController:**  
  Exposes an endpoint that forwards client requests to the backend servers.

- **BackendManagementController:**  
  Provides REST endpoints to dynamically register or remove backend servers.

#### Outbound Adapters
- **HealthCheckAdapter:**  
  Periodically checks the health of backend servers. Servers that do not pass the health checks are moved from the active list to inactive. Conversely, recovered servers are restored.

- **ConsoleMonitoringAdapter:**  
  Implements the `MonitoringPort` for logging monitoring events to the console.

## Infrastructure
- **Spring Boot:**  
  Used to provide an embedded web server, scheduling, and REST support.

- **Java 21 Virtual Threads:**  
  Facilitate high concurrency, allowing efficient request handling across multiple threads.
