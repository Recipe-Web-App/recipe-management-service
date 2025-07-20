Feature: Delete Ingredient Media Endpoint

  Scenario: Delete media from an ingredient
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/ingredients/456/media'
    When method DELETE
    Then status 200
