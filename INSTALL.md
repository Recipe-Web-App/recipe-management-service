# Installation Guide - Recipe Management Service

This guide provides step-by-step instructions for setting up the Recipe
Management Service in different environments.

## System Requirements

### Minimum Requirements

- **CPU**: 2 cores
- **Memory**: 4GB RAM
- **Disk Space**: 2GB free space
- **Network**: Internet connection for dependency downloads

### Software Requirements

- **Java**: 21+ (Eclipse Temurin LTS recommended)
- **Maven**: 3.9.0 or higher
- **Docker**: 24.0+ with Docker Compose v2
- **PostgreSQL**: 15+ (for local development)
- **Git**: 2.30+ for version control

### Operating System Support

- **Linux**: Ubuntu 20.04+, RHEL 8+, Amazon Linux 2, CentOS 8+
- **macOS**: 12+ (Intel and Apple Silicon)
- **Windows**: 10+ with WSL2 (Windows Subsystem for Linux)

## Quick Start (5 Minutes)

### Option 1: Docker Compose (Recommended)

```bash
# 1. Clone the repository
git clone https://github.com/your-org/recipe-management-service.git
cd recipe-management-service

# 2. Set up environment
cp .env.example .env
# Edit .env with your configuration

# 3. Start with Docker Compose
docker-compose up -d

# 4. Verify installation
curl http://localhost:8080/actuator/health
```

### Option 2: Local Development Setup

```bash
# 1. Clone and setup
git clone https://github.com/your-org/recipe-management-service.git
cd recipe-management-service
cp .env.example .env

# 2. Start PostgreSQL
docker run --name recipe-postgres \
  -e POSTGRES_DB=recipe_db \
  -e POSTGRES_USER=recipe_user \
  -e POSTGRES_PASSWORD=your_password \
  -p 5432:5432 \
  -d postgres:15

# 3. Build and run
mvn clean install
java -jar target/recipe-management-service-*.jar

# 4. Verify
curl http://localhost:8080/actuator/health
```

## Detailed Installation Instructions

### Step 1: Install Prerequisites

#### Java 21 Installation

**Ubuntu/Debian:**

```bash
# Install OpenJDK 21
sudo apt update
sudo apt install openjdk-21-jdk

# Verify installation
java --version
javac --version
```

**macOS:**

```bash
# Using Homebrew
brew install openjdk@21

# Add to PATH (add to ~/.zshrc or ~/.bash_profile)
export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"
```

**Windows (WSL2):**

```bash
# In WSL2 terminal
sudo apt update
sudo apt install openjdk-21-jdk
```

#### Maven Installation

**Ubuntu/Debian:**

```bash
sudo apt install maven
mvn --version
```

**macOS:**

```bash
brew install maven
```

**Windows (WSL2):**

```bash
sudo apt install maven
```

#### Docker Installation

**Ubuntu:**

```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Install Docker Compose
sudo apt install docker-compose-plugin

# Verify
docker --version
docker compose version
```

**macOS:**

```bash
# Install Docker Desktop
brew install --cask docker
# Start Docker Desktop from Applications
```

**Windows:**

- Install Docker Desktop for Windows
- Enable WSL2 integration

### Step 2: Database Setup

#### Option A: Docker PostgreSQL (Recommended)

```bash
# Create PostgreSQL container
docker run --name recipe-postgres \
  -e POSTGRES_DB=recipe_db \
  -e POSTGRES_USER=recipe_user \
  -e POSTGRES_PASSWORD=secure_password \
  -e POSTGRES_SCHEMA=recipe_management \
  -p 5432:5432 \
  -v recipe_postgres_data:/var/lib/postgresql/data \
  -d postgres:15

# Verify database is running
docker ps
docker logs recipe-postgres
```

#### Option B: Local PostgreSQL Installation

**Ubuntu/Debian:**

```bash
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Create database and user
sudo -u postgres psql
CREATE DATABASE recipe_db;
CREATE USER recipe_user WITH ENCRYPTED PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE recipe_db TO recipe_user;
\q
```

**macOS:**

```bash
brew install postgresql
brew services start postgresql

# Create database
createdb recipe_db
psql recipe_db
CREATE USER recipe_user WITH ENCRYPTED PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE recipe_db TO recipe_user;
\q
```

