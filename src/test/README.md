# Recipe Management Service - Test Framework

This directory contains a comprehensive test framework for the Recipe Management
Service, implementing multiple testing strategies to ensure code quality,
reliability, and performance.

## Test Structure

```text
src/test/
├── unit/                    # Unit tests with mocked dependencies
│   └── java/com/recipe_manager/
│       ├── config/          # Configuration tests
│       ├── controller/      # Controller tests
│       ├── exception/       # Exception handler tests
│       ├── model/           # DTO and model tests
│       ├── security/        # Security component tests
│       ├── service/         # Service layer tests
│       └── util/            # Utility class tests
├── component/               # Component tests with Spring context
│   └── java/com/recipe_manager/component_tests/
│       ├── ingredient_service/
│       ├── media_service/
│       ├── recipe_service/
│       ├── review_service/
│       ├── step_service/
│       └── tag_service/
├── dependency/              # Integration tests with real dependencies
│   ├── java/com/recipe_manager/dependency_tests/
│   │   ├── ActuatorKarateRunner.java
│   │   ├── IngredientKarateRunner.java
│   │   ├── MediaKarateRunner.java
│   │   ├── RecipeKarateRunner.java
│   │   ├── ReviewKarateRunner.java
│   │   ├── StepKarateRunner.java
│   │   └── TagKarateRunner.java
│   └── resources/com/recipe_manager/dependency_tests/feature/
│       ├── actuator/        # Actuator endpoint tests
│       ├── ingredient/      # Ingredient API tests
│       ├── media/           # Media API tests
│       ├── recipe/          # Recipe API tests
│       ├── review/          # Review API tests
│       ├── step/            # Step API tests
│       └── tag/             # Tag API tests
├── performance/             # JMeter performance tests
│   └── resources/
│       ├── health-endpoint-performance.jmx
│       └── performance-config.properties
└── resources/
    ├── application-test.yml # Test application configuration
    └── karate-config.js    # Karate test configuration
```

## Test Types

### 1. Unit Tests (`src/test/unit/`)

- **Purpose**: Test individual methods in isolation
- **Dependencies**: All external dependencies are mocked
- **Framework**: JUnit 5 + Mockito + AssertJ
- **Speed**: Fastest execution
- **Coverage**: Method-level logic testing

**Example Usage:**

```java
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class RecipeServiceTest {
    @Mock
    private RecipeRepository repository;

    @InjectMocks
    private RecipeService service;

    @Test
    @DisplayName("Should create recipe successfully")
    @Tag("standard")
    void shouldCreateRecipe() {
        // Given
        Recipe recipe = new Recipe("Test Recipe");
        when(repository.save(any())).thenReturn(recipe);

        // When
        Recipe result = service.createRecipe(recipe);

        // Then
        assertThat(result.getName()).isEqualTo("Test Recipe");
        verify(repository).save(recipe);
    }
}
```

### 2. Component Tests (`src/test/component/`)

- **Purpose**: Test Spring-managed components with in-memory database
- **Dependencies**: H2 database, mocked external services
- **Framework**: Spring Boot Test + Testcontainers
- **Speed**: Medium execution
- **Coverage**: Component integration and business logic testing

**Example Usage:**

```java
@SpringBootTest
@Testcontainers
@Tag("component")
class RecipeServiceComponentTest extends AbstractComponentTest {
    @Test
    @DisplayName("Should create recipe with valid data")
    @Tag("standard")
    void shouldCreateRecipeWithValidData() {
        // Test with real database but mocked external services
    }
}
```

### 3. Dependency Tests (`src/test/dependency/`)

- **Purpose**: Test with real dependencies and external systems
- **Dependencies**: Testcontainers (PostgreSQL), real HTTP calls
- **Framework**: Karate for API testing
- **Speed**: Slower execution
- **Coverage**: End-to-end integration and API contract testing

**Example Usage:**

```java
@Tag("dependency")
class RecipeKarateRunner {
    @Karate.Test
    @DisplayName("Create Recipe Endpoint")
    Karate testCreateRecipe() {
        return Karate.run("feature/recipe/create-recipe.feature").relativeTo(getClass());
    }
}
```

**Karate Feature Example:**

```gherkin
Feature: Recipe Management

  Scenario: Create recipe successfully
    Given url baseUrl + '/api/v1/recipe-management/recipe-management/recipes'
    And header Authorization = 'Bearer ' + token
    When method POST
    Then status 201
    And match response contains { id: '#string', name: '#string' }
```

### 4. Performance Tests (`src/test/performance/`)

- **Purpose**: Load testing and performance validation
- **Framework**: JMeter
- **Coverage**: Response times, throughput, error rates

