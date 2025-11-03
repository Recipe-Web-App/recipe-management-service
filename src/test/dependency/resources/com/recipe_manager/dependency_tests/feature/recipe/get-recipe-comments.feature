Feature: Get Recipe Comments Endpoint

  Background:
    * url baseUrl + '/recipes'
    * def createRecipeRequest =
      """
      {
        "title": "Test Recipe for Comments",
        "description": "A recipe to test comment retrieval",
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
  Scenario: Successfully retrieve empty comments list for new recipe
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    Given url baseUrl + '/recipes/' + recipeId + '/comments'
    When method GET
    Then status 200
    And match response.recipeId == recipeId
    And match response.comments == '#array'
    And match response.comments == '#[0]'
    And match responseHeaders['X-Request-ID'][0] == '#present'

  Scenario: Successfully retrieve comments after adding comments
    Given request createRecipeRequest
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Add first comment
    Given url baseUrl + '/recipes/' + recipeId + '/comments'
    And request { commentText: 'First comment', isPublic: true }
    When method POST
    Then status 200

    # Add second comment
    Given url baseUrl + '/recipes/' + recipeId + '/comments'
    And request { commentText: 'Second comment', isPublic: false }
    When method POST
    Then status 200

    # Get all comments
    Given url baseUrl + '/recipes/' + recipeId + '/comments'
    When method GET
    Then status 200
    And match response.recipeId == recipeId
    And match response.comments == '#array'
    And match response.comments == '#[2]'
    And match response.comments[0].commentText == 'First comment'
    And match response.comments[0].isPublic == true
    And match response.comments[1].commentText == 'Second comment'
    And match response.comments[1].isPublic == false
    And match responseHeaders['X-Request-ID'][0] == '#present'

  Scenario: Return 404 when retrieving comments for non-existent recipe
    Given url baseUrl + '/recipes/999999/comments'
    When method GET
    Then status 404
    And match response.error == 'Resource not found'
    And match responseHeaders['X-Request-ID'][0] == '#present'
