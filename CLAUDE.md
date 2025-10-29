# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with
code in this repository.

## Build and Development Commands

### Essential Commands

- **Build**: `mvn clean package`
- **Run**: `java -jar target/recipe-management-service-*.jar`
- **Format Code**: `mvn spotless:apply`
- **Code Quality**: `mvn checkstyle:check spotbugs:check pmd:check`
- **Tests**: `mvn test` (all tests)
- **Coverage**: `mvn jacoco:report` (requires tests to run first)

### Test Commands

- **Unit Tests**: `mvn test -Dgroups="unit"`
- **Component Tests**: `mvn test -Dgroups="component"`
- **Integration Tests**: `mvn test -Dgroups="dependency"`
- **Performance Tests**: `mvn verify` (includes JMeter tests)
- **Single Test**: `mvn test -Dtest=ClassName`

### Docker and Deployment

- **Docker Build**: `docker build -t recipe-management-service:latest .`
- **Kubernetes Deploy**: `kubectl apply -f k8s/`
- **Container Scripts**: Available in `scripts/containerManagement/`

## Architecture Overview

### Technology Stack

- **Java 25** with Spring Boot 3.5.7
- **PostgreSQL 15+** database with Flyway migrations
- **Maven** build system with comprehensive quality plugins
- **JWT authentication** with OAuth2 introspection support
- **Feign clients** for external service integration (media-management, recipe-scraper)
- **Docker + Kubernetes** deployment

### Package Structure

```text
com.recipe_manager/
├── client/          # External service clients (Feign)
│   ├── common/     # Common client configurations
│   ├── mediamanager/   # Media management service client
│   └── recipescraper/  # Recipe scraper service client
├── config/          # Spring configuration (Security, Logging, Health)
├── controller/      # REST controllers
├── exception/       # Global exception handling
├── health/          # Custom health check indicators
├── model/
│   ├── converter/  # JPA attribute converters
│   ├── dto/        # Data transfer objects
│   ├── entity/     # JPA entities
│   ├── enums/      # Enums
│   └── mapper/     # MapStruct mappers (Lombok compatible)
├── repository/      # JPA repositories
│   ├── ingredient/ # Ingredient-related repositories
│   ├── media/      # Media-related repositories
│   └── recipe/     # Recipe-related repositories
├── security/        # JWT and security components
├── service/         # Business logic
│   └── external/   # External service integration
└── util/           # Utility classes
```

### Key Patterns

- **Layered Architecture**: Controller → Service → Repository
- **DTO Pattern**: Separate DTOs for requests/responses
- **MapStruct Mapping**: Entity ↔ DTO conversion with Lombok integration
- **Exception Handling**: Global exception handler with custom exceptions
- **Security**: JWT authentication with role-based authorization

### Database Architecture

- **PostgreSQL** with schema: `recipe_manager` (configurable via `POSTGRES_SCHEMA`)
- **Connection Pooling**: HikariCP with optimized settings
- **Schema Management**: Flyway migrations in `src/main/resources/db/migration/`
- **JPA Configuration**: Hibernate with batch processing optimizations

## Test Strategy

### Test Types (Multi-layered Testing)

1. **Unit Tests** (`src/test/unit/`): Mocked dependencies, fastest execution
2. **Component Tests** (`src/test/component/`): Spring context with Testcontainers
3. **Integration Tests** (`src/test/dependency/`): Karate API tests with real dependencies
4. **Performance Tests** (`src/test/performance/`): JMeter load testing

### Test Configuration

- **Base Class**: Extend `AbstractComponentTest` for component tests
- **Tags**: Use `@Tag("unit")`, `@Tag("component")`, `@Tag("dependency")`
- **Coverage Target**: 90% minimum (enforced by JaCoCo)
- **Test Data**: Use Datafaker for realistic test data generation

### Component Test Guidelines

Component tests should test the actual service logic while only mocking
repository/external dependencies:

- **DO**: Mock repositories (`@Mock RecipeRepository`, `@Mock RecipeIngredientRepository`)
- **DO**: Use real services (`useRealRecipeService()`, `useRealIngredientService()`)
- **DO**: Use real mappers (`@SpringBootTest` with mapper implementations)
- **DON'T**: Mock service layer in component tests - this defeats the purpose
- **Pattern**: Follow existing tests in `src/test/component/java/com/recipe_manager/component_tests/recipe_service/`

Example component test setup:

```java
@SpringBootTest(classes = {RecipeIngredientMapperImpl.class, ShoppingListMapperImpl.class})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
@Tag("component")
class MyComponentTest extends AbstractComponentTest {
    @BeforeEach
    protected void setUp() {
        super.setUp();
        useRealIngredientService(); // Test real service logic
    }

    @Test
    void shouldTestRealServiceLogic() {
        // Mock repository data
        when(recipeIngredientRepository.findByRecipeRecipeId(123L)).thenReturn(mockData);
        // Test endpoint - exercises real service + mapper logic
    }
}
```

