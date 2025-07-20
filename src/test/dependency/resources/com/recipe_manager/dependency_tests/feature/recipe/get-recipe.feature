Feature: Get Recipe Endpoint

  Scenario: Get a recipe by ID
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123'
    When method GET
    Then status 200
