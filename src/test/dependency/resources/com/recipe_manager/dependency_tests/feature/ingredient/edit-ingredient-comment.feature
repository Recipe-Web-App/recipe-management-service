Feature: Edit Ingredient Comment Endpoint

  Scenario: Edit a comment on an ingredient
    Given url baseUrl + '/api/v1/recipe-management/recipe-management/recipes/123/ingredients/456/comment'
    And request { "comment": "Updated comment" }
    When method PUT
    Then status 200
