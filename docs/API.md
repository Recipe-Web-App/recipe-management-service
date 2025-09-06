# Recipe Management Service API Documentation

## Overview

The Recipe Management Service is a production-ready microservice that provides
comprehensive recipe management functionality within the Recipe Web Application
ecosystem. Built with Java 21 and Spring Boot 3.5.3, it offers a complete suite
of endpoints for managing recipes, ingredients, steps, tags, reviews, and media
content.

## Architecture

- **Framework**: Spring Boot 3.5.3 with Java 21
- **Database**: PostgreSQL with Flyway migrations
- **Authentication**: JWT token authentication integrated with
  user-management-service
- **Documentation**: OpenAPI 3.1 specification with comprehensive endpoint
  coverage
- **Testing**: Multi-layered testing strategy (Unit, Component, Integration,
  Performance)

## Base URLs

- **Local Development**: `http://localhost:8080/api/v1/recipe-management`
- **Kubernetes**: `http://recipe-management.local/api/v1/recipe-management`

## Authentication

All endpoints require JWT authentication via the `Authorization` header:

```http
Authorization: Bearer <jwt_token>
```

The JWT token must be obtained from the user-management-service and include
appropriate role-based permissions for the requested operations.

## Response Formats

All API responses follow a consistent JSON structure:

### Success Responses

```json
{
  "data": { /* Response data */ },
  "status": "success"
}
```

### Error Responses

```json
{
  "error": "Error Type",
  "message": "Human-readable error description",
  "timestamp": "2024-08-28T10:30:00Z",
  "path": "/recipe-management/recipes/123"
}
```

## Pagination

Endpoints that return lists support pagination using Spring Data's Pageable
interface:

### Query Parameters

- `page` (integer): Page number (0-based, default: 0)
- `size` (integer): Page size (default: 20, max: 100)
- `sort` (string): Sort criteria in format `property,direction` (e.g.,
  `createdAt,desc`)

### Paginated Response Format

```json
{
  "content": [ /* Array of items */ ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": { "sorted": true, "orders": [...] }
  },
  "totalElements": 150,
  "totalPages": 8,
  "first": true,
  "last": false,
  "numberOfElements": 20
}
```

---

## Recipe Management Endpoints

### Create Recipe

**POST** `/recipe-management/recipes`

Creates a new recipe with ingredients and steps.

**Request Body:**

```json
{
  "title": "Chocolate Chip Cookies",
  "description": "Classic homemade chocolate chip cookies",
  "servings": 24,
  "prepTime": 15,
  "cookTime": 12,
  "difficulty": "EASY",
  "ingredients": [
    {
      "name": "All-purpose flour",
      "quantity": 2.25,
      "unit": "CUPS",
      "notes": "Sifted"
    }
  ],
  "steps": [
    {
      "stepNumber": 1,
      "instruction": "Preheat oven to 375°F (190°C)",
      "duration": 5
    }
  ],
  "tags": ["dessert", "cookies", "baking"]
}
```

**Response (201 Created):**

```json
{
  "recipeId": 123,
  "title": "Chocolate Chip Cookies",
  "description": "Classic homemade chocolate chip cookies",
  "servings": 24,
  "prepTime": 15,
  "cookTime": 12,
  "totalTime": 27,
  "difficulty": "EASY",
  "createdAt": "2024-08-28T10:30:00Z",
  "updatedAt": "2024-08-28T10:30:00Z",
  "createdBy": 456,
  "ingredients": [ /* Full ingredient details */ ],
  "steps": [ /* Full step details */ ],
  "tags": [ /* Tag details */ ],
  "averageRating": null,
  "reviewCount": 0
}
```

### Get All Recipes

**GET** `/recipe-management/recipes`

Retrieves all recipes with pagination support.

**Query Parameters:**

- Standard pagination parameters (`page`, `size`, `sort`)

**Response (200 OK):**

