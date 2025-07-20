Feature: Search Recipes Endpoint

  Scenario: Search for recipes
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/search'
    When method GET
    Then status 200
