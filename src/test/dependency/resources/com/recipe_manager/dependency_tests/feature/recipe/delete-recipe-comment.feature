Feature: Delete Recipe Comment Endpoint

  Background:
    * url baseUrl + '/recipes'
    * def createRecipeRequest =
      """
      {
        "title": "Test Recipe for Deleting Comments",
        "description": "A recipe to test comment deletion",
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
  Scenario: Successfully delete own comment
    # Create recipe
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Add comment
    Given url baseUrl + '/recipes/' + recipeId + '/comments'
    And request { commentText: 'Comment to be deleted', isPublic: true }
    When method POST
    Then status 200
    * def commentId = response.commentId

    # Delete comment
    Given url baseUrl + '/recipes/' + recipeId + '/comments/' + commentId
    When method DELETE
    Then status 204
    And match responseHeaders['X-Request-ID'][0] == '#present'

    # Verify comment is deleted
    Given url baseUrl + '/recipes/' + recipeId + '/comments'
    When method GET
    Then status 200
    And match response.comments == '#[0]'

  Scenario: Successfully delete multiple comments independently
    # Create recipe
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Add first comment
    Given url baseUrl + '/recipes/' + recipeId + '/comments'
    And request { commentText: 'First comment', isPublic: true }
    When method POST
    Then status 200
    * def commentId1 = response.commentId

    # Add second comment
    Given url baseUrl + '/recipes/' + recipeId + '/comments'
    And request { commentText: 'Second comment', isPublic: true }
    When method POST
    Then status 200
    * def commentId2 = response.commentId

    # Delete first comment
    Given url baseUrl + '/recipes/' + recipeId + '/comments/' + commentId1
    When method DELETE
    Then status 204

    # Verify only second comment remains
    Given url baseUrl + '/recipes/' + recipeId + '/comments'
    When method GET
    Then status 200
    And match response.comments == '#[1]'
    And match response.comments[0].commentId == commentId2
    And match response.comments[0].commentText == 'Second comment'

  Scenario: Return 404 when deleting comment from non-existent recipe
    Given url baseUrl + '/recipes/999999/comments/1'
    When method DELETE
    Then status 404
    And match response.error == 'Resource not found'
    And match responseHeaders['X-Request-ID'][0] == '#present'

  Scenario: Return 404 when deleting non-existent comment
    # Create recipe
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Try to delete non-existent comment
    Given url baseUrl + '/recipes/' + recipeId + '/comments/999999'
    When method DELETE
    Then status 404
    And match response.error == 'Resource not found'
    And match responseHeaders['X-Request-ID'][0] == '#present'

  Scenario: Return 404 when deleting already deleted comment
    # Create recipe
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Add comment
    Given url baseUrl + '/recipes/' + recipeId + '/comments'
    And request { commentText: 'Comment to be deleted', isPublic: true }
    When method POST
    Then status 200
    * def commentId = response.commentId

    # Delete comment
    Given url baseUrl + '/recipes/' + recipeId + '/comments/' + commentId
    When method DELETE
    Then status 204

    # Try to delete again
    Given url baseUrl + '/recipes/' + recipeId + '/comments/' + commentId
    When method DELETE
    Then status 404
    And match response.error == 'Resource not found'
    And match responseHeaders['X-Request-ID'][0] == '#present'
