# Recipe Manager Service

A modern, production-ready recipe management backend service built with Java
Spring Boot, designed for cloud-native deployment on Kubernetes.

## Features

- RESTful API for recipe management
- Media management with external service integration
- PostgreSQL database integration
- Health checks and metrics
- Containerized with Docker
- Kubernetes manifests for deployment
- CI/CD with GitHub Actions

## Tech Stack

- Java 21, Spring Boot 3, PostgreSQL, Maven, Docker, Kubernetes

## Quick Start (5 minutes)

1. **Prerequisites & Setup**

   ```bash
   # Clone repository
   git clone https://github.com/your-org/recipe-manager-service.git
   cd recipe-manager-service

   # Copy environment configuration
   cp .env.example .env
   # Edit .env with your database credentials
   ```

2. **Start Database**

   ```bash
   # Using Docker (recommended)
   docker run --name recipe-postgres \
     -e POSTGRES_DB=recipe_db \
     -e POSTGRES_USER=recipe_user \
     -e POSTGRES_PASSWORD=your_password \
     -p 5432:5432 \
     -d postgres:15
   ```

3. **Build & Run Application**

   ```bash
   mvn clean install
   java -jar target/recipe-manager-service-*.jar
   ```

4. **Verify Installation**

   ```bash
   curl http://localhost:8080/actuator/health
   # Should return: {"status":"UP"}
   ```

## System Requirements

### Minimum System Requirements

- **CPU**: 2 cores
- **Memory**: 4GB RAM
- **Disk Space**: 2GB free space
- **Network**: Internet connection for dependencies

### Software Requirements

- **Java**: 21+ (Eclipse Temurin LTS recommended)
- **Maven**: 3.9.0+
- **Docker**: 24.0+ with Compose v2
- **PostgreSQL**: 15+ (for local development)
- **Kubernetes**: 1.28+ (for production deployment)

### Operating System Support

- **Linux**: Ubuntu 20.04+, RHEL 8+, Amazon Linux 2
- **macOS**: 12+ (Intel and Apple Silicon)
- **Windows**: 10+ with WSL2

### Port Requirements

- **Application**: 8080 (default, configurable)
- **Database**: 5432 (PostgreSQL)
- **Kubernetes**: 80, 443 (ingress)

## Installation

For detailed installation instructions, see [INSTALL.md](INSTALL.md).

### Prerequisites

- Java 21+
- Maven 3.9+
- Docker
- PostgreSQL 15+
- Kubernetes cluster (minikube, k3s, GKE, etc.)

### Environment Variables

Required environment variables (see `.env.example` for complete list):

```env
# Database Configuration
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=recipe_db
POSTGRES_SCHEMA=recipe_manager
RECIPE_MANAGEMENT_DB_USER=recipe_user
RECIPE_MANAGEMENT_DB_PASSWORD=your_secure_password

# JWT Configuration (MUST match user-management-service)
JWT_SECRET=your-very-secure-secret-key-at-least-32-characters-long

# Application Configuration
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080
```

### Database Setup

#### Option 1: Docker (Recommended)

```bash
docker run --name recipe-postgres \
  -e POSTGRES_DB=recipe_db \
  -e POSTGRES_USER=recipe_user \
  -e POSTGRES_PASSWORD=your_password \
  -p 5432:5432 \
  -d postgres:15
```

#### Option 2: Local PostgreSQL

```bash
# Install PostgreSQL 15+
sudo apt install postgresql postgresql-contrib  # Ubuntu
brew install postgresql                         # macOS

# Create database and user
sudo -u postgres createdb recipe_db
sudo -u postgres createuser recipe_user
sudo -u postgres psql -c "ALTER USER recipe_user WITH ENCRYPTED PASSWORD 'your_password';"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE recipe_db TO recipe_user;"
```

### Build & Run Locally

```sh
# Build application
mvn clean install

# Run application
java -jar target/recipe-manager-service-*.jar

# Or run with Maven
mvn spring-boot:run
```

### Docker

```sh
docker build -t your-docker-repo/recipe-manager-service:latest .
```

### Kubernetes

1. Create secrets/configmaps as needed (see `k8s/secret-template.yaml`)
2. Copy `.env.example` to `.env` and fill in your values
3. Deploy:

```sh
./scripts/containerManagement/deploy-container.sh
```

## Troubleshooting

### Authentication Issues (403 Forbidden)

If you're getting 403 errors with JWT tokens from your user-management-service:

1. **Check JWT Secret Configuration**

   ```bash
   # Ensure both services use the same JWT secret
   # In your .env file:
   JWT_SECRET=your-shared-jwt-secret-key-here
   ```

2. **Verify Service Logs**

   ```bash
   kubectl logs -n recipe-manager -l app=recipe-manager-service
   ```

   Look for: "JWT signature does not match locally computed signature"

