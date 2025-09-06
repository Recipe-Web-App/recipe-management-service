Feature: Delete Recipe Review Endpoint

  Scenario: Delete a review from a recipe
    Given url baseUrl + '/api/v1/recipe-management/recipe-management/recipes/123/review'
    When method DELETE
    Then status 200
