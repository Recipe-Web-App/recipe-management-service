Feature: Get Recipe Endpoint

  Background:
    * url baseUrl + '/recipe-management/recipes'
    * def createRecipeRequest =
      """
      {
        "title": "Test Recipe for Get",
        "description": "A test recipe for get endpoint testing",
        "originUrl": "https://example.com/get-test-recipe",
        "servings": 3,
        "preparationTime": 10,
        "cookingTime": 25,
        "difficulty": "EASY",
        "ingredients": [
          {
            "ingredientName": "Tomato",
            "quantity": 2.0,
            "unit": "PIECE",
            "isOptional": false
          },
          {
            "ingredientName": "Basil",
            "quantity": 1.0,
            "unit": "TSP",
            "isOptional": true
          }
        ],
        "steps": [
          {
            "stepNumber": 1,
            "instruction": "Slice the tomatoes",
            "optional": false
          },
          {
            "stepNumber": 2,
            "instruction": "Add basil on top",
            "optional": true
          }
        ]
      }
      """

  @smoke
  Scenario: Successfully retrieve an existing recipe by ID
    # First create a recipe to retrieve
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Now get the recipe
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    When method GET
    Then status 200
    And match response.recipeId == recipeId
    And match response.title == 'Test Recipe for Get'
    And match response.description == 'A test recipe for get endpoint testing'
    And match response.originUrl == 'https://example.com/get-test-recipe'
    And match response.servings == 3
    And match response.preparationTime == 10
    And match response.cookingTime == 25
    And match response.difficulty == 'EASY'
    And match response.userId == '#uuid'
    And match response.createdAt == '#string'
    And match response.updatedAt == '#string'
    And match response.ingredients == '#array'
    And match response.ingredients.length == 2
    And match response.ingredients[0].ingredientName == 'Tomato'
    And match response.ingredients[0].quantity == 2.0
    And match response.ingredients[0].unit == 'PIECE'
    And match response.ingredients[0].isOptional == false
    And match response.ingredients[1].ingredientName == 'Basil'
    And match response.ingredients[1].quantity == 1.0
    And match response.ingredients[1].unit == 'TSP'
    And match response.ingredients[1].isOptional == true
    And match response.steps == '#array'
    And match response.steps.length == 2
    And match response.steps[0].stepNumber == 1
    And match response.steps[0].instruction == 'Slice the tomatoes'
    And match response.steps[0].optional == false
    And match response.steps[1].stepNumber == 2
    And match response.steps[1].instruction == 'Add basil on top'
    And match response.steps[1].optional == true
    And match responseHeaders['X-Request-ID'] == '#present'
    And match responseHeaders['Content-Type'][0] contains 'application/json'

  @not-found
  Scenario: Return 404 for non-existent recipe ID
    Given url baseUrl + '/recipe-management/recipes/999999'
    When method GET
    Then status 404
    And match response.timestamp == '#string'
    And match response.status == 404
    And match response.error == '#string'
    And match response.message contains '999999'
    And match responseHeaders['X-Request-ID'] == '#present'

  @validation
  Scenario: Return 400 for invalid recipe ID format
    Given url baseUrl + '/recipe-management/recipes/invalid-id'
    When method GET
    Then status 400
    And match response.timestamp == '#string'
    And match response.status == 400
    And match response.error == '#string'
    And match response.message contains 'invalid-id'
    And match responseHeaders['X-Request-ID'] == '#present'

  @validation
  Scenario: Return 400 for null recipe ID
    Given url baseUrl + '/recipe-management/recipes/null'
    When method GET
    Then status 400
    And match response.timestamp == '#string'
    And match response.status == 400
    And match response.error == '#string'
    And match responseHeaders['X-Request-ID'] == '#present'

  @auth
  Scenario: Reject get recipe without authentication (401 Unauthorized)
    # First create a recipe to retrieve
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Try to get without authentication
    * configure headers = {}
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    When method GET
    Then status 401
    And match response.timestamp == '#string'
    And match response.status == 401
    And match response.error == 'Authentication failed'
    And match responseHeaders['X-Request-ID'] == '#present'

  @edge-cases
  Scenario: Get recipe with minimal data successfully
    * def minimalRecipeRequest =
      """
      {
        "title": "Minimal Recipe",
        "servings": 1,
        "ingredients": [
          {
            "ingredientName": "Water",
            "quantity": 1.0,
            "unit": "CUP",
            "isOptional": false
          }
        ],
        "steps": [
          {
            "stepNumber": 1,
            "instruction": "Pour water",
            "optional": false
          }
        ]
      }
      """

    # Create minimal recipe
    Given request minimalRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Get minimal recipe
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    When method GET
    Then status 200
    And match response.recipeId == recipeId
    And match response.title == 'Minimal Recipe'
    And match response.servings == 1
    And match response.description == '#null'
    And match response.originUrl == '#null'
    And match response.preparationTime == '#null'
    And match response.cookingTime == '#null'
    And match response.difficulty == '#null'
    And match response.ingredients.length == 1
    And match response.steps.length == 1
    And match responseHeaders['X-Request-ID'] == '#present'

  @edge-cases
  Scenario: Get recipe with numeric ID edge cases
    # First create a recipe to retrieve
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Test with leading zeros (should still work)
    * def paddedId = '000' + recipeId
    Given url baseUrl + '/recipe-management/recipes/' + paddedId
    When method GET
    Then status 200
    And match response.recipeId == recipeId
    And match responseHeaders['X-Request-ID'] == '#present'
