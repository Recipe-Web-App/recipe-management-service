Feature: Get Ingredients Endpoint

  Scenario: Get ingredients for a recipe
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/ingredients'
    When method GET
    Then status 200
    And match response == { recipeId: 123, ingredients: '#array', totalCount: '#number' }
    And match header Content-Type == 'application/json'

  Scenario: Get ingredients for non-existent recipe
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/999999/ingredients'
    When method GET
    Then status 200
    And match response.recipeId == 999999
    And match response.ingredients == []
    And match response.totalCount == 0