## Security and Authentication

### JWT Integration

- **Shared Secret**: JWT_SECRET only required if OAUTH2_INTROSPECTION_ENABLED=false
- **Token Validation**: Automatic via `JwtAuthenticationFilter`
- **User Context**: Available via `SecurityContextHolder`
- **Authorization**: Role-based with `@PreAuthorize` annotations

### Environment Variables

- `JWT_SECRET`: JWT signing key (only required if OAUTH2_INTROSPECTION_ENABLED=false)
- `POSTGRES_HOST/PORT/DB`: Database connection
- `RECIPE_MANAGEMENT_DB_USER/PASSWORD`: Database credentials
- `POSTGRES_SCHEMA`: Database schema (default: recipe_manager)

## Code Quality Standards

### Enforced Standards

- **Google Java Style**: Automatic formatting via Spotless
- **Static Analysis**: SpotBugs, PMD, Checkstyle (all must pass)
- **Code Coverage**: 90% minimum line coverage (enforced by JaCoCo)
- **Documentation**: Javadoc required for public APIs
- **Line Length**: 100 characters maximum
- **Indentation**: 2 spaces (configured in .editorconfig)

### Unit Test Requirements

- **MANDATORY**: Every new file created must have corresponding unit tests
  before proceeding to the next file
- **No Exceptions**: This applies to all classes including DTOs, services,
  controllers, mappers, exceptions, and utilities
- **Test Location**: Unit tests must be placed in
  `src/test/unit/java/com/recipe_manager/unit_tests/` following the package
  structure
- **Test Naming**: Test classes must be named `{ClassName}Test.java`
- **Test Tags**: All unit tests must include `@Tag("unit")` annotation
- **Coverage**: Each new file must achieve minimum 90% test coverage before
  moving to next file

### Annotation Processing

- **Lombok**: Must be first in annotation processor chain
- **MapStruct**: Configured to work with Lombok-generated code
- **Processing Order**: Lombok → MapStruct → Spring processors

## Configuration Management

### Application Profiles

- **Default**: Development configuration with debug logging
- **Production**: Use `application-prod.yml` with environment variables
- **Test**: Separate `application-test.yml` for testing

### Key Configuration Areas

- **Database**: HikariCP connection pooling with leak detection
- **Security**: JWT configuration and service-to-service auth
- **Monitoring**: Actuator endpoints, Prometheus metrics, structured logging
- **Caching**: Caffeine cache with 10-minute TTL

## Monitoring and Observability

### Available Endpoints

- **Health**: `/actuator/health` (with detailed components)
- **Metrics**: `/actuator/prometheus` (Prometheus format)
- **Info**: `/actuator/info` (application information)

### Logging

- **Structured Logging**: JSON format with correlation IDs
- **Request Tracking**: `X-Request-ID` header propagation
- **Log Levels**: Configurable per package, debug enabled for development

## Development Workflow

### Before Committing

1. Run `mvn clean package` to ensure build passes
2. Verify code formatting: `mvn spotless:check`
3. Check static analysis: `mvn checkstyle:check spotbugs:check pmd:check`
4. Ensure tests pass: `mvn test`
5. Verify coverage: `mvn jacoco:report`

### Adding New Features

1. Create JPA entities in `model/entity/`
2. Add repositories in `repository/`
3. Implement services in `service/`
4. Create DTOs and mappers in `model/dto/` and `model/mapper/`
5. Add REST controllers in `controller/`
6. Write comprehensive tests for all layers

### Common Issues

- **MapStruct + Lombok**: Ensure Lombok is first in annotation processor
  chain
- **Database Schema**: Use `POSTGRES_SCHEMA` environment variable
  consistently
- **JWT Authentication**: With OAuth2 introspection enabled, no shared
  JWT_SECRET needed
- **Test Containers**: May require Docker daemon for component/integration
  tests
- **Spotless Formatting**: Run automatically during validate phase, but may
  need explicit `mvn spotless:apply` for formatting issues

## External Service Dependencies

This service integrates with external services via Spring Cloud OpenFeign:

- **Media Management Service**: Handles recipe media upload, storage, and deletion
- **Recipe Scraper Service**: Scrapes recipes from external URLs
- **User Management Service**: JWT token generation and validation (OAuth2 introspection)

Feign clients are configured with:

- Resilience4j circuit breakers for fault tolerance
- Custom error decoders for proper exception handling
- Request/response logging for debugging

## Service Integration Patterns

- **Circuit Breaker**: Automatic failover when external services are unavailable
- **Retry Logic**: Configurable retry policies for transient failures
- **Timeout Configuration**: Request timeouts to prevent hanging operations
- **Error Handling**: Custom exceptions mapped from HTTP status codes
