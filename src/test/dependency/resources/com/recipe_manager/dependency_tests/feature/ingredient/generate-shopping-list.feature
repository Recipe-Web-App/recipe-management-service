Feature: Generate Shopping List Endpoint

  Scenario: Generate a shopping list for a recipe
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/ingredients/shopping-list'
    When method GET
    Then status 200
