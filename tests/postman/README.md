# Recipe Manager API - Postman Collection

This directory contains Postman collection and environment files for
comprehensive API testing of the Recipe Manager Service.

## Files Overview

### Collection Files

- **`Recipe-Manager-Actuator.postman_collection.json`** - Health checks,
  metrics, and operational endpoints
- **`Recipe-Manager-Auth.postman_collection.json`** - User authentication
  flows (General & Admin users)
- **`Recipe-Manager-Ingredient.postman_collection.json`** - Ingredient
  management, comments, media, scaling, shopping lists
- **`Recipe-Manager-Media.postman_collection.json`** - Media operations for
  recipes, ingredients, and steps
- **`Recipe-Manager-Recipe.postman_collection.json`** - Core recipe CRUD
  and search operations
- **`Recipe-Manager-Review.postman_collection.json`** - Recipe review and
  rating management
- **`Recipe-Manager-Step.postman_collection.json`** - Recipe step
  management with comments and media
- **`Recipe-Manager-Tag.postman_collection.json`** - Tag management and
  search operations

### Environment Files

- **`Recipe-Manager-Development.postman_environment.json`** - Development
  environment variables (passwords as placeholders)
- **`Recipe-Manager-Local.postman_environment.json`** - Local development
  environment variables (passwords as placeholders)
- **`*-Private.postman_environment.json`** - Local-only files with real
  passwords (gitignored)

### Setup Instructions

#### 1. Import Collections and Environments

1. **Import Collections:**
   - Open Postman
   - Click "Import" button
   - Select all `Recipe-Manager-*.postman_collection.json` files
   - Collections will appear in your workspace

2. **Import Environment Templates:**
   - Import both environment files:
     `Recipe-Manager-Development.postman_environment.json` and
     `Recipe-Manager-Local.postman_environment.json`

#### 2. Set Up Private Environment with Passwords

The environment files in Git have placeholder values for passwords. To use them locally:

1. **Create Private Environment Files:**

   ```bash
   # Copy the environment files and add '-Private' suffix
   cp Recipe-Manager-Development.postman_environment.json \
      Recipe-Manager-Development-Private.postman_environment.json
   cp Recipe-Manager-Local.postman_environment.json \
      Recipe-Manager-Local-Private.postman_environment.json
   ```

2. **Add Real Passwords:**
   Edit your `-Private` files and replace these placeholder values:
   - `REPLACE_WITH_YOUR_TEST_USER_PASSWORD` → Your actual test user password
   - `REPLACE_WITH_YOUR_ADMIN_USER_PASSWORD` → Your actual admin user password

3. **Import Private Environments:**
   - Import your `-Private.postman_environment.json` files into Postman
   - Use these private environments for actual testing
   - The `-Private` files are automatically gitignored

4. **Select Environment:**
   - Choose the appropriate private environment from the dropdown in
     Postman's top-right corner

## Collection Structure

### 1. Recipe-Manager-Auth Collection

Complete authentication flows for both general users and admin users:

#### General User Flow

- **Register General User** - Creates new user account with token extraction
- **Login General User (Username)** - Login with username, extracts tokens
- **Login General User (Email)** - Login with email, extracts tokens
- **Refresh General User Token** - Refreshes access token using refresh
  token
- **Logout General User** - Logs out and clears all auth variables

#### Admin User Flow

- **Register Admin User** - Creates admin account with separate token
  variables
- **Login Admin User (Username)** - Admin login with username
- **Login Admin User (Email)** - Admin login with email
- **Refresh Admin User Token** - Refreshes admin tokens
- **Logout Admin User** - Admin logout with token cleanup

### 2. Recipe-Manager-Recipe Collection

Core recipe CRUD operations and search functionality:

- **Create Recipe** - Creates new recipe with ingredients and steps
- **Get All Recipes** - Retrieves all recipes with array validation
- **Get Recipe by ID** - Retrieves specific recipe with field validation
- **Update Recipe** - Updates existing recipe with validation checks
- **Delete Recipe** - Deletes recipe with status code validation
- **Search Recipes** - Advanced recipe search with multiple criteria

### 3. Recipe-Manager-Ingredient Collection

Ingredient management with comments, media, and utility features:

- **Get Recipe Ingredients / Get All Ingredients** - Retrieve ingredient
  data
- **Add/Edit/Delete Ingredient Comments** - User feedback on
  ingredients
- **Add/Update/Delete Ingredient Media** - Photos and videos for
  ingredients
- **Scale Recipe Ingredients** - Adjust quantities for different serving
  sizes
- **Generate Shopping List** - Create shopping lists from recipes

### 4. Recipe-Manager-Media Collection

Media management for recipes, ingredients, and steps:

- **Recipe Media** - Add, update, delete media for recipes
- **Ingredient Media** - Media operations for ingredients
- **Step Media** - Media operations for cooking steps

### 5. Recipe-Manager-Review Collection

Recipe review and rating system:

- **Add/Edit/Delete Recipe Reviews** - User reviews with ratings
- **Get Recipe Reviews / Get All Reviews** - Retrieve review data
- **Get Review by ID** - Individual review details

### 6. Recipe-Manager-Step Collection

Recipe step management with enhanced features:

- **Get Recipe Steps** - Retrieve cooking instructions
- **Add/Edit/Delete Step Comments** - User tips and feedback on steps
- **Add/Update/Delete Step Media** - Photos and videos for cooking steps

