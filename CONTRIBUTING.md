# Contributing to Recipe Manager Service

We love your input! We want to make contributing to Recipe Manager Service
as easy and transparent as possible, whether it's:

- Reporting a bug
- Discussing the current state of the code
- Submitting a fix
- Proposing new features
- Becoming a maintainer

## Development Process

We use GitHub to host code, to track issues and feature requests, as well as
accept pull requests.

## Pull Requests

Pull requests are the best way to propose changes to the codebase. We
actively welcome your pull requests:

1. Fork the repo and create your branch from `main`.
2. If you've added code that should be tested, add tests.
3. If you've changed APIs, update the documentation.
4. Ensure the test suite passes.
5. Make sure your code lints.
6. Issue that pull request!

## Setting Up Development Environment

### Prerequisites

- **Java 21+** (Eclipse Temurin recommended)
- **Maven 3.9+**
- **Docker & Docker Compose**
- **PostgreSQL 15+** (for local development)
- **Git**
- **VS Code** (recommended) with our extension pack

### Initial Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/your-org/recipe-manager-service.git
   cd recipe-manager-service
   ```

2. **Install VS Code extensions**

   ```bash
   # VS Code will prompt you to install recommended extensions
   code .
   ```

3. **Set up environment variables**

   ```bash
   cp .env.example .env
   # Edit .env with your local configuration
   ```

4. **Set up database**

   ```bash
   # Using Docker (recommended)
   docker run --name recipe-postgres \
     -e POSTGRES_DB=recipe_db \
     -e POSTGRES_USER=recipe_user \
     -e POSTGRES_PASSWORD=your_password \
     -p 5432:5432 \
     -d postgres:15
   ```

5. **Install pre-commit hooks**

   ```bash
   pip install pre-commit
   pre-commit install
   ```

6. **Build and test**

   ```bash
   mvn clean install
   ```

## Code Style & Standards

### Java Code Style

- We use **Google Java Style Guide** enforced by Spotless
- Maximum line length: **100 characters**
- Indentation: **2 spaces** (no tabs)
- Import order: `java`, `javax`, `org`, `com`

### Formatting

- Code is automatically formatted during Maven build (`mvn clean install`)
- VS Code formats on save
- Pre-commit hooks validate formatting

### Naming Conventions

- **Classes**: PascalCase (e.g., `RecipeService`)
- **Methods**: camelCase (e.g., `createRecipe`)
- **Variables**: camelCase (e.g., `recipeId`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_ATTEMPTS`)
- **Packages**: lowercase (e.g., `com.recipe_manager.service`)

## Testing Standards

### Test Structure

```text
src/test/
├── unit/          # Unit tests (isolated, mocked dependencies)
├── component/     # Component tests (Spring context, real database)
├── dependency/    # Integration tests (Karate, real HTTP calls)
└── performance/   # JMeter performance tests
```

### Test Requirements

- **Unit tests**: Required for all business logic
- **Component tests**: Required for service layers
- **Integration tests**: Required for API endpoints
- **Minimum coverage**: 90% (enforced by JaCoCo)

### Test Naming

```java
// Unit test example
@Test
@DisplayName("Should create recipe when valid data provided")
void shouldCreateRecipeWhenValidDataProvided() {
    // Given - When - Then
}
```

### Running Tests

```bash
# All tests
mvn test

# Specific test types
mvn test -P unit-tests
mvn test -P component-tests
mvn test -P dependency-tests

# Single test
mvn test -Dtest=RecipeServiceTest
```

## Git Workflow

### Branch Naming

- **Feature**: `feature/add-recipe-search`
- **Bug fix**: `fix/recipe-validation-error`
- **Hotfix**: `hotfix/security-vulnerability`
- **Documentation**: `docs/update-api-documentation`

### Commit Messages

