# Support

Need help with Recipe Management Service? This document will guide you to the
right resources.

## Documentation

Start with our comprehensive documentation:

- **[README.md](../README.md)** - Overview, features, and quick start guide
- **[CLAUDE.md](../CLAUDE.md)** - Development guide, architecture, and build instructions
- **[API Documentation](../README.md#api-endpoints)** - REST API reference
- **[CONTRIBUTING.md](CONTRIBUTING.md)** - How to contribute to the project

## Getting Help

Choose the appropriate channel based on your needs:

### 🐛 **Bug Reports**

Found a bug? [Create a Bug Report](https://github.com/Recipe-Web-App/recipe-management-service/issues/new?template=bug_report.yml)

### 💡 **Feature Requests**

Have an idea? [Submit a Feature Request](https://github.com/Recipe-Web-App/recipe-management-service/issues/new?template=feature_request.yml)

### 🔒 **Security Issues**

Security vulnerability? **Do not create a public issue!** Use [Security Advisories](https://github.com/Recipe-Web-App/recipe-management-service/security/advisories/new)

### ❓ **Questions & Discussions**

General questions? Use [GitHub Discussions](https://github.com/Recipe-Web-App/recipe-management-service/discussions)

### 📚 **Documentation Issues**

Documentation unclear? [Report Documentation Issue](https://github.com/Recipe-Web-App/recipe-management-service/issues/new?template=documentation.yml)

### ⚡ **Performance Issues**

Performance problems? [Create Performance Issue](https://github.com/Recipe-Web-App/recipe-management-service/issues/new?template=performance_issue.yml)

## GitHub Discussions

Use
[Discussions](https://github.com/Recipe-Web-App/recipe-management-service/discussions)
for:

- **Q&A** - Ask questions about using the service
- **Ideas** - Discuss potential features before creating issues
- **Show and Tell** - Share your implementations and integrations
- **General** - Community conversations

**Before asking:**

1. Search existing discussions
2. Check the documentation
3. Review closed issues

## GitHub Issues

Use
[Issues](https://github.com/Recipe-Web-App/recipe-management-service/issues)
for:

- Bug reports (with reproduction steps)
- Feature requests (with use cases)
- Performance issues (with metrics)
- Documentation improvements

**Issue Guidelines:**

- Use appropriate templates
- Provide complete information
- Include reproduction steps
- Add relevant logs (redact secrets!)
- Search for duplicates first

## Common Questions

### Setup & Configuration

**Q: How do I set up the development environment?**

A: See [CONTRIBUTING.md](CONTRIBUTING.md#development-setup). You'll need
Java 21, Maven, PostgreSQL, and Docker.

**Q: What environment variables are required?**

A: Check [CLAUDE.md](../CLAUDE.md#environment-variables) for the complete
list. Key variables:

- `POSTGRES_HOST`, `POSTGRES_PORT`, `POSTGRES_DB`
- `RECIPE_MANAGEMENT_DB_USER`, `RECIPE_MANAGEMENT_DB_PASSWORD`
- `JWT_SECRET` (if OAuth2 introspection disabled)

**Q: How do I run database migrations?**

A: Migrations run automatically on startup via Flyway. Manual execution: `mvn flyway:migrate`

### Testing

**Q: How do I run tests?**

A:

```bash
# All tests
mvn test

# Unit tests only
mvn test -Dgroups="unit"

# Component tests
mvn test -Dgroups="component"

# Integration tests
mvn test -Dgroups="dependency"
```

**Q: Why are my component tests failing?**

A: Component tests use Testcontainers which requires Docker. Ensure Docker is
running. Check logs for port conflicts or container startup issues.

**Q: How do I check test coverage?**

A: Run `mvn jacoco:report` and open `target/site/jacoco/index.html`. Minimum
coverage is 90%.

### Build & Deployment

#### Q: Build fails with "package does not exist" error

A: Run `mvn clean install` to regenerate MapStruct mappers. Ensure Lombok is
processed before MapStruct.

**Q: How do I build a Docker image?**

A: `docker build -t recipe-management-service:latest .`

**Q: How do I deploy to Kubernetes?**

A: Apply manifests: `kubectl apply -f k8s/`. Ensure ConfigMaps and Secrets
are created first.

### Database

**Q: How do I handle database migrations?**

A: Create SQL files in `src/main/resources/db/migration/` following Flyway
naming: `V{version}__{description}.sql`

#### Q: PostgreSQL connection fails

A: Check:

- PostgreSQL is running
- Credentials are correct
- `POSTGRES_SCHEMA` matches (default: recipe_manager)
- Network connectivity and firewall rules

#### Q: How do I reset the database?

A: Development only:

```bash
mvn flyway:clean
mvn flyway:migrate
```

### API

#### Q: JWT authentication fails

A: Ensure:

- JWT_SECRET is set (if not using OAuth2 introspection)
- Token is valid and not expired
- User-management-service is accessible
- OAuth2 introspection is configured correctly

#### Q: How do I test API endpoints?

A: Use integration tests (Karate) in `src/test/dependency/` or tools like
Postman/curl.

### Code Quality

#### Q: Spotless/Checkstyle errors

A: Run `mvn spotless:apply` to auto-format. Then verify with
`mvn checkstyle:check`.

#### Q: SpotBugs warnings

A: Review warnings in `target/spotbugsXml.xml`. Fix issues or add
`@SuppressFBWarnings` with justification.

#### Q: PMD warnings

A: See `target/pmd.xml`. Fix violations or suppress with
`@SuppressWarnings("PMD.RuleName")`.

## Response Times

**Expected Response Times:**

- 🔴 **Critical Security Issues**: 24-48 hours
- 🟠 **High Priority Bugs**: 2-5 business days
- 🟡 **Feature Requests**: 1-2 weeks for initial review
- 🟢 **Questions**: 3-7 days (community-driven)
- ⚪ **Documentation**: 1-2 weeks

*These are targets, not guarantees. Response times depend on maintainer availability.*

## Bug Report Best Practices

Help us help you by providing:

1. **Clear Description**: Concise summary of the issue
2. **Reproduction Steps**: Exact steps to reproduce
3. **Expected Behavior**: What should happen
4. **Actual Behavior**: What actually happens
5. **Environment**: Java version, OS, deployment type
6. **Logs**: Relevant logs (redact secrets!)
7. **Screenshots**: If applicable
8. **Minimal Example**: Simplified code demonstrating the issue

**Good Bug Report Example:**

```text
Title: NullPointerException in Recipe Search with Empty Query

Description:
The /api/v1/recipes/search endpoint throws NullPointerException when
query parameter is empty string instead of null.

Steps to Reproduce:
1. POST /api/v1/recipes/search
2. Body: { "query": "", "limit": 10 }
3. Observe 500 error

Expected: Empty results array []
Actual: 500 Internal Server Error with NPE

Environment:
- Version: 1.2.3
- Java: 21 (Temurin)
- Deployment: Docker
- PostgreSQL: 15.2

Logs:
[paste sanitized stack trace]
```

## Additional Resources

### External Documentation

- **Spring Boot**: <https://spring.io/projects/spring-boot>
- **JPA/Hibernate**: <https://hibernate.org/orm/documentation>
- **PostgreSQL**: <https://www.postgresql.org/docs/>
- **MapStruct**: <https://mapstruct.org/documentation/>
- **Maven**: <https://maven.apache.org/guides/>

### Related Projects

- **user-management-service**: Authentication and user management
- **Recipe-Web-App**: Organization repositories

### Community

- **GitHub Discussions**: Community Q&A and ideas
- **Issue Tracker**: Bug reports and feature requests
- **Pull Requests**: Code contributions

## Still Need Help?

If you've:

- ✅ Checked the documentation
- ✅ Searched existing issues and discussions
- ✅ Reviewed common questions above

And still need help:

1. **Ask in Discussions**: For usage questions
2. **Create an Issue**: For bugs or features (use templates!)
3. **Reach Out**: Contact @jsamuelsen11 for project-specific questions

**Remember**:

- Be respectful and patient
- Provide context and details
- Help others when you can
- Follow our [Code of Conduct](CODE_OF_CONDUCT.md)

Thank you for using Recipe Management Service! 🍽️