### 7. Recipe-Manager-Tag Collection

Tag management and recipe categorization:

- **Get Recipe Tags** - Tags associated with recipes
- **Add/Remove Tags** - Recipe categorization
- **Get All Tags / Get Popular Tags** - Browse available tags
- **Search Tags** - Find tags by query
- **Get Tags by Category** - Filter tags by type (dietary, cuisine, etc.)

### 8. Recipe-Manager-Actuator Collection

Operational monitoring and health checks:

- **Application Health** - Service health status
- **Application Info** - Build and version information
- **Application Metrics** - Performance metrics
- **Application Environment** - Configuration details
- **Configuration Properties** - Runtime configuration
- **Prometheus Metrics** - Metrics in Prometheus format

## Environment Variables

### Base URLs

- **`authBaseUrl`** - Authentication service base URL
- **`recipeManagerBaseUrl`** - Recipe Manager service base URL

### User Credentials (General User)

- **`testUserUsername`** - Test user username
- **`testUserEmail`** - Test user email
- **`testUserFullName`** - Test user full name
- **`testUserBio`** - Test user bio
- **`testUserPassword`** - Test user password (secret type)

### User Credentials (Admin User)

- **`adminUserUsername`** - Admin user username
- **`adminUserEmail`** - Admin user email
- **`adminUserFullName`** - Admin user full name
- **`adminUserBio`** - Admin user bio
- **`adminUserPassword`** - Admin user password (secret type)

### Authentication Tokens (Auto-managed)

These variables are automatically set by test scripts:

#### General User Tokens

- **`accessToken`** - Current access token (secret type)
- **`refreshToken`** - Current refresh token (secret type)
- **`userId`** - Current user ID
- **`username`** - Current username
- **`userEmail`** - Current user email

#### Admin User Tokens

- **`adminAccessToken`** - Admin access token (secret type)
- **`adminRefreshToken`** - Admin refresh token (secret type)
- **`adminUserId`** - Admin user ID
- **`adminUsername`** - Admin username
- **`adminUserEmail`** - Admin user email

### Test Data Variables

- **`testRecipeId`** - Sample recipe ID for testing (123)
- **`testIngredientId`** - Sample ingredient ID for testing (456)
- **`testStepId`** - Sample step ID for testing (789)
- **`testReviewId`** - Sample review ID for testing (456)
- **`createdRecipeId`** - Dynamically set recipe ID from create operations

## Automatic Response Field Extraction

The collection includes robust test scripts that automatically extract
important response fields and store them as environment variables for use in
subsequent requests:

### Authentication Flow

- Login/Register requests automatically extract and store access tokens,
  refresh tokens, and user details
- Token refresh requests update stored tokens
- Logout requests clear all authentication variables

### Recipe Management

- Create Recipe requests extract the new recipe ID for use in subsequent
  operations
- All requests include status code validation and response structure
  validation

## Environment Switching

**Development Environment:**

- Auth Service: `http://user-management.local/api/v1/user-management/auth`
- Recipe Manager: `http://recipe-manager.local/api/v1/recipe-manager`

**Local Environment:**

- Auth Service: `http://user-management.local/api/v1/user-management/auth`
- Recipe Manager: `http://localhost:8080/api/v1/recipe-manager`

Switch between environments using the environment selector dropdown in
Postman's top-right corner.

## Security Features

- **Password Protection**: Sensitive passwords are excluded from Git repository
- **Private Environment Pattern**: Use local `-Private` files for
  credentials (automatically gitignored)
- **Secret Variables**: Passwords and tokens are marked as secret type in
  Postman
- **Automatic Token Management**: Access tokens are automatically
  refreshed and managed through test scripts
- **Environment Isolation**: Separate environments prevent accidental
  cross-environment requests

### Security Model

- **Git Repository**: Contains collections and environment templates with
  placeholder passwords
- **Local Development**: Uses private environment files with real
  credentials
- **Team Collaboration**: Secure sharing of API structure without
  exposing credentials

## Usage Workflow

### Getting Started

1. Import all collection files and environment templates
2. Set up private environment files with real passwords (see setup
   instructions above)
3. Select appropriate private environment (Development-Private or
   Local-Private)
4. Start with Authentication collection to establish user session
5. Use individual collections for targeted testing of specific
   functionality
6. Tokens are automatically managed - no manual intervention needed

### Typical Testing Flow

1. **Authenticate** - Use Recipe-Manager-Auth collection for user or admin
   login
2. **Test Core Operations** - Use Recipe-Manager-Recipe collection for
   CRUD operations
3. **Test Specific Features** - Use dedicated collections (Ingredients,
   Media, Reviews, Steps, Tags)
4. **Monitor Health** - Use Recipe-Manager-Actuator collection for
   operational checks
5. **Token Refresh** - Manually refresh tokens if needed during long
   sessions
6. **Logout** - Clean up session when testing complete

## Test Script Features

All requests include comprehensive test scripts that:

- Validate HTTP status codes
- Check response structure and required fields
- Extract and store important response data as environment variables
- Provide clear test result feedback
- Enable request chaining through automatic variable management

## Future Enhancements

The collection is designed for expansion and will include:

- Complete ingredient management endpoints
- Media upload and management functionality
- Recipe review and rating system
- Step-by-step cooking instructions
- Recipe tagging and categorization
- Health monitoring and metrics endpoints

This collection provides a foundation for comprehensive API testing with
automatic token management, response validation, and seamless request
chaining.
