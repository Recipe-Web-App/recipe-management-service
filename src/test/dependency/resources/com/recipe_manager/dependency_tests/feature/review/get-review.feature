Feature: Get Recipe Reviews Endpoint

  Scenario: Get reviews for a recipe
    Given url baseUrl + '/api/v1/recipe-management/recipe-management/recipes/123/review'
    When method GET
    Then status 200
