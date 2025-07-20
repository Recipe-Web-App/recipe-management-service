Feature: Update Ingredient Media Endpoint

  Scenario: Update media on an ingredient
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/ingredients/456/media'
    And request { "mediaUrl": "http://example.com/updated-image.jpg" }
    When method PUT
    Then status 200
