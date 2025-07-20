Feature: Edit Step Comment Endpoint

  Scenario: Edit a comment on a step
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/steps/789/comment'
    And request { "comment": "Updated step comment" }
    When method PUT
    Then status 200
