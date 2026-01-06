# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with
code in this repository.

## Build and Development Commands

### Essential Commands

- **Build**: `mvn clean package`
- **Run**: `java -jar target/recipe-management-service-*.jar` or `mvn spring-boot:run`
- **Format Code**: `mvn spotless:apply`
- **Code Quality**: `mvn checkstyle:check spotbugs:check pmd:check`
- **Tests**: `mvn test` (all tests)
- **Coverage**: `mvn jacoco:report` (generates report in `target/site/jacoco/index.html`)

### Test Commands

- **Unit Tests**: `mvn test -Dgroups="unit"`
- **Component Tests**: `mvn test -Dgroups="component"`
- **Integration Tests**: `mvn test -Dgroups="dependency"`
- **Performance Tests**: `mvn jmeter:jmeter` (excluded from default build)
- **Single Test**: `mvn test -Dtest=ClassName`
- **Single Test Method**: `mvn test -Dtest=ClassName#methodName`

### Docker and Deployment

- **Docker Build**: `docker build -t recipe-management-service:latest .`
- **Kubernetes Deploy**: `kubectl apply -f k8s/`
- **Container Scripts**: Available in `scripts/containerManagement/`

## Architecture Overview

### Technology Stack

- **Java 25** with Spring Boot 4.0.1
- **PostgreSQL 15+** database with Flyway migrations
- **Maven** build system with comprehensive quality plugins
- **JWT authentication** with OAuth2 introspection support
- **Spring Cloud OpenFeign** for external service integration (media-management, recipe-scraper)
- **Resilience4j** for circuit breakers and fault tolerance
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
- **Coverage Target**: 85% minimum line coverage (enforced by JaCoCo)
- **Test Data**: Use Datafaker for realistic test data generation

### Component Test Guidelines

Component tests exercise real service logic with mocked repositories. The
`AbstractComponentTest` base class provides helper methods to switch between
mocked and real services:

- **`useRealRecipeService()`** - Uses real RecipeService with mocked repositories
- **`useRealIngredientService()`** - Uses real IngredientService with mocked repositories
- **`useRealStepService()`** - Uses real StepService with mocked repositories
- **`useRealTagService()`** - Uses real TagService with mocked repositories

**DO**: Mock repositories, use real services and mappers
**DON'T**: Mock service layer in component tests

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
- **Code Coverage**: 85% minimum line coverage (enforced by JaCoCo)
- **Documentation**: Javadoc required for public APIs
- **Line Length**: 100 characters maximum
- **Indentation**: 2 spaces (configured in .editorconfig)

### Unit Test Requirements

- **MANDATORY**: Every new file created must have corresponding unit tests
- **Test Location**: `src/test/unit/java/com/recipe_manager/unit_tests/`
- **Test Naming**: `{ClassName}Test.java`
- **Test Tags**: All unit tests must include `@Tag("unit")` annotation
- **Coverage**: Minimum 85% line coverage enforced by JaCoCo

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

1. Run `mvn clean package` (includes formatting, quality checks, and tests)
2. Verify coverage: `mvn jacoco:report`

### Adding New Features

1. Create JPA entities in `model/entity/`
2. Add Flyway migration in `src/main/resources/db/migration/`
3. Add repositories in `repository/`
4. Implement services in `service/`
5. Create DTOs and mappers in `model/dto/` and `model/mapper/`
6. Add REST controllers in `controller/`
7. Write unit tests (mandatory) and component tests

### Common Issues

- **MapStruct + Lombok**: Lombok must be first in annotation processor chain (already configured in pom.xml)
- **Database Schema**: Use `POSTGRES_SCHEMA` environment variable consistently
- **JWT Authentication**: With OAuth2 introspection enabled, no shared JWT_SECRET needed
- **Test Containers**: Requires Docker daemon for component/integration tests
- **Spotless Formatting**: Run `mvn spotless:apply` to fix formatting issues

## External Service Dependencies

Feign clients in `client/` integrate with external services:

- **Media Management Service** (`mediamanager/`): Recipe media upload, storage, deletion
- **Recipe Scraper Service** (`recipescraper/`): Scrapes recipes from external URLs
- **User Management Service**: JWT validation via OAuth2 introspection

All clients use Resilience4j circuit breakers with custom error decoders.