3. **Common Causes**
   - Services using different JWT secret keys
   - Missing JWT_SECRET environment variable
   - Different token signing algorithms

See [Authentication Documentation](docs/AUTHENTICATION.md) for detailed setup.

## CI/CD

- Automated build, test, and Docker image build via GitHub Actions (`.github/workflows/ci.yml`)

## API Usage

### Authentication

This service integrates with the user-management-service for JWT-based authentication:

```bash
# Get JWT token from user-management-service
curl -X POST http://user-management.local/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}'

# Use token in requests
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     http://localhost:8080/api/v1/recipe-manager/recipes
```

### Example API Calls

```bash
# Health check
curl http://localhost:8080/actuator/health

# Get all recipes (requires authentication)
curl -H "Authorization: Bearer YOUR_TOKEN" \
     http://localhost:8080/api/v1/recipe-manager/recipes

# Create recipe (requires authentication)
curl -X POST \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Pasta","description":"Delicious pasta recipe"}' \
  http://localhost:8080/api/v1/recipe-manager/recipes

# Get recipe media (requires authentication)
curl -H "Authorization: Bearer YOUR_TOKEN" \
     http://localhost:8080/recipe-management/recipes/123/media

# Upload media for recipe (requires authentication)
curl -X POST \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@photo.jpg" \
  -F "originalFilename=recipe_photo.jpg" \
  -F "mediaType=IMAGE_JPEG" \
  -F "fileSize=1048576" \
  http://localhost:8080/recipe-management/recipes/123/media

# Delete recipe media (requires authentication)
curl -X DELETE \
  -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/recipe-management/recipes/123/media/456
```

### Testing

#### Postman Collection Testing (Recommended)

Import the comprehensive Postman collections from `tests/postman/` directory:

- `Recipe-Manager-Recipe.postman_collection.json` - Recipe management endpoints
- `Recipe-Manager-Auth.postman_collection.json` - Authentication endpoints
- `Recipe-Manager-Actuator.postman_collection.json` - Health and metrics endpoints
- `Recipe-Manager-Ingredient.postman_collection.json` - Ingredient management
- `Recipe-Manager-Media.postman_collection.json` - Media operations
- `Recipe-Manager-Review.postman_collection.json` - Review system
- `Recipe-Manager-Step.postman_collection.json` - Recipe steps
- `Recipe-Manager-Tag.postman_collection.json` - Tag management

See [Postman Testing Guide](tests/postman/README.md) for detailed instructions.

## Development

### Code Quality & Formatting

Code formatting is handled automatically:

- **On build**: `mvn clean install` runs Spotless formatting
- **In VS Code**: Format on save enabled
- **Pre-commit**: Hooks validate formatting before commit

#### Manual Commands

```bash
# Format code
mvn spotless:apply

# Check formatting
mvn spotless:check

# Run code quality checks
mvn checkstyle:check spotbugs:check pmd:check
```

### Running Tests

#### Test Structure

```text
src/test/
├── unit/          # Unit tests (mocked dependencies)
├── component/     # Component tests (Spring context)
├── dependency/    # Integration tests (Karate framework)
└── performance/   # JMeter performance tests
```

#### Test Execution

```bash
# All tests
mvn test

# Specific test types
mvn test -P unit-tests
mvn test -P component-tests
mvn test -P dependency-tests

# Coverage report (requires 90% coverage)
mvn test jacoco:report
open target/site/jacoco/index.html
```

### Database Migrations

Database schema is managed with Flyway:

- **Location**: `src/main/resources/db/migration/`
- **Naming**: `V{version}__{description}.sql`
- **Example**: `V001__Create_recipes_table.sql`

```bash
# Run migrations manually
mvn flyway:migrate

# Migration info
mvn flyway:info
```

### Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for development guidelines,
coding standards, and pull request process.

## Security

### Security Features

- JWT-based authentication
- Role-based authorization (RBAC)
- Input validation and sanitization
- SQL injection prevention
- Security headers implementation

### Security Policy

For security vulnerability reporting, see [SECURITY.md](SECURITY.md).

### Best Practices

- Use strong JWT secrets (32+ characters)
- Enable HTTPS in production
- Regular dependency updates
- Monitor security advisories

## Monitoring & Observability

### Actuator Endpoints

- `/actuator/health` - Application health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus metrics format

### Logging

- Structured JSON logging in production
- Request ID tracking for distributed tracing
- Separate security event logging
- Log levels configurable per environment

### Health Checks

```bash
# Basic health check
curl http://localhost:8080/actuator/health

# Detailed health (requires management access)
curl http://localhost:8080/actuator/health/db
```

## Deployment

### Docker Deployment

