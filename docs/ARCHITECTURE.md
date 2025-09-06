# Recipe Management Service - Architecture Overview

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

## Media Management Architecture

### External Service Integration

- **Media Storage**: Integrates with external media-management-service (Rust/Axum)
- **Content-Addressable Storage**: Files stored by SHA-256 hash for deduplication
- **Resilience Patterns**: Circuit breaker, retry, and fallback mechanisms
- **Async Processing**: Non-blocking file upload and processing pipeline

### Media Service Components

- **MediaController**: REST endpoints for media operations (8 endpoints)
- **MediaService**: Business logic with security-first validation
- **MediaManagerService**: External service integration with resilience4j
- **Media Entities**: Database entities for media associations
  (RecipeMedia, IngredientMedia, StepMedia)

### Security Model

- **User Ownership**: Recipe ownership validation before media operations
- **Content Validation**: File type and size validation
- **JWT Authentication**: Integrated with user-management-service
- **Access Control**: Operation-specific permission checks

### Service Dependencies

- **media-management-service**: File storage and processing (required)
- **user-management-service**: Authentication and authorization (required)
- **PostgreSQL**: Metadata storage for media associations (required)

## Extensibility

- Modular package structure for controllers, services, repositories, models, etc.
- Easily add new features and endpoints
- Clean separation between business logic and external service integration

---
For more details, see the README, MEDIA.md documentation, and code comments.
