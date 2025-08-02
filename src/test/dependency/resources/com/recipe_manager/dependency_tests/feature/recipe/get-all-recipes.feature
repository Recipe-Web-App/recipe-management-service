Feature: Get All Recipes Endpoint

  Background:
    * url baseUrl + '/recipe-management/recipes'
    * def createRecipeRequest1 =
      """
      {
        "title": "Spaghetti Carbonara",
        "description": "Classic Italian pasta dish with eggs, cheese, and pancetta",
        "originUrl": "https://example.com/carbonara-recipe",
        "servings": 4,
        "preparationTime": 15,
        "cookingTime": 20,
        "difficulty": "MEDIUM",
        "ingredients": [
          {
            "ingredientName": "Spaghetti",
            "quantity": 400.0,
            "unit": "GRAMS",
            "isOptional": false
          },
          {
            "ingredientName": "Pancetta",
            "quantity": 150.0,
            "unit": "GRAMS",
            "isOptional": false
          }
        ]
      }
      """
    * def createRecipeRequest2 =
      """
      {
        "title": "Chicken Stir Fry",
        "description": "Quick and healthy stir fry",
        "originUrl": "https://example.com/stir-fry-recipe",
        "servings": 2,
        "preparationTime": 10,
        "cookingTime": 15,
        "difficulty": "EASY",
        "ingredients": [
          {
            "ingredientName": "Chicken Breast",
            "quantity": 300.0,
            "unit": "GRAMS",
            "isOptional": false
          },
          {
            "ingredientName": "Bell Pepper",
            "quantity": 1.0,
            "unit": "PIECE",
            "isOptional": false
          }
        ]
      }
      """

  Scenario: Get all recipes with default pagination - success
    # Create some test recipes first
    Given path ''
    And request createRecipeRequest1
    When method POST
    Then status 200
    * def recipe1Id = response.recipeId

    Given path ''
    And request createRecipeRequest2
    When method POST
    Then status 200
    * def recipe2Id = response.recipeId

    # Get all recipes
    Given path ''
    When method GET
    Then status 200
    And match response.recipes == '#array'
    And match response.recipes.length >= 2
    And match response.page == 0
    And match response.size == 20
    And match response.totalElements >= 2
    And match response.first == true
    And match response.numberOfElements >= 2
    And match response.empty == false

    # Cleanup
    Given path recipe1Id
    When method DELETE
    Then status 204

    Given path recipe2Id
    When method DELETE
    Then status 204

  Scenario: Get all recipes with custom pagination - success
    # Create a test recipe first
    Given path ''
    And request createRecipeRequest1
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Get all recipes with pagination
    Given path ''
    And param page = 0
    And param size = 1
    When method GET
    Then status 200
    And match response.recipes == '#array'
    And match response.page == 0
    And match response.size == 1
    And match response.totalElements >= 1
    And match response.first == true

    # Cleanup
    Given path recipeId
    When method DELETE
    Then status 204

  Scenario: Get all recipes when no recipes exist - empty result
    # Ensure we start with a clean state by getting current recipes and deleting them
    Given path ''
    When method GET
    Then status 200
    * def existingRecipes = response.recipes
    * def deleteRecipe =
      """
      function(recipe) {
        karate.set('recipeToDelete', recipe.recipeId);
        var result = karate.call('classpath:com/recipe_manager/dependency_tests/feature/recipe/delete-recipe.feature', { recipeId: recipe.recipeId });
      }
      """
    * karate.forEach(existingRecipes, deleteRecipe)

    # Now get all recipes - should be empty
    Given path ''
    When method GET
    Then status 200
    And match response.recipes == '#array'
    And match response.recipes.length == 0
    And match response.page == 0
    And match response.totalElements == 0
    And match response.totalPages == 0
    And match response.first == true
    And match response.last == true
    And match response.numberOfElements == 0
    And match response.empty == true

  Scenario: Get all recipes with invalid pagination parameters - should use defaults
    Given path ''
    And param page = -1
    And param size = 0
    When method GET
    Then status 200
    And match response.recipes == '#array'
    # Should still return valid response with corrected pagination
