# Recipe Manager Service

A modern, production-ready recipe management backend service built with Java Spring Boot, designed for cloud-native deployment on Kubernetes.

## Features
- RESTful API for recipe management
- PostgreSQL database integration
- Health checks and metrics
- Containerized with Docker
- Kubernetes manifests for deployment
- CI/CD with GitHub Actions

## Tech Stack
- Java 21, Spring Boot 3, PostgreSQL, Maven, Docker, Kubernetes

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.9+
- Docker
- Kubernetes cluster (minikube, k3s, GKE, etc.)

### Build & Run Locally
```sh
mvn clean package
java -jar target/recipe-manager-service-*.jar
```

### Docker
```sh
docker build -t your-docker-repo/recipe-manager-service:latest .
```

### Kubernetes
1. Create secrets/configmaps as needed (see `k8s/secret-template.yaml`)
2. Deploy:
```sh
kubectl apply -f k8s/
```

## CI/CD
- Automated build, test, and Docker image build via GitHub Actions (`.github/workflows/ci.yml`)

## Documentation
- [Architecture](docs/ARCHITECTURE.md)

---
MIT License
