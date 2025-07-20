Feature: Add Tag Endpoint

  Scenario: Add a tag to a recipe
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/tags'
    And request { "tag": "Test Tag" }
    When method POST
    Then status 200
