# Media Management System Documentation

## Overview

The Recipe Manager Service provides comprehensive media management
capabilities through integration with an external media-management-service.
This system enables users to upload, manage, and organize media files (images
and videos) associated with recipes, ingredients, and cooking steps.

## Architecture

### System Components

```text
┌─────────────────────┐    ┌──────────────────────┐    ┌─────────────────────┐
│                     │    │                      │    │                     │
│   Recipe Manager    │◄──►│  Media Manager       │◄──►│   File Storage      │
│   Service (Java)    │    │  Service (Rust)      │    │   (Content-         │
│                     │    │                      │    │    Addressable)     │
└─────────────────────┘    └──────────────────────┘    └─────────────────────┘
          │                          │
          │                          │
          ▼                          ▼
┌─────────────────────┐    ┌──────────────────────┐
│                     │    │                      │
│   PostgreSQL        │    │   Media Processing   │
│   (Metadata)        │    │   Pipeline           │
│                     │    │                      │
└─────────────────────┘    └──────────────────────┘
```

### Service Responsibilities

#### Recipe Manager Service (This Service)

- **API Endpoints**: 8 REST endpoints for media operations
- **Authentication**: JWT-based user authentication and authorization
- **Business Logic**: Recipe ownership validation and access control
- **Database**: Media association metadata storage
- **Integration**: External service communication with resilience patterns

#### Media Manager Service (External)

- **File Storage**: Content-addressable storage with SHA-256 hashing
- **Processing**: Image/video processing and format conversion
- **Deduplication**: Automatic content deduplication
- **Content Validation**: File type and size validation

## API Endpoints

### Recipe Media Endpoints

#### Get Recipe Media

```http
GET /recipe-management/recipes/{recipeId}/media
Authorization: Bearer {jwt_token}
```

**Response:**

```json
{
  "content": [
    {
      "mediaId": 123,
      "originalFilename": "pasta_dish.jpg",
      "mediaType": "IMAGE_JPEG",
      "fileSize": 1048576,
      "contentHash": "abc123...",
      "processingStatus": "COMPLETE",
      "createdAt": "2024-08-24T10:30:00Z"
    }
  ],
  "pageable": {...},
  "totalElements": 1
}
```

#### Upload Recipe Media

```http
POST /recipe-management/recipes/{recipeId}/media
Authorization: Bearer {jwt_token}
Content-Type: multipart/form-data

file: [binary file data]
originalFilename: recipe_photo.jpg
mediaType: IMAGE_JPEG
fileSize: 1048576
contentHash: abc123... (optional)
```

**Response:**

```json
{
  "mediaId": 123,
  "uploadUrl": "https://media.example.com/upload/abc123...",
  "contentHash": "def456..."
}
```

#### Delete Recipe Media

```http
DELETE /recipe-management/recipes/{recipeId}/media/{mediaId}
Authorization: Bearer {jwt_token}
```

**Response:**

```json
{
  "success": true,
  "message": "Media successfully deleted from recipe",
  "mediaId": 123
}
```

### Ingredient Media Endpoints

#### Get Ingredient Media

```http
GET /recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/media
Authorization: Bearer {jwt_token}
```

#### Upload Ingredient Media

```http
POST /recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/media
Authorization: Bearer {jwt_token}
Content-Type: multipart/form-data
```

#### Delete Ingredient Media

```http
DELETE /recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/media/{mediaId}
Authorization: Bearer {jwt_token}
```

### Step Media Endpoints

#### Get Step Media

```http
GET /recipe-management/recipes/{recipeId}/steps/{stepId}/media
Authorization: Bearer {jwt_token}
```

#### Upload Step Media

```http
POST /recipe-management/recipes/{recipeId}/steps/{stepId}/media
Authorization: Bearer {jwt_token}
Content-Type: multipart/form-data
```

#### Delete Step Media

```http
DELETE /recipe-management/recipes/{recipeId}/steps/{stepId}/media/{mediaId}
Authorization: Bearer {jwt_token}
```

## Security Model

### Authentication & Authorization

1. **JWT Token Required**: All endpoints require valid JWT token
2. **Recipe Ownership**: Users can only manage media for recipes they own
3. **Media Ownership**: Users can only delete media files they uploaded
4. **Operation-Specific Validation**: Different validation for different HTTP methods

### Security Validation Flow

```text
1. JWT Token Validation
   ↓
2. Recipe Ownership Check
   ↓
3. Media Ownership Check (for delete operations)
   ↓
4. Content Validation (for upload operations)
   ↓
5. External Service Call
   ↓
6. Database Transaction
```

## External Service Integration

### MediaManagerService Configuration

The service integrates with the external media-management-service using:

- **Circuit Breaker**: Prevents cascade failures
- **Retry Logic**: Automatic retry with exponential backoff
- **Fallback Methods**: Graceful degradation when service unavailable
- **Timeout Handling**: Configurable request timeouts

### Resilience Patterns

```java
@CircuitBreaker(name = "media-manager", fallbackMethod = "uploadMediaFallback")
@Retry(name = "media-manager")
@TimeLimiter(name = "media-manager")
public CompletableFuture<UploadMediaResponseDto> uploadMedia(MultipartFile file)
```

### Configuration Properties

```yaml
resilience4j:
  circuitbreaker:
    instances:
      media-manager:
        sliding-window-size: 10
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s

  retry:
    instances:
      media-manager:
        max-attempts: 3
        wait-duration: 1s
        exponential-backoff-multiplier: 2
```

## Database Schema

### Media Entity

