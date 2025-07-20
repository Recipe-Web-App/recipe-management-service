Feature: Scale Ingredients Endpoint

  Scenario: Scale ingredients for a recipe
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/ingredients/scale'
    And param quantity = 2.5
    When method GET
    Then status 200
