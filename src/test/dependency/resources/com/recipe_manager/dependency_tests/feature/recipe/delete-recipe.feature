Feature: Delete Recipe Endpoint

  Scenario: Delete a recipe by ID
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123'
    When method DELETE
    Then status 200
