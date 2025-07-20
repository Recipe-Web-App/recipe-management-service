Feature: Update Recipe Endpoint

  Scenario: Update a recipe by ID
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123'
    And request { "name": "Updated Recipe" }
    When method PUT
    Then status 200
