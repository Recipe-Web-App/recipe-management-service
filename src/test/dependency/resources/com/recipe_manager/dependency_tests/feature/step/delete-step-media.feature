Feature: Delete Step Media Endpoint

  Scenario: Delete media from a step
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/steps/789/media'
    When method DELETE
    Then status 200