### Step 3: Application Setup

#### Clone Repository

```bash
git clone https://github.com/your-org/recipe-management-service.git
cd recipe-management-service
```

#### Environment Configuration

```bash
# Copy environment template
cp .env.example .env

# Edit configuration
nano .env  # or your preferred editor
```

**Required Environment Variables:**

```env
# Database Configuration
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=recipe_db
POSTGRES_SCHEMA=recipe_management
RECIPE_MANAGEMENT_DB_USER=recipe_user
RECIPE_MANAGEMENT_DB_PASSWORD=secure_password

# JWT Configuration (only required if OAUTH2_INTROSPECTION_ENABLED=false)
JWT_SECRET=your-very-secure-secret-key-at-least-32-characters-long

# Application Configuration
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080
```

#### Build Application

```bash
# Clean build with tests
mvn clean install

# Quick build (skip tests)
mvn clean package -DskipTests
```

#### Database Migration

```bash
# Run Flyway migrations (if configured)
mvn flyway:migrate

# Or let Spring Boot handle it automatically
# (configured in application.yml)
```

### Step 4: Run Application

#### Local Development

```bash
# Method 1: Using Maven
mvn spring-boot:run

# Method 2: Using JAR file
java -jar target/recipe-management-service-*.jar

# Method 3: With custom JVM options
java -Xmx2g -Xms1g -jar target/recipe-management-service-*.jar
```

#### Production Deployment

```bash
# Create production profile
export SPRING_PROFILES_ACTIVE=prod

# Run with production settings
java -jar \
  -Dspring.profiles.active=prod \
  -Xmx4g -Xms2g \
  target/recipe-management-service-*.jar
```

### Step 5: Verification

#### Health Check

```bash
# Basic health check
curl http://localhost:8080/actuator/health

# Expected response:
# {"status":"UP"}
```

#### API Testing

```bash
# Test API endpoint (requires authentication)
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     http://localhost:8080/api/v1/recipe-management/recipes
```

#### Database Connection

```bash
# Connect to database
psql -h localhost -U recipe_user -d recipe_db

# List tables
\dt
```

## Development Environment Setup

### VS Code Setup

1. **Install VS Code**
2. **Open project**: `code .`
3. **Install extensions**: VS Code will prompt to install recommended extensions
4. **Configure settings**: Settings are already configured in `.vscode/settings.json`

### Pre-commit Hooks

```bash
# Install pre-commit
pip install pre-commit

# Install hooks
pre-commit install

# Test hooks
pre-commit run --all-files
```

### Testing Setup

```bash
# Run all tests
mvn test

# Run specific test types
mvn test -P unit-tests
mvn test -P component-tests
mvn test -P dependency-tests

# Generate coverage report
mvn test jacoco:report
open target/site/jacoco/index.html
```

## Docker Deployment

### Build Docker Image

```bash
# Build image
docker build -t recipe-management-service:latest .

# Tag for registry
docker tag recipe-management-service:latest your-registry/recipe-management-service:v1.0.0

# Push to registry
docker push your-registry/recipe-management-service:v1.0.0
```

### Docker Compose Deployment

```yaml
# docker-compose.yml
version: '3.8'
services:
  recipe-management:
    image: recipe-management-service:latest
    ports:
      - "8080:8080"
    environment:
      - POSTGRES_HOST=postgres
      - POSTGRES_DB=recipe_db
      - POSTGRES_USER=recipe_user
      - POSTGRES_PASSWORD=secure_password
      - JWT_SECRET=your-jwt-secret  # Only if introspection disabled
    depends_on:
      - postgres

  postgres:
    image: postgres:15
    environment:
      - POSTGRES_DB=recipe_db
      - POSTGRES_USER=recipe_user
      - POSTGRES_PASSWORD=secure_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

volumes:
  postgres_data:
```

```bash
# Deploy with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f recipe-management

# Stop services
docker-compose down
```

## Kubernetes Deployment

### Prerequisites

- Kubernetes cluster (minikube, k3s, GKE, EKS, AKS)
- kubectl configured
- Docker registry access

### Deployment Steps

