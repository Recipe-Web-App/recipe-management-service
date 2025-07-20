Feature: Add Recipe Review Endpoint

  Scenario: Add a review to a recipe
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/review'
    And request { "review": "Great recipe!" }
    When method POST
    Then status 200
