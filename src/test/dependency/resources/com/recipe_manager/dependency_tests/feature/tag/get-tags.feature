Feature: Get Tags Endpoint

  Scenario: Get tags for a recipe
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/tags'
    When method GET
    Then status 200
