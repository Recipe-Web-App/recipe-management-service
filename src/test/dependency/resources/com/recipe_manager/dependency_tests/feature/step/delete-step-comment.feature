Feature: Delete Step Comment Endpoint

  Scenario: Delete a comment from a step
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/steps/789/comment'
    When method DELETE
    Then status 200
