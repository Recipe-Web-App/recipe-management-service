Feature: Update Recipe Endpoint

  Background:
    * url baseUrl + '/recipe-management/recipes'
    * def validUpdateRequest =
      """
      {
        "title": "Updated Test Recipe",
        "description": "An updated delicious test recipe",
        "originUrl": "https://example.com/updated-recipe",
        "servings": 6,
        "preparationTime": 20,
        "cookingTime": 45,
        "difficulty": "HARD",
        "ingredients": [
          {
            "ingredientName": "Flour",
            "quantity": 3.0,
            "unit": "CUP",
            "isOptional": false
          },
          {
            "ingredientName": "Eggs",
            "quantity": 2.0,
            "unit": "PIECE",
            "isOptional": false
          }
        ],
        "steps": [
          {
            "stepNumber": 1,
            "instruction": "Mix flour with eggs",
            "optional": false
          },
          {
            "stepNumber": 2,
            "instruction": "Bake for 45 minutes at 350F",
            "optional": false
          }
        ]
      }
      """
    * def createRecipeRequest =
      """
      {
        "title": "Original Recipe",
        "description": "Original description",
        "servings": 4,
        "ingredients": [
          {
            "ingredientName": "Salt",
            "quantity": 1.0,
            "unit": "TSP",
            "isOptional": false
          }
        ],
        "steps": [
          {
            "stepNumber": 1,
            "instruction": "Add salt",
            "optional": false
          }
        ]
      }
      """

  @smoke
  Scenario: Successfully update an existing recipe with valid data
    # First create a recipe to update
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Now update it
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    And request validUpdateRequest
    When method PUT
    Then status 200
    And match response.recipeId == recipeId
    And match response.title == 'Updated Test Recipe'
    And match response.description == 'An updated delicious test recipe'
    And match response.originUrl == 'https://example.com/updated-recipe'
    And match response.servings == 6
    And match response.preparationTime == 20
    And match response.cookingTime == 45
    And match response.difficulty == 'HARD'
    And match response.userId == '#uuid'
    And match response.createdAt == '#string'
    And match response.updatedAt == '#string'
    And match response.ingredients == '#array'
    And match response.ingredients.length == 2
    And match response.ingredients[0].ingredientName == 'Flour'
    And match response.ingredients[0].quantity == 3.0
    And match response.ingredients[0].unit == 'CUP'
    And match response.ingredients[0].isOptional == false
    And match response.steps == '#array'
    And match response.steps.length == 2
    And match response.steps[0].stepNumber == 1
    And match response.steps[0].instruction == 'Mix flour with eggs'
    And match response.steps[0].optional == false
    And match responseHeaders['X-Request-ID'] == '#present'
    And match responseHeaders['Content-Type'][0] contains 'application/json'

  @validation
  Scenario: Reject recipe update with missing title (400 Bad Request)
    # First create a recipe to update
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Try to update with invalid data
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    And request { "description": "Missing title", "servings": 2, "ingredients": [], "steps": [] }
    When method PUT
    Then status 400
    And match response.timestamp == '#string'
    And match response.status == 400
    And match response.error == '#string'
    And match response.message contains 'title'
    And match responseHeaders['X-Request-ID'] == '#present'

  @validation
  Scenario: Reject recipe update with empty ingredients list (400 Bad Request)
    # First create a recipe to update
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Try to update with invalid data
    * def invalidRequest = validUpdateRequest
    * invalidRequest.ingredients = []
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    And request invalidRequest
    When method PUT
    Then status 400
    And match response.timestamp == '#string'
    And match response.status == 400
    And match response.error == '#string'
    And match responseHeaders['X-Request-ID'] == '#present'

  @validation
  Scenario: Reject recipe update with invalid difficulty level (400 Bad Request)
    # First create a recipe to update
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Try to update with invalid data
    * def invalidRequest = validUpdateRequest
    * invalidRequest.difficulty = 'SUPER_HARD'
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    And request invalidRequest
    When method PUT
    Then status 400
    And match response.timestamp == '#string'
    And match response.status == 400
    And match response.error == '#string'
    And match responseHeaders['X-Request-ID'] == '#present'

  @validation
  Scenario: Reject recipe update with zero servings (400 Bad Request)
    # First create a recipe to update
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Try to update with invalid data
    * def invalidRequest = validUpdateRequest
    * invalidRequest.servings = 0
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    And request invalidRequest
    When method PUT
    Then status 400
    And match response.timestamp == '#string'
    And match response.status == 400
    And match response.error == '#string'
    And match responseHeaders['X-Request-ID'] == '#present'

  @validation
  Scenario: Reject recipe update with malformed JSON (400 Bad Request)
    # First create a recipe to update
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Try to update with malformed JSON
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    And request '{ "title": "Invalid JSON" "missing comma": true }'
    When method PUT
    Then status 400
    And match responseHeaders['X-Request-ID'] == '#present'

  @auth
  Scenario: Reject recipe update without authentication (401 Unauthorized)
    # First create a recipe to update
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Try to update without authentication
    * configure headers = {}
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    And request validUpdateRequest
    When method PUT
    Then status 401
    And match response.timestamp == '#string'
    And match response.status == 401
    And match response.error == 'Authentication failed'
    And match responseHeaders['X-Request-ID'] == '#present'

  @auth
  Scenario: Reject recipe update by non-owner (403 Forbidden)
    # First create a recipe to update
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Try to update with different user context (simulated by changing headers)
    * def originalAuth = karate.get('authToken')
    * configure headers = { 'Authorization': 'Bearer fake-token-different-user' }
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    And request validUpdateRequest
    When method PUT
    Then status 403
    And match response.timestamp == '#string'
    And match response.status == 403
    And match response.error == 'Access denied'
    And match responseHeaders['X-Request-ID'] == '#present'

  @not-found
  Scenario: Reject recipe update for non-existent recipe (404 Not Found)
    Given url baseUrl + '/recipe-management/recipes/999999'
    And request validUpdateRequest
    When method PUT
    Then status 404
    And match response.timestamp == '#string'
    And match response.status == 404
    And match response.error == 'Recipe not found'
    And match response.message contains '999999'
    And match responseHeaders['X-Request-ID'] == '#present'

  @not-found
  Scenario: Reject recipe update with invalid recipe ID format (404 Not Found)
    Given url baseUrl + '/recipe-management/recipes/invalid-id'
    And request validUpdateRequest
    When method PUT
    Then status 404
    And match response.timestamp == '#string'
    And match response.status == 404
    And match responseHeaders['X-Request-ID'] == '#present'

  @edge-cases
  Scenario: Update recipe with minimal required fields
    # First create a recipe to update
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Update with minimal data
    * def minimalUpdateRequest =
      """
      {
        "title": "Minimal Updated Recipe",
        "servings": 1,
        "ingredients": [
          {
            "ingredientName": "Pepper",
            "quantity": 0.5,
            "unit": "TSP",
            "isOptional": false
          }
        ],
        "steps": [
          {
            "stepNumber": 1,
            "instruction": "Add pepper",
            "optional": false
          }
        ]
      }
      """
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    And request minimalUpdateRequest
    When method PUT
    Then status 200
    And match response.recipeId == recipeId
    And match response.title == 'Minimal Updated Recipe'
    And match response.servings == 1
    And match response.description == '#null'
    And match response.originUrl == '#null'
    And match responseHeaders['X-Request-ID'] == '#present'

  @edge-cases
  Scenario: Update recipe multiple times in sequence
    # First create a recipe to update
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId
    * def originalCreatedAt = response.createdAt

    # First update
    * def firstUpdate = validUpdateRequest
    * firstUpdate.title = 'First Update'
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    And request firstUpdate
    When method PUT
    Then status 200
    And match response.title == 'First Update'
    And match response.createdAt == originalCreatedAt
    And match response.updatedAt != originalCreatedAt
    * def firstUpdatedAt = response.updatedAt

    # Second update
    * def secondUpdate = validUpdateRequest
    * secondUpdate.title = 'Second Update'
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    And request secondUpdate
    When method PUT
    Then status 200
    And match response.title == 'Second Update'
    And match response.createdAt == originalCreatedAt
    And match response.updatedAt != firstUpdatedAt
    And match responseHeaders['X-Request-ID'] == '#present'
