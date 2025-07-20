Feature: Update Step Media Endpoint

  Scenario: Update media on a step
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/steps/789/media'
    And request { "mediaUrl": "http://example.com/step-image-updated.jpg" }
    When method PUT
    Then status 200
