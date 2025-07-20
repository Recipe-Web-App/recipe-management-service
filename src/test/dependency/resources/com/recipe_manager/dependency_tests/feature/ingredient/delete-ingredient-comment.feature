Feature: Delete Ingredient Comment Endpoint

  Scenario: Delete a comment from an ingredient
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/ingredients/456/comment'
    When method DELETE
    Then status 200