```json
{
  "content": [
    {
      "recipeId": 123,
      "title": "Chocolate Chip Cookies",
      "description": "Classic homemade chocolate chip cookies",
      "servings": 24,
      "prepTime": 15,
      "cookTime": 12,
      "totalTime": 27,
      "difficulty": "EASY",
      "averageRating": 4.5,
      "reviewCount": 12,
      "createdAt": "2024-08-28T10:30:00Z"
    }
  ],
  "totalElements": 150,
  "totalPages": 8
}
```

### Get Recipe by ID

**GET** `/recipe-management/recipes/{recipeId}`

Retrieves a complete recipe with all associated data.

**Path Parameters:**

- `recipeId` (string): Recipe identifier

**Response (200 OK):**

```json
{
  "recipeId": 123,
  "title": "Chocolate Chip Cookies",
  "description": "Classic homemade chocolate chip cookies",
  "servings": 24,
  "prepTime": 15,
  "cookTime": 12,
  "totalTime": 27,
  "difficulty": "EASY",
  "createdAt": "2024-08-28T10:30:00Z",
  "updatedAt": "2024-08-28T10:30:00Z",
  "createdBy": 456,
  "ingredients": [
    {
      "ingredientId": 789,
      "name": "All-purpose flour",
      "quantity": 2.25,
      "unit": "CUPS",
      "notes": "Sifted",
      "order": 1
    }
  ],
  "steps": [
    {
      "stepId": 101,
      "stepNumber": 1,
      "instruction": "Preheat oven to 375°F (190°C)",
      "duration": 5,
      "order": 1
    }
  ],
  "tags": [
    {
      "tagId": 201,
      "name": "dessert",
      "category": "CATEGORY"
    }
  ],
  "averageRating": 4.5,
  "reviewCount": 12
}
```

### Update Recipe

**PUT** `/recipe-management/recipes/{recipeId}`

Updates an existing recipe.

**Path Parameters:**

- `recipeId` (string): Recipe identifier

**Request Body:** Same structure as Create Recipe

**Response (200 OK):** Updated recipe data (same structure as Get Recipe)

### Delete Recipe

**DELETE** `/recipe-management/recipes/{recipeId}`

Deletes a recipe and all associated data.

**Path Parameters:**

- `recipeId` (string): Recipe identifier

**Response (204 No Content):** Empty response body

### Search Recipes

**POST** `/recipe-management/recipes/search`

Advanced recipe search with flexible criteria.

**Request Body:**

```json
{
  "query": "chocolate chip",
  "ingredients": ["flour", "chocolate"],
  "tags": ["dessert"],
  "difficulty": ["EASY", "MEDIUM"],
  "maxPrepTime": 30,
  "maxCookTime": 60,
  "minRating": 4.0,
  "sortBy": "RATING_DESC"
}
```

**Response (200 OK):** Paginated search results with same structure as Get All Recipes

---

## Ingredient Management Endpoints

### Get Recipe Ingredients

**GET** `/recipe-management/recipes/{recipeId}/ingredients`

Retrieves all ingredients for a specific recipe.

**Path Parameters:**

- `recipeId` (string): Recipe identifier

**Response (200 OK):**

```json
{
  "recipeId": 123,
  "ingredients": [
    {
      "ingredientId": 789,
      "name": "All-purpose flour",
      "quantity": 2.25,
      "unit": "CUPS",
      "notes": "Sifted",
      "order": 1,
      "comments": [
        {
          "commentId": 301,
          "userId": 456,
          "comment": "Make sure to sift for best results",
          "createdAt": "2024-08-28T10:35:00Z"
        }
      ]
    }
  ]
}
```

### Scale Recipe Ingredients

**GET** `/recipe-management/recipes/{recipeId}/ingredients/scale`

Scales ingredient quantities for different serving sizes.

**Path Parameters:**

- `recipeId` (string): Recipe identifier

**Query Parameters:**

- `quantity` (float): Scaling multiplier (minimum: 0)

**Response (200 OK):** Scaled ingredients with same structure as Get Recipe Ingredients

### Generate Shopping List

**GET** `/recipe-management/recipes/{recipeId}/ingredients/shopping-list`

Generates a shopping list from recipe ingredients.

