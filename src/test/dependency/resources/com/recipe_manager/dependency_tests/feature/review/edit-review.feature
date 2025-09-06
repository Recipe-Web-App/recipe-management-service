Feature: Edit Recipe Review Endpoint

  Scenario: Edit a review on a recipe
    Given url baseUrl + '/api/v1/recipe-management/recipe-management/recipes/123/review'
    And request { "review": "Updated review text." }
    When method PUT
    Then status 200
