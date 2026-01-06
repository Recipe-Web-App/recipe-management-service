# Contributing to Recipe Management Service

Thank you for your interest in contributing! This document provides guidelines
and instructions for contributing to this project.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Development Workflow](#development-workflow)
- [Testing](#testing)
- [Code Style](#code-style)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)
- [Security](#security)

## Code of Conduct

This project adheres to a Code of Conduct. By participating, you are expected
to uphold this code. Please report unacceptable behavior through the project's
issue tracker.

## Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:

   ```bash
   git clone https://github.com/YOUR_USERNAME/recipe-management-service.git
   cd recipe-management-service
   ```

3. **Add upstream remote**:

   ```bash
   git remote add upstream https://github.com/Recipe-Web-App/recipe-management-service.git
   ```

## Development Setup

### Prerequisites

- Java 25 (OpenJDK or Eclipse Temurin)
- Maven 3.9+
- Docker and Docker Compose
- PostgreSQL 14+ (for local development)
- Git
- Your favorite Java IDE (IntelliJ IDEA, Eclipse, VS Code)

### Initial Setup

1. **Build the project**:

   ```bash
   mvn clean install
   ```

2. **Set up environment**:

   ```bash
   cp .env.example .env.local
   # Edit .env.local with your local configuration
   ```

3. **Start development environment**:

   ```bash
   docker-compose up -d
   ```

4. **Run database migrations**:

   ```bash
   mvn flyway:migrate
   ```

5. **Run the service**:

   ```bash
   mvn spring-boot:run
   ```

## Development Workflow

1. **Create a feature branch**:

   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes** following the code style guidelines

3. **Run tests frequently**:

   ```bash
   mvn test
   ```

4. **Commit your changes** following commit guidelines

5. **Keep your branch updated**:

   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

6. **Push to your fork**:

   ```bash
   git push origin feature/your-feature-name
   ```

7. **Create a Pull Request** on GitHub

## Testing

This project uses a multi-layered testing strategy as defined in CLAUDE.md.

### Running Tests

```bash
# All tests
mvn test

# Unit tests only (85% coverage requirement)
mvn test -Dgroups="unit"

# Component tests (with Testcontainers)
mvn test -Dgroups="component"

# Integration tests (Karate API tests)
mvn test -Dgroups="dependency"

# Performance tests (JMeter)
mvn verify

# Single test class
mvn test -Dtest=RecipeServiceTest
```

### Test Coverage

```bash
# Generate coverage report
mvn test jacoco:report

# View report at: target/site/jacoco/index.html
```

### Test Requirements

- **Unit tests**: Required for all new code, 85% coverage minimum
- **Component tests**: Required for service layer changes
- **Integration tests**: Required for API endpoint changes
- **Performance tests**: Required for performance-critical features

### Test Guidelines

- Write tests before or alongside code (TDD encouraged)
- Use Datafaker for realistic test data
- Follow the AbstractComponentTest pattern for component tests
- Mock repositories, use real services in component tests
- Include edge cases and error scenarios
- Test security implications

## Code Style

This project enforces strict code quality standards.

### Formatting

```bash
# Check formatting
mvn spotless:check

# Apply formatting
mvn spotless:apply
```

### Code Quality Checks

```bash
# Run all quality checks
mvn checkstyle:check spotbugs:check pmd:check

# Individual checks
mvn checkstyle:check  # Google Java Style
mvn spotbugs:check    # Bug detection
mvn pmd:check         # Code analysis
```

### Style Guidelines

- Follow Google Java Style Guide
- Use meaningful variable and method names
- Add JavaDoc comments for public APIs
- Keep methods small and focused
- Avoid code duplication (DRY principle)
- Use Spring Boot best practices
- Follow JPA/Hibernate best practices

### IDE Setup

**IntelliJ IDEA:**

- Install Google Java Format plugin
- Import code style from `.editorconfig`
- Enable "Optimize imports on the fly"

**Eclipse:**

- Install Eclipse Code Formatter
- Import formatter settings from project

## Commit Guidelines

This project follows [Conventional Commits](https://www.conventionalcommits.org/).

### Commit Message Format

```text
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code formatting (no functional changes)
- `refactor`: Code refactoring
- `perf`: Performance improvements
- `test`: Adding or updating tests
- `chore`: Build/tooling changes
- `security`: Security fixes
- `deps`: Dependency updates

### Examples

```bash
feat(recipe): add ingredient substitution feature

Implemented ingredient substitution logic that suggests alternatives
based on dietary restrictions and availability.

Fixes #123
```

```bash
fix(shopping-list): resolve duplicate ingredient aggregation

Fixed bug where ingredients from multiple recipes were not properly
aggregated, causing duplicates in shopping lists.

Closes #456
```

## Pull Request Process

### Before Creating a PR

1. **Ensure all tests pass**:

   ```bash
   mvn clean verify
   ```

2. **Verify code quality**:

   ```bash
   mvn spotless:apply
   mvn checkstyle:check spotbugs:check pmd:check
   ```

3. **Check test coverage**:

   ```bash
   mvn jacoco:report
   # Ensure coverage is >= 85%
   ```

4. **Update documentation**:
   - README.md for user-facing changes
   - CLAUDE.md for developer changes
   - JavaDoc for API changes

### PR Requirements

- [ ] All tests pass
- [ ] Code coverage >= 85%
- [ ] All code quality checks pass
- [ ] No merge conflicts
- [ ] Documentation updated
- [ ] Follows coding standards
- [ ] Commit messages follow conventional commits
- [ ] PR template filled out completely

### PR Template

Use the provided PR template to ensure all necessary information is included:

- Description of changes
- Type of change
- Related issues
- Security impact
- Breaking changes
- Testing performed
- Configuration changes
- Database migrations
- Performance impact
- Deployment notes

### Review Process

1. **Automated Checks**: CI pipeline runs all tests and quality checks
2. **Code Review**: At least one maintainer must approve
3. **Security Review**: For security-related changes
4. **Testing Verification**: Reviewer validates test coverage
5. **Documentation Review**: Ensure docs are complete and clear

### After PR is Merged

- Delete your feature branch
- Pull the latest changes from main
- Close related issues if not auto-closed

## Security

### Reporting Vulnerabilities

**DO NOT report security vulnerabilities through public issues!**

Use
[GitHub Security Advisories](https://github.com/Recipe-Web-App/recipe-management-service/security/advisories/new)
for sensitive security issues.

### Security Guidelines

- Never commit secrets, tokens, or passwords
- Use environment variables for sensitive configuration
- Follow OWASP best practices
- Validate all user inputs
- Use parameterized queries (JPA prevents SQL injection)
- Implement proper authentication and authorization
- Keep dependencies up to date
- Run security scanners before submitting PRs

### Security Checklist

Before submitting a PR with security implications:

- [ ] No hardcoded credentials
- [ ] Sensitive data is encrypted
- [ ] Input validation implemented
- [ ] Authorization checks in place
- [ ] Security tests added
- [ ] Dependencies scanned for vulnerabilities
- [ ] Error messages don't leak sensitive info

## Questions?

- **Issues**: For bugs and features, use GitHub Issues
- **Discussions**: For questions and ideas, use GitHub Discussions
- **Security**: For security concerns, use Security Advisories
- **Documentation**: Check README.md and CLAUDE.md first

Thank you for contributing to Recipe Management Service! 🎉