**Path Parameters:**

- `recipeId` (string): Recipe identifier

**Response (200 OK):**

```json
{
  "recipeId": 123,
  "recipeTitle": "Chocolate Chip Cookies",
  "items": [
    {
      "name": "All-purpose flour",
      "quantity": 2.25,
      "unit": "CUPS",
      "category": "BAKING",
      "notes": "Sifted"
    }
  ],
  "generatedAt": "2024-08-28T10:30:00Z"
}
```

---

## Ingredient Comments Endpoints

### Add Ingredient Comment

**POST** `/recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/comment`

Adds a comment to a recipe ingredient.

**Path Parameters:**

- `recipeId` (string): Recipe identifier
- `ingredientId` (string): Ingredient identifier

**Request Body:**

```json
{
  "comment": "Make sure to sift for best results",
  "userId": 456
}
```

**Response (200 OK):**

```json
{
  "commentId": 301,
  "ingredientId": 789,
  "userId": 456,
  "comment": "Make sure to sift for best results",
  "createdAt": "2024-08-28T10:35:00Z",
  "updatedAt": "2024-08-28T10:35:00Z"
}
```

### Edit Ingredient Comment

**PUT** `/recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/comment`

Edits an existing ingredient comment.

**Path Parameters:**

- `recipeId` (string): Recipe identifier
- `ingredientId` (string): Ingredient identifier

**Request Body:**

```json
{
  "commentId": 301,
  "comment": "Updated comment text",
  "userId": 456
}
```

**Response (200 OK):** Updated comment data

### Delete Ingredient Comment

**DELETE** `/recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/comment`

Deletes an ingredient comment.

**Path Parameters:**

- `recipeId` (string): Recipe identifier
- `ingredientId` (string): Ingredient identifier

**Request Body:**

```json
{
  "commentId": 301,
  "userId": 456
}
```

**Response (200 OK):** Confirmation of deletion

---

## Step Management Endpoints

### Get Recipe Steps

**GET** `/recipe-management/recipes/{recipeId}/steps`

Retrieves all steps for a specific recipe.

**Path Parameters:**

- `recipeId` (long): Recipe identifier

**Response (200 OK):**

```json
{
  "recipeId": 123,
  "steps": [
    {
      "stepId": 101,
      "stepNumber": 1,
      "instruction": "Preheat oven to 375°F (190°C)",
      "duration": 5,
      "order": 1,
      "comments": [
        {
          "commentId": 401,
          "userId": 456,
          "comment": "Don't forget to preheat early",
          "createdAt": "2024-08-28T10:40:00Z"
        }
      ]
    }
  ]
}
```

### Get Step Comments

**GET** `/recipe-management/recipes/{recipeId}/steps/{stepId}/comment`

Retrieves comments for a specific step.

**Path Parameters:**

- `recipeId` (long): Recipe identifier
- `stepId` (long): Step identifier

**Response (200 OK):**

```json
{
  "stepId": 101,
  "comments": [
    {
      "commentId": 401,
      "userId": 456,
      "comment": "Don't forget to preheat early",
      "createdAt": "2024-08-28T10:40:00Z",
      "updatedAt": "2024-08-28T10:40:00Z"
    }
  ]
}
```

### Add Step Comment

**POST** `/recipe-management/recipes/{recipeId}/steps/{stepId}/comment`

Adds a comment to a recipe step.

**Path Parameters:**

- `recipeId` (long): Recipe identifier
- `stepId` (long): Step identifier

**Request Body:**

```json
{
  "comment": "Don't forget to preheat early",
  "userId": 456
}
```

**Response (201 Created):**

```json
{
  "commentId": 401,
  "stepId": 101,
  "userId": 456,
  "comment": "Don't forget to preheat early",
  "createdAt": "2024-08-28T10:40:00Z",
  "updatedAt": "2024-08-28T10:40:00Z"
}
```

### Edit Step Comment

**PUT** `/recipe-management/recipes/{recipeId}/steps/{stepId}/comment`

Edits an existing step comment.

**Path Parameters:**

