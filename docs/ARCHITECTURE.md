# Recipe Manager Service - Architecture Overview

## Tech Stack
- Java 21
- Spring Boot 3
- PostgreSQL (database)
- Maven (build)
- Docker (containerization)
- Kubernetes (deployment)
- GitHub Actions (CI/CD)

## Deployment
- Runs as a stateless container in a Kubernetes cluster
- Exposed via Kubernetes Service and Ingress
- Uses ConfigMap/Secret for configuration and credentials
- Health checks via /actuator/health

## Key Design Principles
- 12-factor app methodology
- Environment-agnostic configuration
- Secure secret management
- Observability (metrics, health, logs)
- Automated build, test, and deployment

## Directory Structure
- `src/main/java/` - Application source code
- `src/main/resources/` - Configuration files
- `k8s/` - Kubernetes manifests
- `docs/` - Documentation

## Extensibility
- Modular package structure for controllers, services, repositories, models, etc.
- Easily add new features and endpoints

---
For more details, see the README and code comments.
