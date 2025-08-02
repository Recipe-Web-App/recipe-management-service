Feature: Delete Recipe Endpoint

  Background:
    * url baseUrl + '/recipe-management/recipes'
    * def createRecipeRequest =
      """
      {
        "title": "Test Recipe for Delete",
        "description": "A test recipe for delete endpoint testing",
        "originUrl": "https://example.com/delete-test-recipe",
        "servings": 2,
        "preparationTime": 5,
        "cookingTime": 15,
        "difficulty": "BEGINNER",
        "ingredients": [
          {
            "ingredientName": "Bread",
            "quantity": 2.0,
            "unit": "SLICE",
            "isOptional": false
          }
        ],
        "steps": [
          {
            "stepNumber": 1,
            "instruction": "Toast the bread",
            "optional": false
          }
        ]
      }
      """

  @smoke
  Scenario: Successfully delete an existing recipe by ID
    # First create a recipe to delete
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Verify recipe exists
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    When method GET
    Then status 200
    And match response.recipeId == recipeId

    # Now delete the recipe
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    When method DELETE
    Then status 204
    And match response == ''
    And match responseHeaders['X-Request-ID'] == '#present'

    # Verify recipe is deleted by trying to get it
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    When method GET
    Then status 404

  @not-found
  Scenario: Return 404 for non-existent recipe ID
    Given url baseUrl + '/recipe-management/recipes/999999'
    When method DELETE
    Then status 404
    And match response.timestamp == '#string'
    And match response.status == 404
    And match response.error == '#string'
    And match response.message contains '999999'
    And match responseHeaders['X-Request-ID'] == '#present'

  @validation
  Scenario: Return 400 for invalid recipe ID format
    Given url baseUrl + '/recipe-management/recipes/invalid-id'
    When method DELETE
    Then status 400
    And match response.timestamp == '#string'
    And match response.status == 400
    And match response.error == '#string'
    And match response.message contains 'invalid-id'
    And match responseHeaders['X-Request-ID'] == '#present'

  @validation
  Scenario: Return 400 for null recipe ID
    Given url baseUrl + '/recipe-management/recipes/null'
    When method DELETE
    Then status 400
    And match response.timestamp == '#string'
    And match response.status == 400
    And match response.error == '#string'
    And match responseHeaders['X-Request-ID'] == '#present'

  @auth
  Scenario: Reject delete recipe without authentication (401 Unauthorized)
    # First create a recipe to delete
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Try to delete without authentication
    * configure headers = {}
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    When method DELETE
    Then status 401
    And match response.timestamp == '#string'
    And match response.status == 401
    And match response.error == 'Authentication failed'
    And match responseHeaders['X-Request-ID'] == '#present'

  @auth
  Scenario: Reject delete recipe by non-owner (403 Forbidden)
    # First create a recipe to delete
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Try to delete with different user context (simulated by changing headers)
    * configure headers = { 'Authorization': 'Bearer fake-token-different-user' }
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    When method DELETE
    Then status 403
    And match response.timestamp == '#string'
    And match response.status == 403
    And match response.error == 'Access denied'
    And match responseHeaders['X-Request-ID'] == '#present'

  @edge-cases
  Scenario: Delete recipe with minimal data successfully
    * def minimalRecipeRequest =
      """
      {
        "title": "Minimal Recipe to Delete",
        "servings": 1,
        "ingredients": [
          {
            "ingredientName": "Salt",
            "quantity": 0.5,
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

    # Create minimal recipe
    Given request minimalRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Delete minimal recipe
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    When method DELETE
    Then status 204
    And match response == ''
    And match responseHeaders['X-Request-ID'] == '#present'

    # Verify deletion
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    When method GET
    Then status 404

  @edge-cases
  Scenario: Delete recipe with numeric ID edge cases
    # First create a recipe to delete
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Test with leading zeros (should still work)
    * def paddedId = '000' + recipeId
    Given url baseUrl + '/recipe-management/recipes/' + paddedId
    When method DELETE
    Then status 204
    And match response == ''
    And match responseHeaders['X-Request-ID'] == '#present'

    # Verify deletion
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    When method GET
    Then status 404

  @edge-cases
  Scenario: Attempt to delete already deleted recipe
    # First create a recipe to delete
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Delete the recipe first time
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    When method DELETE
    Then status 204

    # Try to delete the same recipe again
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    When method DELETE
    Then status 404
    And match response.timestamp == '#string'
    And match response.status == 404
    And match response.error == '#string'
    And match responseHeaders['X-Request-ID'] == '#present'

  @integration
  Scenario: Delete recipe and verify related operations fail
    # Create a recipe
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Verify recipe exists and can be retrieved
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    When method GET
    Then status 200
    And match response.recipeId == recipeId

    # Delete the recipe
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    When method DELETE
    Then status 204

    # Verify GET returns 404
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    When method GET
    Then status 404

    # Verify UPDATE returns 404
    * def updateRequest =
      """
      {
        "title": "Updated Title",
        "servings": 5,
        "ingredients": [
          {
            "ingredientName": "New Ingredient",
            "quantity": 1.0,
            "unit": "CUP",
            "isOptional": false
          }
        ],
        "steps": [
          {
            "stepNumber": 1,
            "instruction": "New instruction",
            "optional": false
          }
        ]
      }
      """
    Given url baseUrl + '/recipe-management/recipes/' + recipeId
    And request updateRequest
    When method PUT
    Then status 404
