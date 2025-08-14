Feature: Scale Ingredients Endpoint

  Scenario: Scale ingredients for a recipe
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/ingredients/scale'
    And param quantity = 2.5
    When method GET
    Then status 200
    And match responseType == 'json'
    And match response.recipeId == 123
    And match response.ingredients == '#array'
    And match response.totalCount == '#number'

  Scenario: Scale ingredients with different scale factor
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/ingredients/scale'
    And param quantity = 0.5
    When method GET
    Then status 200
    And match response.recipeId == 123
    And match response.ingredients == '#array'
    And match response.totalCount == '#number'

  Scenario: Invalid quantity parameter
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/ingredients/scale'
    And param quantity = 'invalid'
    When method GET
    Then status 400

  Scenario: Missing quantity parameter
    Given url baseUrl + '/api/v1/recipe-manager/recipe-management/recipes/123/ingredients/scale'
    When method GET
    Then status 400
