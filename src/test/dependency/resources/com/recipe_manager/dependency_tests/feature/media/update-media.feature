Feature: Update Media on Recipe Endpoint

  Scenario: Update media on a recipe
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/media'
    And request { "mediaUrl": "http://example.com/recipe-image-updated.jpg" }
    When method PUT
    Then status 200