**Example Usage:**

```bash
mvn verify  # Runs JMeter tests during verify phase
```

## Running Tests

### All Tests

```bash
mvn test
```

### Unit Tests Only

```bash
mvn test -Dtest="*Test" -Dgroups="unit"
```

### Component Tests Only

```bash
mvn test -Dtest="*Test" -Dgroups="component"
```

### Dependency Tests Only

```bash
mvn test -Dtest="*KarateRunner" -Dgroups="dependency"
```

### Specific Test Categories

```bash
# Standard test cases
mvn test -Dgroups="standard"

# Error processing test cases
mvn test -Dgroups="error-processing"

# Performance tests
mvn verify  # Runs JMeter tests
```

## Test Configuration

### Unit Tests

- Use `@Tag("unit")` for unit test identification
- Use `@Tag("standard")` for normal scenarios
- Use `@Tag("error-processing")` for error scenarios
- Mock all external dependencies with `@Mock`
- Use `@InjectMocks` for dependency injection

### Component Tests

- Extend `AbstractComponentTest` for common setup
- Use `@Tag("component")` for component test identification
- Use Testcontainers for database testing
- Test business logic with real database but mocked external services

### Dependency Tests

- Use Karate framework for API testing
- Use `@Tag("dependency")` for dependency test identification
- Test with real HTTP calls to deployed application
- Validate API contracts and integration points

### Performance Tests

- JMeter test plans in `src/test/performance/resources/`
- Configuration in `performance-config.properties`
- Executed during Maven `verify` phase

## Test Data Management

### Faker Integration

```java
Faker faker = new Faker();
String recipeName = faker.food().dish();
String ingredientName = faker.food().ingredient();
```

### Test Constants

```java
public static final class TestConstants {
    public static final String TEST_USER_ID = "test-user-123";
    public static final String TEST_RECIPE_ID = "recipe-456";
}
```

## Reporting

### Allure Reports

Generate detailed test reports:

```bash
mvn allure:report
mvn allure:serve
```

### JaCoCo Coverage

View code coverage:

```bash
mvn jacoco:report
```

## Best Practices

### 1. Test Naming

- Use descriptive test names: `shouldReturnRecipeWhenValidIdProvided()`
- Follow Given-When-Then structure
- Use `@DisplayName` for readable test descriptions

### 2. Test Isolation

- Each test should be independent
- Clean up test data after each test
- Use `@BeforeEach` and `@AfterEach` for setup/teardown

### 3. Tagging Strategy

- `@Tag("unit")` - Unit tests
- `@Tag("component")` - Component tests
- `@Tag("dependency")` - Dependency tests
- `@Tag("standard")` - Normal scenarios
- `@Tag("error-processing")` - Error scenarios

### 4. Assertions

- Use AssertJ for fluent assertions
- Test one thing per test method
- Include both positive and negative test cases

### 5. Performance Testing

- JMeter test plans for load testing
- Monitor response times and error rates
- Test under various load conditions

## Continuous Integration

### GitHub Actions

```yaml
- name: Run Unit Tests
  run: mvn test -Dgroups="unit"
- name: Run Component Tests
  run: mvn test -Dgroups="component"
- name: Run Dependency Tests
  run: mvn test -Dgroups="dependency"
- name: Run Performance Tests
  run: mvn verify
- name: Generate Reports
  run: mvn allure:report
```

## Troubleshooting

### Common Issues

1. **Test Database Connection**

   - Ensure Testcontainers is properly configured
   - Check container startup logs

2. **Karate Test Failures**

   - Verify application is running on correct port
   - Check feature file syntax and paths
   - Ensure authentication is properly configured

3. **Performance Test Issues**
   - Check JMeter test plan syntax
   - Verify target application is accessible
   - Review performance thresholds

### Debug Mode

```bash
mvn test -Dspring.profiles.active=test -Dlogging.level.com.recipe_manager=DEBUG
```

## Additional Tools

### Test Data Generation

- **Datafaker**: Generate realistic test data
- **JSON Schema Validator**: Validate API responses
- **WireMock**: Mock external HTTP services

### Performance Monitoring

- **JMeter**: Load testing and performance validation
- **Allure**: Detailed test reporting and analysis

### Code Quality

- **JaCoCo**: Code coverage reporting
- **AssertJ**: Fluent assertion library
- **Mockito**: Mocking framework

## Contributing

When adding new tests:

1. Follow the existing test structure and naming conventions
2. Use appropriate test configuration classes
3. Include both positive and negative test cases
4. Add proper documentation and comments
5. Ensure tests are fast and reliable
6. Use appropriate tags for test categorization
7. Update this README if adding new test types
