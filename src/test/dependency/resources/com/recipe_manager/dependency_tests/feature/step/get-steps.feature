Feature: Get Steps Endpoint

  Scenario: Get steps for a recipe
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/steps'
    When method GET
    Then status 200
