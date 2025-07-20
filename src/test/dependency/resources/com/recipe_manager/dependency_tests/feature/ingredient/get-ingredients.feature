Feature: Get Ingredients Endpoint

  Scenario: Get ingredients for a recipe
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/ingredients'
    When method GET
    Then status 200
