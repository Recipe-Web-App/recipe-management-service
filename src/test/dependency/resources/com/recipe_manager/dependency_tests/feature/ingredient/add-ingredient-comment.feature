Feature: Add Ingredient Comment Endpoint

  Scenario: Add a comment to an ingredient
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/ingredients/456/comment'
    And request { "comment": "Test comment" }
    When method POST
    Then status 200