- `recipeId` (long): Recipe identifier
- `stepId` (long): Step identifier

**Request Body:**

```json
{
  "commentId": 401,
  "comment": "Updated step comment",
  "userId": 456
}
```

**Response (200 OK):** Updated comment data

### Delete Step Comment

**DELETE** `/recipe-management/recipes/{recipeId}/steps/{stepId}/comment`

Deletes a step comment.

**Path Parameters:**

- `recipeId` (long): Recipe identifier
- `stepId` (long): Step identifier

**Request Body:**

```json
{
  "commentId": 401,
  "userId": 456
}
```

**Response (204 No Content):** Empty response body

---

## Tag Management Endpoints

### Get Recipe Tags

**GET** `/recipe-management/recipes/{recipeId}/tags`

Retrieves all tags associated with a recipe.

**Path Parameters:**

- `recipeId` (long): Recipe identifier

**Response (200 OK):**

```json
{
  "recipeId": 123,
  "tags": [
    {
      "tagId": 201,
      "name": "dessert",
      "category": "CATEGORY",
      "description": "Sweet treats and desserts",
      "usageCount": 150
    }
  ]
}
```

### Add Tag to Recipe

**POST** `/recipe-management/recipes/{recipeId}/tags`

Adds a tag to a recipe.

**Path Parameters:**

- `recipeId` (long): Recipe identifier

**Request Body:**

```json
{
  "tagName": "dessert",
  "category": "CATEGORY"
}
```

**Response (201 Created):**

```json
{
  "recipeId": 123,
  "tag": {
    "tagId": 201,
    "name": "dessert",
    "category": "CATEGORY",
    "description": "Sweet treats and desserts",
    "usageCount": 151
  },
  "addedAt": "2024-08-28T10:45:00Z"
}
```

### Remove Tag from Recipe

**DELETE** `/recipe-management/recipes/{recipeId}/tags`

Removes a tag from a recipe.

**Path Parameters:**

- `recipeId` (long): Recipe identifier

**Request Body:**

```json
{
  "tagName": "dessert"
}
```

**Response (200 OK):**

```json
{
  "recipeId": 123,
  "removedTag": {
    "tagId": 201,
    "name": "dessert",
    "category": "CATEGORY"
  },
  "removedAt": "2024-08-28T10:50:00Z"
}
```

---

## Review Management Endpoints

### Get Recipe Reviews

**GET** `/recipe-management/recipes/{recipeId}/review`

Retrieves all reviews for a specific recipe.

**Path Parameters:**

- `recipeId` (long): Recipe identifier

**Response (200 OK):**

```json
{
  "recipeId": 123,
  "averageRating": 4.5,
  "reviewCount": 12,
  "reviews": [
    {
      "reviewId": 501,
      "userId": 456,
      "rating": 5,
      "comment": "Absolutely delicious! My family loved these cookies.",
      "createdAt": "2024-08-28T10:55:00Z",
      "updatedAt": "2024-08-28T10:55:00Z"
    }
  ]
}
```

### Add Recipe Review

**POST** `/recipe-management/recipes/{recipeId}/review`

Adds a review to a recipe.

**Path Parameters:**

- `recipeId` (long): Recipe identifier

**Request Body:**

```json
{
  "rating": 5,
  "comment": "Absolutely delicious! My family loved these cookies.",
  "userId": 456
}
```

**Response (200 OK):**

```json
{
  "reviewId": 501,
  "recipeId": 123,
  "userId": 456,
  "rating": 5,
  "comment": "Absolutely delicious! My family loved these cookies.",
  "createdAt": "2024-08-28T10:55:00Z",
  "updatedAt": "2024-08-28T10:55:00Z"
}
```

### Edit Recipe Review

**PUT** `/recipe-management/recipes/{recipeId}/review/{reviewId}`

Edits an existing recipe review.

**Path Parameters:**

- `recipeId` (long): Recipe identifier
- `reviewId` (long): Review identifier

**Request Body:**

```json
{
  "rating": 4,
  "comment": "Updated review - still very good!",
  "userId": 456
}
```