```bash
# 1. Create namespace
kubectl create namespace recipe-management

# 2. Create secrets
kubectl create secret generic recipe-management-secrets \
  --from-literal=postgres-password=secure_password \
  --from-literal=jwt-secret=your-jwt-secret \
  -n recipe-management

# 3. Deploy using provided manifests
kubectl apply -f k8s/ -n recipe-management

# 4. Check deployment status
kubectl get pods -n recipe-management
kubectl get services -n recipe-management

# 5. Access application
kubectl port-forward service/recipe-management-service 8080:8080 -n recipe-management
```

## Troubleshooting

### Common Issues

#### Java Version Issues

```bash
# Check Java version
java --version

# If wrong version, update JAVA_HOME
export JAVA_HOME=/path/to/java-21
export PATH=$JAVA_HOME/bin:$PATH
```

#### Database Connection Issues

```bash
# Test database connection
psql -h localhost -U recipe_user -d recipe_db

# Check if PostgreSQL is running
sudo systemctl status postgresql  # Linux
brew services list | grep postgres  # macOS
docker ps | grep postgres  # Docker
```

#### Port Already in Use

```bash
# Find process using port 8080
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Kill process
kill -9 <PID>

# Or use different port
export SERVER_PORT=8081
```

#### Memory Issues

```bash
# Increase JVM memory
export JAVA_OPTS="-Xmx4g -Xms2g"
java $JAVA_OPTS -jar target/recipe-management-service-*.jar

# Or set in application.yml
server:
  tomcat:
    threads:
      max: 200
```

#### Permission Issues

```bash
# Fix file permissions
chmod +x mvnw
sudo chown -R $USER:$USER .

# Docker permission issues
sudo usermod -aG docker $USER
newgrp docker
```

### Log Analysis

```bash
# Application logs
tail -f logs/recipe-management-service.log

# Docker logs
docker logs -f recipe-management-service

# Kubernetes logs
kubectl logs -f deployment/recipe-management-service -n recipe-management
```

### Performance Tuning

```bash
# JVM tuning for production
java -server \
  -Xmx4g -Xms2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -jar target/recipe-management-service-*.jar
```

## Environment-Specific Configuration

### Development Environment

- Verbose logging enabled
- H2 in-memory database option
- Hot reload enabled
- Debug port exposed (5005)

### Staging Environment

- Production-like configuration
- Real database connections
- Monitoring enabled
- SSL/TLS configured

### Production Environment

- Optimized JVM settings
- Connection pooling configured
- Caching enabled
- Security hardening applied
- Monitoring and alerting configured

## Integration with Other Services

### User Management Service

1. Ensure both services use the same JWT secret
2. Configure service-to-service authentication
3. Set up proper network connectivity

### Monitoring Integration

1. Configure Prometheus metrics endpoint
2. Set up Grafana dashboards
3. Configure log aggregation (ELK stack)
4. Set up health check monitoring

## Security Considerations

### Network Security

- Use HTTPS in production
- Configure proper firewall rules
- Implement network segmentation
- Use VPN for remote access

### Application Security

- Change default passwords
- Use strong JWT secrets
- Enable audit logging
- Regular security updates

### Database Security

- Use encrypted connections
- Implement proper user permissions
- Regular backups
- Database activity monitoring

## Backup and Recovery

### Database Backup

```bash
# Create backup
pg_dump -h localhost -U recipe_user recipe_db > backup.sql

# Restore backup
psql -h localhost -U recipe_user recipe_db < backup.sql
```

### Application Backup

```bash
# Backup configuration
tar -czf config-backup.tar.gz .env application*.yml

# Backup logs
tar -czf logs-backup.tar.gz logs/
```

## Next Steps

After successful installation:

1. **Configure monitoring** - Set up application and infrastructure monitoring
2. **Set up CI/CD** - Configure automated deployment pipeline
3. **Performance testing** - Run load tests to validate performance
4. **Security audit** - Perform security assessment
5. **Documentation** - Update API documentation and runbooks

## Getting Help

- **Documentation**: Check README.md and docs/ directory
- **Issues**: Create GitHub issue with installation problems
- **Community**: Join our community chat (if available)
- **Support**: Contact support team for enterprise support

---

**Installation complete!** Your Recipe Management Service should now be running successfully.