```bash
# Build image
docker build -t recipe-manager-service:latest .

# Run container
docker run -p 8080:8080 \
  -e POSTGRES_HOST=host.docker.internal \
  -e POSTGRES_DB=recipe_db \
  -e POSTGRES_USER=recipe_user \
  -e POSTGRES_PASSWORD=password \
  -e JWT_SECRET=your-secret \
  recipe-manager-service:latest
```

### Kubernetes Deployment

```bash
# Create namespace
kubectl create namespace recipe-manager

# Deploy using provided manifests
kubectl apply -f k8s/ -n recipe-manager

# Check deployment
kubectl get pods -n recipe-manager
kubectl get services -n recipe-manager
```

### Environment-Specific Profiles

- **Development**: `SPRING_PROFILES_ACTIVE=dev`
- **Staging**: `SPRING_PROFILES_ACTIVE=staging`
- **Production**: `SPRING_PROFILES_ACTIVE=prod`

## Documentation

### Technical Documentation

- [Installation Guide](INSTALL.md) - Detailed setup instructions
- [Architecture](docs/ARCHITECTURE.md) - System architecture overview
- [Authentication](docs/AUTHENTICATION.md) - Authentication & authorization
- [Infrastructure](docs/INFRASTRUCTURE.md) - Infrastructure components

### Development Documentation

- [Contributing Guidelines](CONTRIBUTING.md) - Development workflow
- [Security Policy](SECURITY.md) - Security vulnerability reporting
- [Test Documentation](src/test/README.md) - Testing framework guide

### API Documentation

- Comprehensive Postman collections in `tests/postman/` for API testing
- Detailed collection documentation in [tests/postman/README.md](tests/postman/README.md)
- OpenAPI/Swagger documentation (planned)

## Service Dependencies

### Required Services

- **user-management-service**: JWT token generation and validation
- **media-management-service**: Media file storage and processing
- **PostgreSQL**: Primary database storage

### Optional Services

- **Redis**: Caching layer (future enhancement)
- **Message Queue**: Async processing (future enhancement)

### Service Integration

Ensure JWT secrets match across all services:

```bash
# Both services must use the same JWT_SECRET
export JWT_SECRET="your-shared-secret-key"
```

## Remaining Work & Recommended Enhancements

### 1. Database Schema Implementation

**Priority: HIGH** - Currently using placeholder migration

- **Gap**: Database migrations are placeholder (`V001__Initial_schema_placeholder.sql`)
- **Recommendation**: Implement complete database schema with proper table
  creation scripts
- **Benefit**: Enable full database functionality and production deployment

### 2. Advanced Search Features

**Priority: MEDIUM** - Enhance existing search capabilities

- **Enhancement**: Add full-text search, filtering by nutrition data,
  advanced ingredient matching
- **Implementation**: Consider PostgreSQL full-text search or Elasticsearch integration

### 3. Caching Layer Optimization

**Priority: MEDIUM** - Improve performance with strategic caching

- **Enhancement**: Implement Redis for distributed caching, cache warming strategies
- **Target Areas**: Recipe search results, external service responses, user preferences

### 4. Real-Time Features

**Priority: LOW** - Add modern user experience features

- **Enhancement**: WebSocket integration for real-time recipe updates
- **Use Cases**: Live cooking mode, collaborative recipe editing, real-time comments
- **Implementation**: Spring WebSocket with STOMP protocol

### 5. Advanced Media Processing

**Priority: MEDIUM** - Enhance media capabilities

- **Enhancement**: Image optimization, thumbnail generation, video processing
- **Integration**: Enhanced MediaManager service capabilities

### 6. Analytics & Metrics Enhancement

**Priority: LOW** - Improve observability

- **Enhancement**: Custom business metrics, user behavior tracking,
  recipe popularity analytics
- **Implementation**: Custom metrics for recipe views, favorites,
  cooking completion rates

### 7. API Rate Limiting

**Priority: MEDIUM** - Production security hardening

- **Enhancement**: Implement rate limiting per user/API key
- **Implementation**: Redis-backed rate limiter or Spring Cloud Gateway integration
- **Benefit**: Prevent abuse and ensure service stability

### 8. Batch Processing Capabilities

**Priority: LOW** - Handle large-scale operations

- **Enhancement**: Spring Batch integration for bulk recipe imports, data migrations
- **Use Cases**: Recipe data imports, bulk nutritional data updates, user data migrations

### 9. Event-Driven Architecture

**Priority: MEDIUM** - Improve scalability and decoupling

- **Enhancement**: Message queue integration (RabbitMQ/Apache Kafka)
- **Use Cases**: Recipe change notifications, async media processing,
  user activity tracking
- **Benefit**: Better scalability and system decoupling

### 10. Advanced Testing Enhancements

**Priority: MEDIUM** - Improve testing coverage

- **Enhancement**: Contract testing, chaos engineering, performance baseline tests
- **Implementation**: Testcontainers improvements, mutation testing

---

MIT License
