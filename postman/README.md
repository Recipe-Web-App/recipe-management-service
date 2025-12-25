# Recipe Management Service - Postman Collection

This directory contains a comprehensive Postman collection and environment files
for testing the Recipe Management Service API, generated from the accurate
OpenAPI specification.

## Files

- **`Recipe-Management-Service.postman_collection.json`** - Complete API
  collection with all 46 endpoints organized by functionality
- **`Recipe-Management-Local.postman_environment.json`** - Local development
  environment variables
- **`Recipe-Management-Development.postman_environment.json`** - Development
  environment variables

## Collection Features

### Complete API Coverage

The collection includes all 46 endpoints from the OpenAPI specification:

#### Recipe Management (9 endpoints)

- Get All Recipes (with pagination and sorting)
- Get Recipe by ID
- Create Recipe
- Update Recipe
- Delete Recipe
- Get/Update Recipe Description
- Get Recipe History
- Search Recipes

#### Ingredients (7 endpoints)

- Get Recipe Ingredients
- Add/Update/Delete Recipe Ingredients
- Scale Recipe Ingredients
- Generate Shopping List
- Ingredient Comments (Get/Add/Update/Delete)

#### Steps (5 endpoints)

- Get Recipe Steps
- Add/Update/Delete Recipe Steps
- Step Comments (Get/Add/Update/Delete)

#### Media Management (9 endpoints)

- Recipe Media (Get/Create/Delete)
- Ingredient Media (Get/Create/Delete)
- Step Media (Get/Create/Delete)

#### Reviews (4 endpoints)

- Get Recipe Reviews
- Add/Update/Delete Recipe Reviews

#### Tags (7 endpoints)

- Get All Tags
- Get Popular Tags
- Search Tags
- Get Tags by Category
- Recipe Tags (Get/Add/Remove)

#### Health & Monitoring (8 endpoints)

- Application Health
- Readiness/Liveness Health Checks
- Application Info
- Metrics & Prometheus
- Environment & Configuration

### Authentication

- Bearer token authentication using `{{accessToken}}` variable
- Automatic JWT token management (when integrated with auth service)
- Support for both regular user and admin user tokens

### Request Features

- Comprehensive test scripts for status code validation
- Response structure validation
- Automatic extraction of created resource IDs
- Request ID generation for tracking
- Proper content-type headers for all requests

### URL Structure

All requests use the correct URL structure: `{{baseUrl}}/api/v1/recipe-management/*`

## Environment Setup

### 1. Import Files

1. Import `Recipe-Management-Service.postman_collection.json` into Postman
2. Import both environment files into Postman

### 2. Create Private Environment Files

The environment files contain placeholder passwords. Create private versions:

```bash
# Copy environment files with -Private suffix
cp Recipe-Management-Local.postman_environment.json Recipe-Management-Local-Private.postman_environment.json
cp Recipe-Management-Development.postman_environment.json Recipe-Management-Development-Private.postman_environment.json
```

### 3. Add Real Credentials

Edit your `-Private` files and replace:

- `REPLACE_WITH_YOUR_TEST_USER_PASSWORD` → Your actual test user password
- `REPLACE_WITH_YOUR_ADMIN_USER_PASSWORD` → Your actual admin user password

### 4. Import Private Environments

Import your `-Private.postman_environment.json` files into Postman (these are
automatically gitignored).

## Environment Variables

### Base URLs

- **`baseUrl`** - Recipe Management Service URL
- **`authServiceBaseUrl`** - User Management/Auth Service URL

### Local Environment

- Recipe Management: `http://localhost:8080/api/v1/recipe-management`
- Auth Service: `http://localhost:8081/api/v1/user-management`

### Development Environment

- Recipe Management: `http://sous-chef-proxy.local/api/v1/recipe-management`
- Auth Service: `http://sous-chef-proxy.local/api/v1/user-management`

### Authentication (Auto-managed)

- `accessToken` - Current user access token
- `refreshToken` - Current user refresh token
- `adminAccessToken` - Admin user access token
- `adminRefreshToken` - Admin user refresh token
- `userId` - Current user ID
- `adminUserId` - Admin user ID

### Test Data

- `testRecipeId` - Sample recipe ID (1)
- `testIngredientId` - Sample ingredient ID (1)
- `testStepId` - Sample step ID (1)
- `testReviewId` - Sample review ID (1)
- `testTagName` - Sample tag name (vegetarian)
- `testMediaId` - Sample media ID (1)

### Dynamic Variables (Auto-set)

- `createdRecipeId` - ID from recipe creation
- `createdIngredientId` - ID from ingredient creation
- `createdStepId` - ID from step creation
- `createdReviewId` - ID from review creation
- `commentId` - ID from comment creation

## Usage

1. **Select Environment** - Choose your private environment in Postman
2. **Set Authentication** - Obtain JWT token and set `accessToken` variable
3. **Test Endpoints** - Run requests organized by functionality
4. **Chain Requests** - Use dynamic variables for request chaining

## Collection Organization

The collection is organized into logical folders:

- **Recipe Management** - Core CRUD operations
- **Ingredients** - Ingredient and shopping list management
- **Steps** - Cooking instructions and comments
- **Media** - File upload and media management (with subfolders)
- **Reviews** - Recipe reviews and ratings
- **Tags** - Recipe categorization and search
- **Actuator** - Health monitoring and metrics

## Security

- Passwords are marked as secret type in Postman
- Private environment files are gitignored
- Bearer token authentication for all protected endpoints
- Request ID generation for audit trails

This collection provides comprehensive testing coverage for all Recipe
Management Service endpoints with proper authentication, validation, and
request chaining capabilities.
