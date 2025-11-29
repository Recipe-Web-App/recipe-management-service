Feature: Get My Recipes Endpoint

  Background:
    * url baseUrl + '/users/me/recipes'
    * def recipesUrl = baseUrl + '/recipes'
    * def createRecipeRequest1 =
      """
      {
        "title": "My Spaghetti Carbonara",
        "description": "My classic Italian pasta dish with eggs, cheese, and pancetta",
        "originUrl": "https://example.com/my-carbonara-recipe",
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
        "title": "My Chicken Stir Fry",
        "description": "My quick and healthy stir fry",
        "originUrl": "https://example.com/my-stir-fry-recipe",
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

  Scenario: Get my recipes with default pagination - success
    # Create some test recipes first (these will be owned by the authenticated user)
    Given url recipesUrl
    And request createRecipeRequest1
    When method POST
    Then status 200
    * def recipe1Id = response.recipeId

    Given url recipesUrl
    And request createRecipeRequest2
    When method POST
    Then status 200
    * def recipe2Id = response.recipeId

    # Get my recipes
    Given url baseUrl + '/users/me/recipes'
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

    # Verify that the recipes returned belong to the current user
    # Each recipe should have a userId field
    And match each response.recipes[*].userId == '#notnull'

    # Cleanup
    Given url recipesUrl + '/' + recipe1Id
    When method DELETE
    Then status 204

    Given url recipesUrl + '/' + recipe2Id
    When method DELETE
    Then status 204

  Scenario: Get my recipes with custom pagination - success
    # Create a test recipe first
    Given url recipesUrl
    And request createRecipeRequest1
    When method POST
    Then status 200
    * def recipeId = response.recipeId

    # Get my recipes with pagination
    Given url baseUrl + '/users/me/recipes'
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
    Given url recipesUrl + '/' + recipeId
    When method DELETE
    Then status 204

  Scenario: Get my recipes returns only user-owned recipes
    # Create a test recipe
    Given url recipesUrl
    And request createRecipeRequest1
    When method POST
    Then status 200
    * def recipeId = response.recipeId
    * def createdRecipeUserId = response.userId

    # Get my recipes and verify the recipe is returned
    Given url baseUrl + '/users/me/recipes'
    When method GET
    Then status 200
    And match response.recipes == '#array'
    And match response.totalElements >= 1

    # Find our created recipe in the response
    * def myRecipe = karate.filter(response.recipes, function(x){ return x.recipeId == recipeId })[0]
    And match myRecipe != null
    And match myRecipe.title == 'My Spaghetti Carbonara'
    And match myRecipe.userId == createdRecipeUserId

    # Cleanup
    Given url recipesUrl + '/' + recipeId
    When method DELETE
    Then status 204

  Scenario: Get my recipes with sorting parameter - success
    # Create test recipes
    Given url recipesUrl
    And request createRecipeRequest1
    When method POST
    Then status 200
    * def recipe1Id = response.recipeId

    Given url recipesUrl
    And request createRecipeRequest2
    When method POST
    Then status 200
    * def recipe2Id = response.recipeId

    # Get my recipes with sorting by createdAt descending
    Given url baseUrl + '/users/me/recipes'
    And param sort = 'createdAt,desc'
    When method GET
    Then status 200
    And match response.recipes == '#array'
    And match response.totalElements >= 2

    # Cleanup
    Given url recipesUrl + '/' + recipe1Id
    When method DELETE
    Then status 204

    Given url recipesUrl + '/' + recipe2Id
    When method DELETE
    Then status 204

  Scenario: Get my recipes when user has no recipes - empty result
    # First, get current recipes and delete them to ensure clean state
    Given url baseUrl + '/users/me/recipes'
    When method GET
    Then status 200
    * def existingRecipes = response.recipes

    # Delete each recipe owned by the current user
    * def deleteMyRecipe =
      """
      function(recipe) {
        var config = { recipeId: recipe.recipeId };
        karate.call('classpath:com/recipe_manager/dependency_tests/feature/recipe/delete-recipe.feature', config);
      }
      """
    * karate.forEach(existingRecipes, deleteMyRecipe)

    # Now get my recipes - should be empty
    Given url baseUrl + '/users/me/recipes'
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
