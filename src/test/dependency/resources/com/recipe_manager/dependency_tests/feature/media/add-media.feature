Feature: Add Media to Recipe Endpoint

  Scenario: Add media to a recipe
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/media'
    And request { "mediaUrl": "http://example.com/recipe-image.jpg" }
    When method POST
    Then status 200
