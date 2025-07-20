Feature: Add Ingredient Media Endpoint

  Scenario: Add media to an ingredient
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/ingredients/456/media'
    And request { "mediaUrl": "http://example.com/image.jpg" }
    When method POST
    Then status 200
