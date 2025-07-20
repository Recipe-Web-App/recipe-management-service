Feature: Delete Media from Recipe Endpoint

  Scenario: Delete media from a recipe
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/media'
    When method DELETE
    Then status 200