We follow [Conventional Commits](https://conventionalcommits.org/):

```text
type(scope): description

[optional body]

[optional footer]
```

**Types**:

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or modifying tests
- `chore`: Maintenance tasks

**Examples**:

```text
feat(recipe): add search functionality with filters

fix(auth): resolve JWT token validation issue

docs(readme): update installation instructions
```

## Pull Request Process

### Before Submitting

1. **Rebase** your branch on the latest `main`
2. **Run tests**: `mvn clean test`
3. **Check formatting**: `mvn spotless:check`
4. **Run static analysis**: `mvn checkstyle:check spotbugs:check pmd:check`
5. **Update documentation** if needed

### PR Description Template

```markdown
## Summary
Brief description of changes

## Changes Made
- List of specific changes
- Another change

## Testing
- [ ] Unit tests added/updated
- [ ] Component tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing completed

## Documentation
- [ ] README updated (if needed)
- [ ] API documentation updated (if needed)
- [ ] Comments added for complex logic

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Tests pass locally
- [ ] No new warnings introduced
```

### Review Process

1. **Automated checks** must pass (CI/CD)
2. **Code review** by at least one maintainer
3. **Testing** in development environment
4. **Approval** and merge by maintainer

## Code Review Guidelines

### For Authors

- Keep PRs small and focused
- Provide clear descriptions
- Respond to feedback promptly
- Be open to suggestions

### For Reviewers

- Be constructive and respectful
- Focus on code quality, not personal preference
- Suggest improvements, don't just point out problems
- Approve when ready, don't perfectionism

## Architecture Guidelines

### Package Structure

```text
com.recipe_manager/
├── config/           # Configuration classes
├── controller/       # REST controllers
├── service/          # Business logic
├── repository/       # Data access
├── model/           # DTOs, entities, enums
├── security/        # Security components
├── exception/       # Exception handling
└── util/            # Utility classes
```

### Design Principles

- **Single Responsibility**: Each class has one reason to change
- **Dependency Injection**: Use Spring's DI container
- **Separation of Concerns**: Controllers, services, repositories have distinct roles
- **Fail Fast**: Validate inputs early
- **Immutability**: Prefer immutable objects where possible

## Database Guidelines

### Flyway Migrations

- **Location**: `src/main/resources/db/migration/`
- **Naming**: `V{version}__{description}.sql`
- **Example**: `V001__Create_recipe_table.sql`
- **Never modify existing migrations**

### SQL Standards

- Use **lowercase** with **underscores** for table/column names
- Add **indexes** for foreign keys and frequently queried columns
- Use **meaningful names** for constraints
- Include **comments** for complex logic

## Security Guidelines

### General Security

- Never commit secrets or credentials
- Use parameterized queries to prevent SQL injection
- Validate all input data
- Implement proper error handling (don't expose internal details)
- Use HTTPS for all communications

### Authentication & Authorization

- JWT tokens for stateless authentication
- Role-based access control (RBAC)
- Principle of least privilege
- Secure password handling with BCrypt

## Performance Guidelines

### Database

- Use connection pooling (HikariCP)
- Implement caching where appropriate (Caffeine)
- Optimize queries (use EXPLAIN ANALYZE)
- Use pagination for large result sets

### Application

- Avoid N+1 queries
- Use async processing for long-running tasks
- Implement proper logging (structured JSON)
- Monitor performance metrics

## Documentation Standards

### Code Documentation

- **JavaDoc** for public APIs
- **Inline comments** for complex logic
- **README updates** for significant changes
- **API documentation** using OpenAPI/Swagger

### API Documentation

- Document all endpoints
- Include request/response examples
- Document error codes and responses
- Keep documentation up-to-date with code changes

## Issue Reporting

### Bug Reports

Use the bug report template and include:

- Steps to reproduce
- Expected vs actual behavior
- Environment details (OS, Java version, etc.)
- Relevant logs or error messages

### Feature Requests

Use the feature request template and include:

- Use case description
- Proposed solution
- Alternative solutions considered
- Additional context

## Community Guidelines

### Code of Conduct

- Be respectful and inclusive
- Welcome newcomers and help them learn
- Focus on constructive feedback
- Assume positive intent

### Communication

- Use clear, concise language
- Ask questions when unsure
- Share knowledge and resources
- Collaborate openly

## Getting Help

- **Documentation**: Check README and docs/ directory
- **Issues**: Search existing issues before creating new ones
- **Discussions**: Use GitHub Discussions for questions
- **Discord/Slack**: Join our community chat (if available)

## License

By contributing, you agree that your contributions will be licensed under
the same license as the project (MIT License).

---

Thank you for contributing to Recipe Manager Service! 🎉
