# Low-Level Design (LLD)

## Map-Based Dynamic Repository

### MapBasedBackendConfigRepository
- **Storage Mechanism:**  
  Uses a `ConcurrentHashMap` to store backend server entries.
  - **Key:** Server URL
  - **Value:** `BackendStatus` object containing the server details and its health status (boolean)

### Methods
- **registerBackend(BackendServer server):**  
  Adds a new server if it is not already present in the repository.

- **removeBackend(BackendServer server):**  
  Removes an existing server from the repository.

- **markUnhealthy(BackendServer server):**  
  Updates the server's status to unhealthy.

- **markHealthy(BackendServer server):**  
  Restores a server's status to healthy.

- **getActiveServers() / getInactiveServers():**  
  Returns lists of servers based on their current health status.

## LoadBalancerService

- **Active Server Selection:**  
  Retrieves active servers from the `MapBasedBackendConfigRepository` and uses the selected load balancing strategy (default: `RoundRobinStrategy`) to choose one server.

- **Asynchronous Request Forwarding:**  
  Uses Java 21 virtual threads to forward the client request asynchronously.

- **Event Reporting:**  
  Reports success or error events via the `MonitoringPort` abstraction.

## HealthCheckAdapter

- **Scheduling:**  
  Runs periodically (every 10 seconds).

- **Health Checking Mechanism:**
  - **Active Servers:**  
    Issues an HTTP GET request to each active server. If the response is not HTTP 200 or if a connection error (like `ConnectException`) occurs, the server is marked as unhealthy.

  - **Inactive Servers:**  
    Checks inactive servers as well. If a previously inactive server responds successfully, it is marked as healthy.

- **Exception Handling and Logging:**  
  Incorporates detailed exception handling and logs information regarding health check operations.

## BackendManagementController

- **REST Endpoints for Dynamic Server Registration:**
  - **POST Endpoint:**  
    Adds a new backend server to the system.

  - **DELETE Endpoint:**  
    Removes an existing backend server.

- **Repository Updates:**  
  Uses the `MapBasedBackendConfigRepository` to update the active/inactive status of the servers accordingly.

## Monitoring

### MonitoringPort
- **Role:**  
  An interface to report operational events within the system.

### ConsoleMonitoringAdapter
- **Implementation:**  
  A concrete implementation of `MonitoringPort` that logs monitoring messages to the console.
