# Load Balancer

## Overview
This project implements a load balancer using Java 21, Spring Boot 3.4.4, and Maven. Built using a hexagonal architecture, it separates the core load balancing logic from external interfaces. The load balancer supports dynamic registration of backend servers via REST endpoints and uses a map-based repository to manage server health (active vs. inactive). Requests are distributed using a load balancing strategy (Round Robin by default) and are forwarded asynchronously using Java 21 virtual threads. Health checks are performed periodically to mark unresponsive servers as unhealthy, while monitoring is integrated using Spring Boot Actuator.

## Key Features:

- **Dynamic Backend Registration:**-  Add and remove backend servers at runtime via REST endpoints.

- **Periodic Health Checks:**-  Automatically mark unresponsive servers as unhealthy until they recover.

- **Load Balancing:**-  Uses Round Robin strategy by default (extendable with additional strategies).

- **Concurrency:**-  Utilizes Java 21 virtual threads to handle a large number of simultaneous requests.

- **Monitoring:**- Exposes health and metrics via Spring Boot Actuator.

- **Integration with WireMock:**-  For simulating backend servers during development/testing.

## Prerequisites

- **JDK:**-  21

- **Maven:**- 3.8.5 or later

- **WireMock Standalone:**- For simulating backend servers

## Setup Instructions

Building the Project

Clone the Repository:
git clone <repository-url>
cd load-balancer

Build the Project with Maven:
mvn clean package

Running the Application

Using Maven
To run the application using Maven:
mvn clean spring-boot:run -Dspring-boot.run.profiles=prod
Replace prod with dev if you want to use an alternative strategy (if configured).

Using the Packaged JAR
After packaging, run the application with:
java -jar target/load-balancer-1.0.0.jar

## API Endpoints

Forwarding Requests
Forward Request:

Method: POST

URL: http://localhost:8080/api/loadbalancer/forward

Body: Plain text payload representing the request

Dynamic Backend Management
Register a New Backend Server:

Method: POST

URL: http://localhost:8080/api/loadbalancer/backend?url=<backend_URL>
Example:
curl -X POST "http://localhost:8080/api/loadbalancer/backend?url=http://localhost:8081"

Remove a Backend Server:

Method: DELETE

URL: http://localhost:8080/api/loadbalancer/backend?url=<backend_URL>
Example:
curl -X DELETE "http://localhost:8080/api/loadbalancer/backend?url=http://localhost:8081"

Get Backend Status:

Method: GET

URL: http://localhost:8080/api/loadbalancer/backend/status
This endpoint returns a JSON object with the lists of active and inactive backend servers.

Actuator Endpoints
Health Check:
curl http://localhost:8080/actuator/health

Metrics:
curl http://localhost:8080/actuator/metrics

WireMock Setup Instructions

WireMock can be used to simulate backend servers when testing your load balancer.

Steps to Set Up WireMock as a Standalone Server

Download WireMock Standalone:
Download the standalone JAR from WireMock Downloads.

Run WireMock:
java -jar wiremock-standalone-<version>.jar --port 8081
This starts WireMock on port 8081.

Configure Stub Mappings:

Create a folder named mappings in the same directory where you run WireMock.

Inside the mappings folder, create a JSON file (e.g., health.json) with the following content:

{ "request": { "method": "GET", "url": "/" }, "response": { "status": 200, "body": "OK", "headers": { "Content-Type": "text/plain" } } }

WireMock will load these mappings automatically. When your load balancer calls http://localhost:8081/ (or the appropriate URL), WireMock will simulate a healthy backend response.

Using WireMock in Your Load Balancer:
Update your backend registration (via REST endpoints or directly in configuration) to use the WireMock URL (e.g., http://localhost:8081) for testing.

## Testing

### Running Unit and Integration Tests

#### To run tests:

- **mvn test**

The test suite includes:

- **LoadBalancerServiceTest:** Verifies that requests are forwarded correctly and dynamic registration works.

- **BackendManagementControllerTest:** Tests endpoints for dynamic registration and removal of backend servers.

- **HealthCheckAdapterTest:** Ensures that health checks correctly mark servers as unhealthy or healthy.

- **RoundRobinConcurrencyTest:** Simulates concurrent access and verifies that Round Robin load balancing functions correctly.

## Extending the Project

### New Load Balancing Strategies:
Implement additional strategies in the com.liftlab.loadbalancer.domain.strategy package and update RoutingStrategyFactory as needed.

