Feature: Generate Shopping List Endpoint

  Scenario: Generate a shopping list for a recipe
    Given url baseUrl + '/api/v1/recipe-management/recipe-management/recipes/123/ingredients/shopping-list'
    When method GET
    Then status 200
