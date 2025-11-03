Feature: Add Recipe Comment Endpoint

  Background:
    * url baseUrl + '/recipes'
    * def createRecipeRequest =
      """
      {
        "title": "Test Recipe for Adding Comments",
        "description": "A recipe to test comment addition",
        "prepTime": 10,
        "cookTime": 20,
        "servingSize": 4,
        "difficultyLevel": "EASY",
        "ingredients": [
          {
            "name": "Test Ingredient",
            "quantity": 1.0,
            "unit": "CUP"
          }
        ],
        "steps": [
          {
            "stepNumber": 1,
            "instruction": "Mix ingredients"
          }
        ]
      }
      """

  @smoke
  Scenario: Successfully add public comment to recipe
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    Given url baseUrl + '/recipes/' + recipeId + '/comments'
    And request { commentText: 'This is a great recipe!', isPublic: true }
    When method POST
    Then status 200
    And match response.commentId == '#present'
    And match response.recipeId == recipeId
    And match response.userId == '#present'
    And match response.commentText == 'This is a great recipe!'
    And match response.isPublic == true
    And match response.createdAt == '#present'
    And match response.updatedAt == '#present'
    And match responseHeaders['X-Request-ID'][0] == '#present'

  Scenario: Successfully add private comment to recipe
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    Given url baseUrl + '/recipes/' + recipeId + '/comments'
    And request { commentText: 'Private note to self', isPublic: false }
    When method POST
    Then status 200
    And match response.commentId == '#present'
    And match response.commentText == 'Private note to self'
    And match response.isPublic == false
    And match responseHeaders['X-Request-ID'][0] == '#present'

  Scenario: Successfully add comment with default isPublic value
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    Given url baseUrl + '/recipes/' + recipeId + '/comments'
    And request { commentText: 'Comment without isPublic field' }
    When method POST
    Then status 200
    And match response.commentId == '#present'
    And match response.commentText == 'Comment without isPublic field'
    And match response.isPublic == true
    And match responseHeaders['X-Request-ID'][0] == '#present'

  Scenario: Return 400 when adding comment with blank text
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    Given url baseUrl + '/recipes/' + recipeId + '/comments'
    And request { commentText: '', isPublic: true }
    When method POST
    Then status 400
    And match response.error == '#present'
    And match responseHeaders['X-Request-ID'][0] == '#present'

  Scenario: Return 400 when adding comment with null commentText
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    Given url baseUrl + '/recipes/' + recipeId + '/comments'
    And request { commentText: null, isPublic: true }
    When method POST
    Then status 400
    And match response.error == '#present'
    And match responseHeaders['X-Request-ID'][0] == '#present'

  Scenario: Return 404 when adding comment to non-existent recipe
    Given url baseUrl + '/recipes/999999/comments'
    And request { commentText: 'Test comment', isPublic: true }
    When method POST
    Then status 404
    And match response.error == 'Resource not found'
    And match responseHeaders['X-Request-ID'][0] == '#present'
