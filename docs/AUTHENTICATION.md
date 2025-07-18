# Authentication System

## Overview

The Recipe Manager Service uses JWT (JSON Web Token) authentication that integrates with your user-management-service. This document explains how authentication works and how to configure it.

## Architecture

### Authentication Flow

1. **User Login**: User authenticates with the user-management-service
2. **Token Generation**: user-management-service generates a JWT token
3. **Token Usage**: Client includes JWT token in Authorization header
4. **Token Validation**: Recipe service validates the JWT token
5. **User Context**: Spring Security context is set up with user details

### Components

- **JwtAuthenticationFilter**: Validates JWT tokens from requests
- **JwtService**: Handles JWT token parsing and validation
- **CustomUserDetailsService**: Creates user details from JWT claims
- **SecurityConfig**: Configures Spring Security with JWT support

## Configuration

### JWT Configuration

```yaml
app:
  security:
    jwt:
      secret: ${JWT_SECRET:your-secret-key-here-change-in-production}
      expiration: 86400000 # 24 hours
```

### Environment Variables

```bash
# JWT Secret (must match user-management-service)
JWT_SECRET=your-base64-encoded-secret-key

# JWT Expiration (optional, defaults to 24 hours)
JWT_EXPIRATION=86400000
```

### Service-to-Service Authentication (Optional)

```yaml
app:
  security:
    service:
      auth:
        enabled: ${SERVICE_AUTH_ENABLED:false}
        key: ${SERVICE_AUTH_KEY:}
```

## JWT Token Structure

### Expected Claims

The JWT token should contain the following claims:

```json
{
  "sub": "username",
  "userId": "user-uuid",
  "roles": ["USER", "ADMIN"],
  "iat": 1642234567,
  "exp": 1642320967
}
```

### Token Validation

The service validates:

- **Signature**: Verifies token was signed by the trusted user-management-service
- **Expiration**: Checks if token has expired
- **Claims**: Extracts username, user ID, and roles
- **Format**: Ensures proper JWT structure

## Usage Examples

### Client Request

```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
     -H "X-Request-ID: abc-123-def" \
     http://localhost:8080/api/v1/recipes
```

### Controller Method

```java
@RestController
@RequestMapping("/api/v1/recipes")
public class RecipeController {

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Recipe>> getRecipes() {
        // User is automatically authenticated via JWT
        String username = SecurityContextHolder.getContext()
            .getAuthentication().getName();

        return ResponseEntity.ok(recipeService.getRecipesByUser(username));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {
        // Only users with ADMIN role can access this endpoint
        return ResponseEntity.ok(recipeService.createRecipe(recipe));
    }
}
```

## Security Features

### Role-Based Authorization

```java
// User role required
@PreAuthorize("hasRole('USER')")

// Admin role required
@PreAuthorize("hasRole('ADMIN')")

// Multiple roles
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")

// Custom expressions
@PreAuthorize("hasRole('USER') and #recipe.userId == authentication.name")
```

### Method-Level Security

```java
@Service
public class RecipeService {

    @PreAuthorize("hasRole('USER')")
    public List<Recipe> getUserRecipes(String username) {
        // Implementation
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Recipe createRecipe(Recipe recipe) {
        // Implementation
    }
}
```

## Error Handling

### Authentication Errors

- **401 Unauthorized**: Invalid or missing JWT token
- **403 Forbidden**: Valid token but insufficient permissions
- **400 Bad Request**: Malformed JWT token

### Error Response Format

```json
{
  "timestamp": "2024-01-15T10:30:45.123Z",
  "status": 401,
  "error": "Authentication failed",
  "message": "Invalid or expired token",
  "path": "/api/v1/recipes",
  "requestId": "abc-123-def"
}
```

## Integration with User Management Service

### Token Generation (User Management Service)

```java
// In your user-management-service
@Service
public class JwtTokenService {

    public String generateToken(User user) {
        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("userId", user.getId())
            .claim("roles", user.getRoles())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 86400000))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
}
```

### Shared Configuration

Both services must use the same JWT secret:

```yaml
# user-management-service
jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000

# recipe-manager-service
app:
  security:
    jwt:
      secret: ${JWT_SECRET}
      expiration: 86400000
```

## Service-to-Service Authentication

### Internal Service Communication

For service-to-service calls, you can use API key authentication:

```bash
curl -H "X-Service-Auth: your-service-key" \
     -H "X-Service-Name: user-management-service" \
     http://localhost:8080/api/v1/internal/health
```

### Configuration

```yaml
app:
  security:
    service:
      auth:
        enabled: true
        key: ${SERVICE_AUTH_KEY}
```

## Monitoring and Logging

### Authentication Events

The service logs authentication events:

```
2024-01-15 10:30:45.123 [main] INFO  security.JwtAuthenticationFilter [abc-123-def] - User authenticated successfully: john.doe
2024-01-15 10:30:45.124 [main] WARN  security.JwtAuthenticationFilter [abc-123-def] - JWT authentication failed: Token expired
```

### Security Metrics

- Authentication success/failure rates
- Token validation times
- Role-based access patterns

## Best Practices

### Token Security

1. **Use HTTPS**: Always transmit tokens over HTTPS
2. **Token Expiration**: Set reasonable expiration times (24 hours max)
3. **Secret Management**: Use environment variables for JWT secrets
4. **Token Storage**: Store tokens securely on the client side

### Authorization

1. **Principle of Least Privilege**: Grant minimum required permissions
2. **Role-Based Access**: Use roles for authorization decisions
3. **Method-Level Security**: Protect sensitive operations
4. **Input Validation**: Validate all user inputs

### Monitoring

1. **Authentication Logs**: Monitor authentication events
2. **Failed Attempts**: Alert on repeated authentication failures
3. **Token Usage**: Track token usage patterns
4. **Performance**: Monitor JWT validation performance

## Troubleshooting

### Common Issues

#### Invalid Token

```
JWT authentication failed: Invalid token signature
```

**Solution**: Ensure JWT_SECRET matches between services

#### Expired Token

```
JWT authentication failed: Token expired
```

**Solution**: Client should refresh token or re-authenticate

#### Missing Authorization Header

```
JWT authentication failed: No token provided
```

**Solution**: Include Authorization header with Bearer token

#### Insufficient Permissions

```
Access denied: You don't have permission to access this resource
```

**Solution**: Check user roles and endpoint permissions

### Debug Mode

Enable debug logging for authentication:

```yaml
logging:
  level:
    security: DEBUG
    config: DEBUG
```

## Migration Guide

### From Session-Based to JWT

1. **Update Client**: Modify client to include JWT tokens
2. **Configure Services**: Set up JWT validation in all services
3. **Test Authentication**: Verify token validation works
4. **Monitor**: Watch for authentication issues

### From Local Authentication to User Management Service

1. **Deploy User Service**: Ensure user-management-service is running
2. **Configure JWT**: Set up shared JWT secret
3. **Update Clients**: Modify clients to use user service for login
4. **Test Integration**: Verify end-to-end authentication flow

## Security Considerations

### Token Security

- **Secret Rotation**: Regularly rotate JWT secrets
- **Token Revocation**: Implement token blacklisting if needed
- **Token Size**: Keep tokens reasonably sized
- **Token Claims**: Only include necessary claims

### Network Security

- **HTTPS Only**: Never transmit tokens over HTTP
- **CORS Configuration**: Properly configure CORS for your domain
- **Rate Limiting**: Implement rate limiting on authentication endpoints
- **Monitoring**: Monitor for suspicious authentication patterns

This authentication system provides secure, scalable authentication for your microservices architecture while maintaining simplicity and ease of use.
