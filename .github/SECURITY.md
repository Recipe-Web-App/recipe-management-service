# Security Policy

## Supported Versions

We release security updates for the following versions:

| Version | Supported          |
| ------- | ------------------ |
| latest  | :white_check_mark: |
| < latest| :x:                |

We recommend always running the latest version for security patches.

## Reporting a Vulnerability

**Please do not report security vulnerabilities through public GitHub issues.**

### Private Reporting (Preferred)

Report security vulnerabilities using [GitHub Security Advisories](https://github.com/Recipe-Web-App/recipe-management-service/security/advisories/new).

This allows us to:

- Discuss the vulnerability privately
- Develop and test a fix
- Coordinate disclosure timing
- Issue a CVE if necessary

### What to Include

When reporting a vulnerability, please include:

1. **Description** - Clear description of the vulnerability
2. **Impact** - What can an attacker achieve?
3. **Reproduction Steps** - Step-by-step instructions to reproduce
4. **Affected Components** - Which parts of the service are affected
5. **Suggested Fix** - If you have ideas for remediation
6. **Environment** - Version, configuration, deployment details
7. **Proof of Concept** - Code or requests demonstrating the issue (if safe to share)

### Example Report

```text
Title: SQL Injection in Recipe Search

Description: The recipe search endpoint does not properly sanitize user input...

Impact: An attacker can execute arbitrary SQL queries and access unauthorized data...

Steps to Reproduce:
1. Send POST request to /api/v1/recipes/search
2. Include malicious SQL in search query parameter
3. Observe unauthorized data access

Affected: RecipeService.java line 142

Suggested Fix: Use JPA criteria queries or validate input

Environment: v1.2.3, Docker deployment
```

## Response Timeline

- **Initial Response**: Within 48 hours
- **Status Update**: Within 7 days
- **Fix Timeline**: Varies by severity (critical: days, high: weeks, medium: months)

## Severity Levels

### Critical

- Remote code execution
- Authentication bypass
- Privilege escalation to admin
- Mass data exposure
- SQL injection with data access

### High

- JWT token forgery/manipulation
- Unauthorized access to user data
- Cross-site scripting (XSS)
- Denial of service affecting all users
- Sensitive data exposure

### Medium

- Information disclosure (limited)
- CSRF vulnerabilities
- Rate limiting bypass
- Session fixation
- Insecure dependencies

### Low

- Verbose error messages
- Security header issues
- Best practice violations
- Minor information leaks

## Security Features

This service implements multiple security layers:

### Authentication & Authorization

- **JWT Integration**: Integrates with user-management-service for authentication
- **Role-Based Access Control**: Spring Security with method-level authorization
- **OAuth2 Support**: Optional OAuth2 introspection for token validation

### Input Validation

- **Bean Validation**: JSR-380 validation on all DTOs
- **SQL Injection Prevention**: JPA/Hibernate parameterized queries
- **XSS Prevention**: Spring Security headers and content security policy
- **Path Traversal Protection**: Validated file paths and input sanitization

### Data Protection

- **PostgreSQL Security**: Schema isolation, connection pooling with HikariCP
- **Sensitive Data Handling**: No passwords or secrets in logs
- **Database Encryption**: TLS connections to PostgreSQL
- **Secure Configuration**: Environment variable based secrets

### API Security

- **CORS Configuration**: Strict origin validation
- **Rate Limiting**: Request throttling and abuse prevention
- **Security Headers**: HSTS, CSP, X-Frame-Options, etc.
- **Error Handling**: Generic error messages, no stack traces in production

### Dependency Security

- **OWASP Dependency Check**: Automated vulnerability scanning
- **Dependabot**: Automatic security updates
- **SBOM Generation**: Software Bill of Materials for compliance
- **License Compliance**: GPL/AGPL license blocking

## Security Best Practices

### For Operators

1. **Environment Variables**: Never commit secrets to version control
2. **Database Security**: Use strong PostgreSQL passwords, enable TLS
3. **Network Security**: Deploy behind WAF, use network policies
4. **Monitoring**: Enable security event logging and alerting
5. **Updates**: Apply security patches promptly
6. **Backups**: Regular database backups with encryption
7. **Access Control**: Principle of least privilege for service accounts

### For Developers

1. **Input Validation**: Validate all user input at API boundaries
2. **Authentication**: Always verify JWT tokens before processing requests
3. **Authorization**: Check permissions at service layer, not just controller
4. **Sensitive Data**: Never log passwords, tokens, or PII
5. **Dependencies**: Keep dependencies up to date, review security advisories
6. **Code Review**: Security review for all authentication/authorization changes
7. **Testing**: Include security test cases in unit and integration tests

## Security Checklist

Before deploying to production:

- [ ] All environment variables configured (JWT_SECRET, DB credentials)
- [ ] PostgreSQL TLS enabled and enforced
- [ ] HTTPS/TLS configured for API endpoints
- [ ] Security headers enabled in Spring Security configuration
- [ ] Rate limiting configured appropriately
- [ ] CORS origins restricted to known domains
- [ ] Error messages sanitized (no stack traces)
- [ ] Logging configured (no sensitive data logged)
- [ ] Database migrations tested and reviewed
- [ ] Dependencies scanned for vulnerabilities
- [ ] Security monitoring and alerting enabled
- [ ] Backup and disaster recovery tested

## Known Security Considerations

### Architecture-Specific

- **JWT Validation**: Service trusts JWT tokens from user-management-service.
  Ensure that service is properly secured.
- **Database Access**: Service has full access to recipe_manager schema. Use
  PostgreSQL row-level security if multi-tenancy is required.
- **API Exposure**: All recipe data is accessible via API. Implement
  additional authorization if data privacy is required.
- **Flyway Migrations**: Database migrations run with elevated privileges.
  Review all migration scripts carefully.

### Configuration Notes

- **JWT_SECRET**: Only required if OAuth2 introspection is disabled. Keep
  secret secure and rotate regularly.
- **Database Credentials**: Use strong passwords and consider using secret
  management (HashiCorp Vault, AWS Secrets Manager).
- **Connection Pooling**: HikariCP configured with leak detection. Monitor for
  connection leaks in production.

## Disclosure Policy

We follow coordinated disclosure:

1. **Private Report**: Vulnerability reported via Security Advisories
2. **Investigation**: Security team investigates and confirms issue
3. **Fix Development**: Patch developed and tested privately
4. **Pre-Disclosure**: Advance notice to affected users (if applicable)
5. **Public Release**: Security advisory published with fix
6. **CVE Assignment**: CVE assigned for tracking (if warranted)

We aim to disclose vulnerabilities within 90 days of initial report, or sooner
if actively exploited.

## Security Updates

Stay informed about security updates:

- **GitHub Watch**: Enable security alert notifications
- **Release Notes**: Review security fixes in release notes
- **Security Advisories**: Subscribe to repository security advisories
- **Dependabot**: Enable Dependabot security updates

## Contact

For security concerns:

- **Private Issues**: Use
  [Security Advisories](https://github.com/Recipe-Web-App/recipe-management-service/security/advisories/new)
- **General Questions**: Use GitHub Discussions (for non-sensitive topics)
- **Maintainer**: @jsamuelsen

## Acknowledgments

We appreciate responsible security researchers who help keep this project secure.
Security researchers who report valid vulnerabilities will be acknowledged in:

- Security advisory credits
- CHANGELOG.md security fixes section
- Public thanks (unless anonymity requested)

Thank you for helping keep Recipe Management Service secure! 🔒
