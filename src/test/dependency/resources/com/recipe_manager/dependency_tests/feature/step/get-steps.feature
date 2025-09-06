Feature: Get Steps Endpoint

  Scenario: Get steps for a recipe
    Given url baseUrl + '/api/v1/recipe-management/recipe-management/recipes/123/steps'
    When method GET
    Then status 200
