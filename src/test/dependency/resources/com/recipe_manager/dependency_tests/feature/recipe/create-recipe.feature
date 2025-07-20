Feature: Create Recipe Endpoint

  Scenario: Create a new recipe
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes'
    And request { "name": "Test Recipe" }
    When method POST
    Then status 200