**Response (200 OK):** Updated review data

### Delete Recipe Review

**DELETE** `/recipe-management/recipes/{recipeId}/review/{reviewId}`

Deletes a recipe review.

**Path Parameters:**

- `recipeId` (long): Recipe identifier
- `reviewId` (long): Review identifier

**Response (204 No Content):** Empty response body

---

## Revision History Endpoints

### Get Recipe Revisions

**GET** `/recipe-management/recipes/{recipeId}/revisions`

Retrieves all revisions for a recipe.

**Path Parameters:**

- `recipeId` (long): Recipe identifier

**Response (200 OK):**

```json
{
  "recipeId": 123,
  "revisions": [
    {
      "revisionId": 601,
      "revisionType": "UPDATE",
      "category": "RECIPE",
      "changes": {
        "title": {
          "oldValue": "Chocolate Cookies",
          "newValue": "Chocolate Chip Cookies"
        }
      },
      "createdBy": 456,
      "createdAt": "2024-08-28T11:00:00Z"
    }
  ]
}
```

### Get Step Revisions

**GET** `/recipe-management/recipes/{recipeId}/steps/{stepId}/revisions`

Retrieves all revisions for a specific step.

**Path Parameters:**

- `recipeId` (long): Recipe identifier
- `stepId` (long): Step identifier

**Response (200 OK):** Revision history for the specified step

### Get Ingredient Revisions

**GET** `/recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/revisions`

Retrieves all revisions for a specific ingredient.

**Path Parameters:**

- `recipeId` (long): Recipe identifier
- `ingredientId` (long): Ingredient identifier

**Response (200 OK):** Revision history for the specified ingredient

---

## Media Management Endpoints

### Recipe Media

#### Get Recipe Media

**GET** `/recipe-management/recipes/{recipeId}/media`

Retrieves all media associated with a recipe.

**Path Parameters:**

- `recipeId` (long): Recipe identifier

**Query Parameters:**

- Standard pagination parameters

**Response (200 OK):**

```json
{
  "content": [
    {
      "mediaId": 701,
      "originalFilename": "cookies.jpg",
      "mediaType": "IMAGE",
      "mediaFormat": "JPEG",
      "fileSize": 1048576,
      "contentHash": "abc123def456",
      "processingStatus": "COMPLETE",
      "uploadedAt": "2024-08-28T11:05:00Z"
    }
  ],
  "totalElements": 3,
  "totalPages": 1
}
```

#### Create Recipe Media

**POST** `/recipe-management/recipes/{recipeId}/media`

Uploads and associates media with a recipe.

**Path Parameters:**

- `recipeId` (long): Recipe identifier

**Content-Type:** `multipart/form-data`

**Form Parameters:**

- `file` (file): Media file to upload
- `originalFilename` (string): Original filename
- `mediaType` (string): Media type (IMAGE, VIDEO, AUDIO)
- `fileSize` (long): File size in bytes
- `contentHash` (string, optional): SHA-256 hash of content

**Response (200 OK):**

```json
{
  "mediaId": 701,
  "recipeId": 123,
  "originalFilename": "cookies.jpg",
  "mediaType": "IMAGE",
  "mediaFormat": "JPEG",
  "fileSize": 1048576,
  "contentHash": "abc123def456",
  "processingStatus": "PROCESSING",
  "uploadedAt": "2024-08-28T11:05:00Z"
}
```

#### Delete Recipe Media

**DELETE** `/recipe-management/recipes/{recipeId}/media/{mediaId}`

Deletes media associated with a recipe.

**Path Parameters:**

- `recipeId` (long): Recipe identifier
- `mediaId` (long): Media identifier

**Response (200 OK):**

```json
{
  "mediaId": 701,
  "recipeId": 123,
  "deleted": true,
  "deletedAt": "2024-08-28T11:10:00Z"
}
```

### Ingredient Media

#### Get Ingredient Media

**GET** `/recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/media`

Retrieves media associated with a recipe ingredient.

**Path Parameters:**

