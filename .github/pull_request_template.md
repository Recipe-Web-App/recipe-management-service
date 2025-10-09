# Pull Request

## Description

<!-- Provide a clear and concise description of the changes in this PR -->

## Type of Change

<!-- Mark the relevant option with an 'x' -->

- [ ] Bug fix (non-breaking change which fixes an issue)
- [ ] New feature (non-breaking change which adds functionality)
- [ ] Breaking change (fix or feature that would cause existing
      functionality to not work as expected)
- [ ] Performance improvement
- [ ] Refactoring (no functional changes)
- [ ] Documentation update
- [ ] Security fix
- [ ] Dependency update
- [ ] Configuration change
- [ ] Other (please describe):

## Related Issues

<!--
Link to related issues using keywords:
Fixes #123, Resolves #456, Related to #789
-->

Fixes #

## Changes Made

<!-- List the specific changes made in this PR -->

-
-
-

## Recipe Service/Security Impact

<!-- Does this change affect authentication, authorization, or data security? -->

- [ ] This PR affects JWT authentication
- [ ] This PR affects authorization/permissions
- [ ] This PR has security implications
- [ ] This PR handles sensitive data (recipes, user data)
- [ ] No security impact

<!-- If checked, please describe the security implications -->

## Breaking Changes

<!-- List any breaking changes and migration steps -->

- [ ] This PR introduces breaking changes

<!-- If checked, describe the breaking changes and how users should migrate -->

## Testing

### Test Coverage

- [ ] Unit tests added/updated (90% coverage requirement)
- [ ] Component tests added/updated (Testcontainers)
- [ ] Integration tests added/updated (Karate API tests)
- [ ] Performance tests added/updated (JMeter)
- [ ] Manual testing performed
- [ ] All existing tests pass

### Test Details

<!-- Describe the testing you performed -->

**Manual Testing:**

- <!-- Add manual testing details -->

**Automated Tests:**

- <!-- Add automated test details -->

**Test Coverage:**

- Current coverage:
- Coverage change:

## Configuration Changes

<!-- Are there new environment variables or configuration options? -->

- [ ] New environment variables added
- [ ] application.yml/application-prod.yml changed
- [ ] Configuration defaults changed
- [ ] No configuration changes

<!-- If checked, list the new/changed configuration -->

**New Configuration:**

```yaml
# Add environment variables or application.yml changes here
```

## Database/Storage Changes

<!-- Does this affect the database schema or PostgreSQL configuration? -->

- [ ] Database schema changes (Flyway migration required)
- [ ] New Flyway migration added
- [ ] JPA entity changes
- [ ] Repository/query changes
- [ ] Connection pooling configuration
- [ ] No database/storage changes

<!-- If checked, describe the migration path -->

**Migration Details:**

<!-- Describe database changes and migration steps -->

## Performance Impact

<!-- Has performance been tested? Are there any impacts? -->

- [ ] Performance tested with JMeter
- [ ] No performance impact expected
- [ ] Performance improvement (provide metrics)
- [ ] Potential performance degradation (explained below)

<!-- If there's a performance impact, provide details -->

**Performance Metrics:**

<!-- JMeter results, query performance, response times -->

## Deployment Notes

<!-- Any special deployment considerations? -->

- [ ] Requires database migration (run Flyway migrations)
- [ ] Requires configuration changes
- [ ] Requires service restart
- [ ] Requires dependency updates
- [ ] Safe to deploy with rolling update
- [ ] Requires Kubernetes manifest updates
- [ ] Standard deployment

<!-- Provide deployment instructions if needed -->

**Deployment Steps:**

1. <!-- Step 1 -->
2. <!-- Step 2 -->

## Documentation

<!-- Has documentation been updated? -->

- [ ] README.md updated
- [ ] CLAUDE.md updated
- [ ] JavaDoc comments added/updated
- [ ] API documentation updated
- [ ] Database schema documentation updated
- [ ] No documentation needed

## Checklist

<!-- Ensure all items are completed before requesting review -->

- [ ] Code follows the project's style guidelines (`mvn spotless:check`)
- [ ] Code formatted with Spotless (`mvn spotless:apply`)
- [ ] Self-review of code performed
- [ ] Code commented, particularly in hard-to-understand areas
- [ ] No new warnings from Checkstyle (`mvn checkstyle:check`)
- [ ] No new warnings from SpotBugs (`mvn spotbugs:check`)
- [ ] No new warnings from PMD (`mvn pmd:check`)
- [ ] All tests pass locally (`mvn test`)
- [ ] Code coverage meets 90% threshold (`mvn jacoco:report`)
- [ ] No sensitive information (secrets, keys, tokens) committed
- [ ] Commit messages follow conventional commit format
- [ ] Branch is up to date with target branch

## Screenshots/Logs (if applicable)

<!-- Add screenshots, logs, or other visual aids -->

## Additional Context

<!-- Add any additional context, concerns, or notes for reviewers -->

## Reviewer Notes

<!-- Specific areas where you'd like reviewer focus -->

Please pay special attention to:

- <!-- Add areas of focus -->
- <!-- Add areas of focus -->

---

**For Reviewers:**

- [ ] Code review completed
- [ ] Security implications reviewed
- [ ] Test coverage is adequate
- [ ] Documentation is clear and complete
