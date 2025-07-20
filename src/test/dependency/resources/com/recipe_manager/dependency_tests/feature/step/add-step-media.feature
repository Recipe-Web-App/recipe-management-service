Feature: Add Step Media Endpoint

  Scenario: Add media to a step
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/steps/789/media'
    And request { "mediaUrl": "http://example.com/step-image.jpg" }
    When method POST
    Then status 200