- `recipeId` (long): Recipe identifier
- `ingredientId` (long): Ingredient identifier

**Response (200 OK):** Paginated media list (same structure as recipe media)

#### Create Ingredient Media

**POST** `/recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/media`

Uploads and associates media with an ingredient.

**Path Parameters:**

- `recipeId` (long): Recipe identifier
- `ingredientId` (long): Ingredient identifier

**Form Parameters:** Same as Create Recipe Media

**Response (200 OK):** Media creation confirmation

#### Delete Ingredient Media

**DELETE** `/recipe-management/recipes/{recipeId}/ingredients/{ingredientId}/media/{mediaId}`

Deletes media associated with an ingredient.

**Path Parameters:**

- `recipeId` (long): Recipe identifier
- `ingredientId` (long): Ingredient identifier
- `mediaId` (long): Media identifier

**Response (200 OK):** Deletion confirmation

### Step Media

#### Get Step Media

**GET** `/recipe-management/recipes/{recipeId}/steps/{stepId}/media`

Retrieves media associated with a recipe step.

**Path Parameters:**

- `recipeId` (long): Recipe identifier
- `stepId` (long): Step identifier

**Response (200 OK):** Paginated media list

#### Create Step Media

**POST** `/recipe-management/recipes/{recipeId}/steps/{stepId}/media`

Uploads and associates media with a step.

**Path Parameters:**

- `recipeId` (long): Recipe identifier
- `stepId` (long): Step identifier

**Form Parameters:** Same as Create Recipe Media

**Response (200 OK):** Media creation confirmation

#### Delete Step Media

**DELETE** `/recipe-management/recipes/{recipeId}/steps/{stepId}/media/{mediaId}`

Deletes media associated with a step.

**Path Parameters:**

- `recipeId` (long): Recipe identifier
- `stepId` (long): Step identifier
- `mediaId` (long): Media identifier

**Response (200 OK):** Deletion confirmation

---

## Data Models

### Recipe Data Model

```json
{
  "recipeId": 123,
  "title": "Chocolate Chip Cookies",
  "description": "Classic homemade cookies",
  "servings": 24,
  "prepTime": 15,
  "cookTime": 12,
  "totalTime": 27,
  "difficulty": "EASY",
  "createdAt": "2024-08-28T10:30:00Z",
  "updatedAt": "2024-08-28T10:30:00Z",
  "createdBy": 456,
  "averageRating": 4.5,
  "reviewCount": 12
}
```

### Ingredient Data Model

```json
{
  "ingredientId": 789,
  "name": "All-purpose flour",
  "quantity": 2.25,
  "unit": "CUPS",
  "notes": "Sifted",
  "order": 1
}
```

### Step Data Model

```json
{
  "stepId": 101,
  "stepNumber": 1,
  "instruction": "Preheat oven to 375°F (190°C)",
  "duration": 5,
  "order": 1
}
```

### Tag

```json
{
  "tagId": 201,
  "name": "dessert",
  "category": "CATEGORY",
  "description": "Sweet treats and desserts",
  "usageCount": 150
}
```

### Review Data Model

```json
{
  "reviewId": 501,
  "userId": 456,
  "rating": 5,
  "comment": "Absolutely delicious!",
  "createdAt": "2024-08-28T10:55:00Z",
  "updatedAt": "2024-08-28T10:55:00Z"
}
```

### Media

```json
{
  "mediaId": 701,
  "originalFilename": "cookies.jpg",
  "mediaType": "IMAGE",
  "mediaFormat": "JPEG",
  "fileSize": 1048576,
  "contentHash": "abc123def456",
  "processingStatus": "COMPLETE",
  "uploadedAt": "2024-08-28T11:05:00Z"
}
```

## Enums

### Difficulty Level

- `EASY` - Simple recipes requiring basic skills
- `MEDIUM` - Moderate complexity recipes
- `HARD` - Advanced recipes requiring specialized skills
- `EXPERT` - Professional-level recipes

### Ingredient Unit

