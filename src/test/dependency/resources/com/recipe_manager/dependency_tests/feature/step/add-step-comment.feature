Feature: Add Step Comment Endpoint

  Scenario: Add a comment to a step
    Given url baseUrl + '/api/v1/recipe-management/recipe-management/recipes/123/steps/789/comment'
    And request { "comment": "Test step comment" }
    When method POST
    Then status 200
