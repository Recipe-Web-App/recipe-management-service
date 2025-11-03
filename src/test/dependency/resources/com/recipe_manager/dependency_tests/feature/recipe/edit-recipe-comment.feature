Feature: Edit Recipe Comment Endpoint

  Background:
    * url baseUrl + '/recipes'
    * def createRecipeRequest =
      """
      {
        "title": "Test Recipe for Editing Comments",
        "description": "A recipe to test comment editing",
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
  Scenario: Successfully edit own comment
    # Create recipe
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Add comment
    Given url baseUrl + '/recipes/' + recipeId + '/comments'
    And request { commentText: 'Original comment text', isPublic: true }
    When method POST
    Then status 200
    * def commentId = response.commentId

    # Edit comment
    Given url baseUrl + '/recipes/' + recipeId + '/comments/' + commentId
    And request { commentText: 'Updated comment text' }
    When method PUT
    Then status 200
    And match response.commentId == commentId
    And match response.recipeId == recipeId
    And match response.commentText == 'Updated comment text'
    And match response.isPublic == true
    And match response.createdAt == '#present'
    And match response.updatedAt == '#present'
    And match responseHeaders['X-Request-ID'][0] == '#present'

  Scenario: Edit comment preserves isPublic value
    # Create recipe
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Add private comment
    Given url baseUrl + '/recipes/' + recipeId + '/comments'
    And request { commentText: 'Private comment', isPublic: false }
    When method POST
    Then status 200
    * def commentId = response.commentId

    # Edit comment (should preserve isPublic=false)
    Given url baseUrl + '/recipes/' + recipeId + '/comments/' + commentId
    And request { commentText: 'Updated private comment' }
    When method PUT
    Then status 200
    And match response.commentId == commentId
    And match response.commentText == 'Updated private comment'
    And match response.isPublic == false
    And match responseHeaders['X-Request-ID'][0] == '#present'

  Scenario: Return 400 when editing comment with blank text
    # Create recipe
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Add comment
    Given url baseUrl + '/recipes/' + recipeId + '/comments'
    And request { commentText: 'Original comment', isPublic: true }
    When method POST
    Then status 200
    * def commentId = response.commentId

    # Try to edit with blank text
    Given url baseUrl + '/recipes/' + recipeId + '/comments/' + commentId
    And request { commentText: '' }
    When method PUT
    Then status 400
    And match response.error == '#present'
    And match responseHeaders['X-Request-ID'][0] == '#present'

  Scenario: Return 404 when editing comment on non-existent recipe
    Given url baseUrl + '/recipes/999999/comments/1'
    And request { commentText: 'Updated text' }
    When method PUT
    Then status 404
    And match response.error == 'Resource not found'
    And match responseHeaders['X-Request-ID'][0] == '#present'

  Scenario: Return 404 when editing non-existent comment
    # Create recipe
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Try to edit non-existent comment
    Given url baseUrl + '/recipes/' + recipeId + '/comments/999999'
    And request { commentText: 'Updated text' }
    When method PUT
    Then status 404
    And match response.error == 'Resource not found'
    And match responseHeaders['X-Request-ID'][0] == '#present'