```sql
CREATE TABLE media (
    media_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id UUID NOT NULL,
    media_type VARCHAR(50) NOT NULL,
    media_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    content_hash VARCHAR(64),
    original_filename VARCHAR(255) NOT NULL,
    processing_status VARCHAR(20) NOT NULL DEFAULT 'INITIATED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Association Tables

```sql
-- Recipe Media Associations
CREATE TABLE recipe_media (
    recipe_id BIGINT NOT NULL,
    media_id BIGINT NOT NULL,
    PRIMARY KEY (recipe_id, media_id)
);

-- Ingredient Media Associations
CREATE TABLE ingredient_media (
    ingredient_id BIGINT NOT NULL,
    media_id BIGINT NOT NULL,
    PRIMARY KEY (ingredient_id, media_id)
);

-- Step Media Associations
CREATE TABLE step_media (
    step_id BIGINT NOT NULL,
    media_id BIGINT NOT NULL,
    PRIMARY KEY (step_id, media_id)
);
```

## Error Handling

### Common Error Responses

#### Authentication Errors

```json
{
  "timestamp": "2024-08-24T10:30:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token is invalid or expired",
  "path": "/recipe-management/recipes/123/media"
}
```

#### Access Denied

```json
{
  "timestamp": "2024-08-24T10:30:00Z",
  "status": 403,
  "error": "Forbidden",
  "message": "You don't have permission to POST this recipe",
  "path": "/recipe-management/recipes/123/media"
}
```

#### Resource Not Found

```json
{
  "timestamp": "2024-08-24T10:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Recipe not found with id: 123",
  "path": "/recipe-management/recipes/123/media"
}
```

#### External Service Errors

```json
{
  "timestamp": "2024-08-24T10:30:00Z",
  "status": 503,
  "error": "Service Unavailable",
  "message": "Media manager service is temporarily unavailable",
  "path": "/recipe-management/recipes/123/media"
}
```

## File Upload Requirements

### Supported File Types

- **Images**: JPEG, PNG, WebP, AVIF, GIF
- **Videos**: MP4, WebM, MOV

### File Size Limits

- **Default**: 10MB per file (configurable)
- **Images**: Recommended max 5MB
- **Videos**: Recommended max 50MB

### Content Validation

- **MIME Type Validation**: Server-side content type validation
- **File Extension Validation**: Filename extension checking
- **Content Hash**: Optional SHA-256 hash for integrity verification

## Performance Considerations

### Caching Strategy

- **Media Metadata**: Cached for 10 minutes
- **External Service Calls**: Circuit breaker prevents repeated failures
- **Database Queries**: Optimized with proper indexing

### Async Processing

- **File Uploads**: Non-blocking upload to external service
- **Processing Pipeline**: Background processing for format conversion
- **Status Updates**: Async status updates via database triggers

## Monitoring & Observability

### Metrics

```bash
# Service call metrics
external.service.calls{service="media-service"}

# Service failure metrics
external.service.failures{service="media-service"}

# Response time metrics
external.service.response.time{service="media-service"}
```

### Health Checks

```bash
# Application health (includes external service status)
curl http://localhost:8080/actuator/health

# Detailed health information
curl http://localhost:8080/actuator/health/db
curl http://localhost:8080/actuator/health/diskSpace
```

### Logging

```json
{
  "timestamp": "2024-08-24T10:30:00Z",
  "level": "INFO",
  "logger": "MediaService",
  "message": "Successfully created media 123 for recipe 456",
  "correlationId": "abc-123-def",
  "userId": "user123",
  "operation": "createRecipeMedia"
}
```

## Troubleshooting

### Common Issues

#### "Media manager service is unavailable"

- **Cause**: External media-management-service is down or unreachable
- **Solution**: Check external service status, review network connectivity
- **Fallback**: Service returns placeholder responses, operations are queued

#### "You don't have permission to POST this recipe"

- **Cause**: User attempting to upload media for recipe they don't own
- **Solution**: Verify recipe ownership, use correct user authentication

#### "Content type validation failed"

- **Cause**: Uploaded file type not supported
- **Solution**: Use supported file formats (JPEG, PNG, MP4, etc.)

#### "File too large: exceeds maximum size limit"

- **Cause**: Uploaded file exceeds configured size limit
- **Solution**: Reduce file size or contact administrator to adjust limits

### Debug Commands

```bash
# Check external service connectivity
curl http://media-management.local/api/v1/media-management/health

# Verify JWT token
curl -H "Authorization: Bearer YOUR_TOKEN" \
     http://localhost:8080/actuator/health

# Check service logs
kubectl logs -n recipe-manager -l app=recipe-manager-service

# Check circuit breaker status
curl http://localhost:8080/actuator/metrics/resilience4j.circuitbreaker.state
```

## Development Guide

### Local Development Setup

1. **Start Media Manager Service**

   ```bash
   # Ensure media-management-service is running
   curl http://localhost:3000/api/v1/media-management/health
   ```

2. **Configure Integration**

   ```bash
   # Set environment variables
   export MEDIA_MANAGER_BASE_URL=http://localhost:3000
   export MEDIA_MANAGER_TIMEOUT=30s
   ```

3. **Test Media Upload**

   ```bash
   curl -X POST \
     -H "Authorization: Bearer YOUR_TOKEN" \
     -F "file=@test.jpg" \
     -F "originalFilename=test.jpg" \
     -F "mediaType=IMAGE_JPEG" \
     -F "fileSize=1024" \
     http://localhost:8080/recipe-management/recipes/1/media
   ```

### Testing Strategy

- **Unit Tests**: Mock external service calls
- **Component Tests**: Use TestContainers for database, mock external service
- **Integration Tests**: Full end-to-end with running external service
- **Contract Tests**: Verify external service API compatibility

For complete API testing, use the Postman collection: `tests/postman/Recipe-Manager-Media.postman_collection.json`

---

## Generated Documentation

Generated for Recipe Manager Service - Media Management System
