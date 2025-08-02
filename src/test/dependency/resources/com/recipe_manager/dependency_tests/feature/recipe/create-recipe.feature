Feature: Create Recipe Endpoint

  Background:
    * url baseUrl + '/recipe-management/recipes'
    * def validRecipeRequest =
      """
      {
        "title": "Test Recipe",
        "description": "A delicious test recipe",
        "originUrl": "https://example.com/recipe",
        "servings": 4,
        "preparationTime": 15,
        "cookingTime": 30,
        "difficulty": "MEDIUM",
        "ingredients": [
          {
            "ingredientName": "Flour",
            "quantity": 2.0,
            "unit": "CUP",
            "isOptional": false
          },
          {
            "ingredientName": "Sugar",
            "quantity": 1.0,
            "unit": "CUP",
            "isOptional": false
          }
        ],
        "steps": [
          {
            "stepNumber": 1,
            "instruction": "Mix flour and sugar",
            "optional": false
          },
          {
            "stepNumber": 2,
            "instruction": "Bake for 30 minutes",
            "optional": false
          }
        ]
      }
      """

  @smoke
  Scenario: Successfully create a new recipe with valid data
    Given request validRecipeRequest
    When method POST
    Then status 200
    And match response.recipeId == '#number'
    And match response.recipeId == '#present'
    And match response.title == 'Test Recipe'
    And match response.description == 'A delicious test recipe'
    And match response.originUrl == 'https://example.com/recipe'
    And match response.servings == 4
    And match response.preparationTime == 15
    And match response.cookingTime == 30
    And match response.difficulty == 'MEDIUM'
    And match response.userId == '#uuid'
    And match response.createdAt == '#string'
    And match response.updatedAt == '#string'
    And match response.ingredients == '#array'
    And match response.ingredients[0].ingredientName == 'Flour'
    And match response.ingredients[0].quantity == 2.0
    And match response.ingredients[0].unit == 'CUP'
    And match response.ingredients[0].isOptional == false
    And match response.steps == '#array'
    And match response.steps[0].stepNumber == 1
    And match response.steps[0].instruction == 'Mix flour and sugar'
    And match response.steps[0].optional == false
    And match responseHeaders['X-Request-ID'] == '#present'
    And match responseHeaders['Content-Type'][0] contains 'application/json'

  @validation
  Scenario: Reject recipe creation with missing title (400 Bad Request)
    Given request { "description": "Missing title", "servings": 2, "ingredients": [], "steps": [] }
    When method POST
    Then status 400
    And match response.timestamp == '#string'
    And match response.status == 400
    And match response.error == '#string'
    And match response.message contains 'title'
    And match responseHeaders['X-Request-ID'] == '#present'

  @validation
  Scenario: Reject recipe creation with empty ingredients list (400 Bad Request)
    * def invalidRequest = validRecipeRequest
    * invalidRequest.ingredients = []
    Given request invalidRequest
    When method POST
    Then status 400
    And match response.timestamp == '#string'
    And match response.status == 400
    And match response.error == '#string'
    And match responseHeaders['X-Request-ID'] == '#present'

  @validation
  Scenario: Reject recipe creation with empty steps list (400 Bad Request)
    * def invalidRequest = validRecipeRequest
    * invalidRequest.steps = []
    Given request invalidRequest
    When method POST
    Then status 400
    And match response.timestamp == '#string'
    And match response.status == 400
    And match response.error == '#string'
    And match responseHeaders['X-Request-ID'] == '#present'

  @validation
  Scenario: Reject recipe creation with invalid difficulty level (400 Bad Request)
    * def invalidRequest = validRecipeRequest
    * invalidRequest.difficulty = 'INVALID_LEVEL'
    Given request invalidRequest
    When method POST
    Then status 400
    And match response.timestamp == '#string'
    And match response.status == 400
    And match response.error == '#string'
    And match responseHeaders['X-Request-ID'] == '#present'

  @validation
  Scenario: Reject recipe creation with negative servings (400 Bad Request)
    * def invalidRequest = validRecipeRequest
    * invalidRequest.servings = -1
    Given request invalidRequest
    When method POST
    Then status 400
    And match response.timestamp == '#string'
    And match response.status == 400
    And match response.error == '#string'
    And match responseHeaders['X-Request-ID'] == '#present'

  @validation
  Scenario: Reject recipe creation with invalid ingredient unit (400 Bad Request)
    * def invalidRequest = validRecipeRequest
    * invalidRequest.ingredients[0].unit = 'INVALID_UNIT'
    Given request invalidRequest
    When method POST
    Then status 400
    And match response.timestamp == '#string'
    And match response.status == 400
    And match response.error == '#string'
    And match responseHeaders['X-Request-ID'] == '#present'

  @validation
  Scenario: Reject recipe creation with malformed JSON (400 Bad Request)
    Given request '{ "title": "Invalid JSON" "missing comma": true }'
    When method POST
    Then status 400
    And match responseHeaders['X-Request-ID'] == '#present'

  @auth
  Scenario: Reject recipe creation without authentication (401 Unauthorized)
    * configure headers = {}
    Given request validRecipeRequest
    When method POST
    Then status 401
    And match response.timestamp == '#string'
    And match response.status == 401
    And match response.error == 'Authentication failed'
    And match responseHeaders['X-Request-ID'] == '#present'

  @edge-cases
  Scenario: Create recipe with minimal required fields
    * def minimalRequest =
      """
      {
        "title": "Minimal Recipe",
        "servings": 1,
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
    Given request minimalRequest
    When method POST
    Then status 200
    And match response.recipeId == '#number'
    And match response.title == 'Minimal Recipe'
    And match response.servings == 1
    And match response.description == '#null'
    And match response.originUrl == '#null'
    And match responseHeaders['X-Request-ID'] == '#present'

  @edge-cases
  Scenario: Create recipe with maximum length title
    * def maxTitleRequest = validRecipeRequest
    * maxTitleRequest.title = 'A'.repeat(255)
    Given request maxTitleRequest
    When method POST
    Then status 200
    And match response.recipeId == '#number'
    And match response.title == '#string'
    And match response.title.length == 255
    And match responseHeaders['X-Request-ID'] == '#present'