- `CUPS`, `TABLESPOONS`, `TEASPOONS`
- `POUNDS`, `OUNCES`, `GRAMS`, `KILOGRAMS`
- `LITERS`, `MILLILITERS`, `FLUID_OUNCES`
- `PIECES`, `CLOVES`, `PINCH`, `DASH`

### Media Type

- `IMAGE` - Photo or image content
- `VIDEO` - Video content
- `AUDIO` - Audio content

### Media Format

**Image Formats:**

- `JPEG`, `PNG`, `WebP`, `AVIF`, `GIF`

**Video Formats:**

- `MP4`, `WebM`, `MOV`, `AVI`

**Audio Formats:**

- `MP3`, `WAV`, `FLAC`, `OGG`

### Processing Status

- `PENDING` - Awaiting processing
- `PROCESSING` - Currently being processed
- `COMPLETE` - Processing completed successfully
- `FAILED` - Processing failed

## Error Codes

### Client Errors (4xx)

- `400 Bad Request` - Invalid request format or parameters
- `401 Unauthorized` - Missing or invalid authentication
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource does not exist
- `409 Conflict` - Resource already exists or conflicts with current state
- `422 Unprocessable Entity` - Validation errors

### Server Errors (5xx)

- `500 Internal Server Error` - Unexpected server error
- `502 Bad Gateway` - External service unavailable
- `503 Service Unavailable` - Service temporarily unavailable
- `504 Gateway Timeout` - External service timeout

## Rate Limiting

API requests are subject to rate limiting:

- **Authenticated Users**: 1000 requests per hour
- **Media Uploads**: 100 uploads per hour
- **Search Requests**: 200 searches per hour

Rate limit headers are included in responses:

```http
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1693224600
```

## Validation Rules

### Recipe

- `title`: 1-200 characters, required
- `description`: Max 2000 characters
- `servings`: Positive integer, max 1000
- `prepTime`: Non-negative integer (minutes)
- `cookTime`: Non-negative integer (minutes)

### Ingredient

- `name`: 1-100 characters, required
- `quantity`: Positive number
- `unit`: Valid enum value, required
- `notes`: Max 500 characters

### Step

- `instruction`: 1-1000 characters, required
- `duration`: Non-negative integer (minutes)

### Review

- `rating`: Integer 1-5, required
- `comment`: Max 2000 characters

## External Service Integration

The Recipe Management Service integrates with:

### User Management Service

- **Purpose**: Authentication and user data
- **Authentication**: JWT token validation
- **Endpoints Used**: User profile lookup, permission validation

### Media Management Service

- **Purpose**: File storage and processing
- **Authentication**: Service-to-service JWT
- **Features**: Upload, processing, retrieval, deletion

### Recipe Scraper Service

- **Purpose**: Import recipes from external sources
- **Authentication**: Service-to-service JWT
- **Features**: URL scraping, ingredient parsing, nutrition data

## Monitoring and Health Checks

### Health Check Endpoints

- **Application Health**: `/actuator/health`
- **Readiness Probe**: `/actuator/health/readiness`
- **Liveness Probe**: `/actuator/health/liveness`

### Metrics

- **Prometheus Metrics**: `/actuator/prometheus`
- **Application Info**: `/actuator/info`
- **Environment**: `/actuator/env`

## Development and Testing

### Test Data

The service includes comprehensive test fixtures:

- Sample recipes with various complexity levels
- Test users with different permission levels
- Media files for upload testing
- Ingredient and step data

### Environment Variables

Key configuration variables:

- `JWT_SECRET`: JWT signing secret (must match user-management-service)
- `POSTGRES_HOST`: Database host
- `POSTGRES_PORT`: Database port
- `POSTGRES_DB`: Database name
- `POSTGRES_SCHEMA`: Database schema (default: recipe_management)

## Support

For questions or issues:

- **Documentation**: See `/docs/` directory for architecture and setup guides
- **Testing**: Use Postman collections in `/tests/postman/`
- **Issues**: Report bugs via project issue tracker

---

## Generated Documentation

Generated for Recipe Management Service - Spring Boot 3.5.3 with Java 21
