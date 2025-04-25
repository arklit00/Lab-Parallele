# Prerequisites
Docker and Docker Compose installed and running with Kubernetes enabled (or Kubernetes cluster).

JDK 17+ (Java must be installed).

# Setup and Run
  Clone or download this repository.

  Run the following command in the root directory:

    docker-compose up --build

The backend API will be accessible at: http://localhost:8081

# API Documentation
The backend API documentation is available via Rapidoc at: http://localhost:8081/docs

# Kubernetes Deployment
To deploy the application in a Kubernetes cluster:
  Run the following commands in the root directory:
  
    kubectl apply -f backend-deployment.yaml
    kubectl apply -f backend-service.yaml
  
The backend API will be accessible at: http://localhost:30081/

# 12-Factor Compliance
This application adheres to 12-factor principles:

  1. Codebase: Single Git repository for the backend application.
  2. Dependencies:  All dependencies are declared in build.gradle.kts using Gradle Kotlin DSL.
  3. Config: Configuration values (e.g., PORT, HOST) are externalized via environment variables.
  4. Backing Services:  Future services (e.g., databases or message queues) will be attached via network URLs or env vars.
  5. Build, Release, Run: The app is containerized with Docker. Kubernetes or Docker Compose separates build and runtime environments.
  6. Processes: The app runs as a stateless process. All data is stored externally.
  7. Port Binding: The application listens on the port defined by PORT env var (default: 8081).
  8. Concurrency: Kubernetes enables scalability.
  9. Disposability: The app starts quickly and can shut down cleanly. Statelessness allows for easy replacement.
  10. Dev/Prod Parity: Docker and Kubernetes ensure local dev closely matches production behavior.
  11. Logs: Logging is written to standard output (stdout), suitable for aggregation by container platforms.
  12. Admin Processes: Admin tasks like resetting items can be triggered via HTTP routes (e.g., POST /items/reset).
