Feature: Remove Tag Endpoint

  Scenario: Remove a tag from a recipe
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/tags'
    When method DELETE
    Then status 200
