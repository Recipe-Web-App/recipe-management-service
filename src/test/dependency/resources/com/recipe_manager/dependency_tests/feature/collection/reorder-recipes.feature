Feature: Reorder Recipes in Collection Endpoint

  Background:
    * url baseUrl + '/recipe-management/collections'
    # Create a test collection to work with
    * def createCollectionRequest =
      """
      {
        "name": "Test Collection for Reorder",
        "description": "Collection for testing recipe reordering",
        "visibility": "PUBLIC",
        "collaborationMode": "OWNER_ONLY"
      }
      """
    Given path ''
    And request createCollectionRequest
    When method POST
    Then status 201
    * def createdCollectionId = response.collectionId

    # Add two recipes to the collection (IDs 1 and 2 should exist in test data)
    * def recipe1Id = 1
    * def recipe2Id = 2

    Given path createdCollectionId, 'recipes', recipe1Id
    When method POST
    Then status 201
    * def recipe1DisplayOrder = response.displayOrder

    Given path createdCollectionId, 'recipes', recipe2Id
    When method POST
    Then status 201
    * def recipe2DisplayOrder = response.displayOrder

  Scenario: Reorder recipes successfully - swap display orders
    # Reorder: recipe 1 to order 20, recipe 2 to order 10
    * def reorderRequest =
      """
      {
        "recipes": [
          {
            "recipeId": 1,
            "displayOrder": 20
          },
          {
            "recipeId": 2,
            "displayOrder": 10
          }
        ]
      }
      """
    Given path createdCollectionId, 'recipes', 'reorder'
    And request reorderRequest
    When method PUT
    Then status 200
    And match response == '#array'
    And match response[*].recipeId contains 1
    And match response[*].recipeId contains 2
    # Response should be sorted by display order
    And match response[0].displayOrder == 10
    And match response[1].displayOrder == 20
    And match response[0].recipeTitle == '#string'
    And match response[0].recipeDescription == '#present'

  Scenario: Reorder single recipe in collection
    * def reorderRequest =
      """
      {
        "recipes": [
          {
            "recipeId": 1,
            "displayOrder": 5
          }
        ]
      }
      """
    Given path createdCollectionId, 'recipes', 'reorder'
    And request reorderRequest
    When method PUT
    Then status 200
    And match response == '#array'
    # Should return all recipes in collection, sorted by display order
    And match response[0].displayOrder == 5
    And match response[0].recipeId == 1

  Scenario: Reorder with non-existent collection ID - returns 404
    * def nonExistentCollectionId = 999999999
    * def reorderRequest =
      """
      {
        "recipes": [
          {
            "recipeId": 1,
            "displayOrder": 10
          }
        ]
      }
      """
    Given path nonExistentCollectionId, 'recipes', 'reorder'
    And request reorderRequest
    When method PUT
    Then status 404
    And match response.error == 'Resource not found'
    And match response.message == 'Collection not found'

  Scenario: Reorder with recipe not in collection - returns 404
    * def nonExistentRecipeId = 999999999
    * def reorderRequest =
      """
      {
        "recipes": [
          {
            "recipeId": #(nonExistentRecipeId),
            "displayOrder": 10
          }
        ]
      }
      """
    Given path createdCollectionId, 'recipes', 'reorder'
    And request reorderRequest
    When method PUT
    Then status 404
    And match response.error == 'Resource not found'
    And match response.message contains 'not found in this collection'

  Scenario: Reorder with duplicate display orders - returns 400
    * def reorderRequest =
      """
      {
        "recipes": [
          {
            "recipeId": 1,
            "displayOrder": 10
          },
          {
            "recipeId": 2,
            "displayOrder": 10
          }
        ]
      }
      """
    Given path createdCollectionId, 'recipes', 'reorder'
    And request reorderRequest
    When method PUT
    Then status 400
    And match response.message == 'Request contains duplicate display orders'

  Scenario: Reorder with empty recipes list - returns 400
    * def reorderRequest =
      """
      {
        "recipes": []
      }
      """
    Given path createdCollectionId, 'recipes', 'reorder'
    And request reorderRequest
    When method PUT
    Then status 400
    And match response.error == 'Validation error'

  Scenario: Reorder with invalid display order (negative) - returns 400
    * def reorderRequest =
      """
      {
        "recipes": [
          {
            "recipeId": 1,
            "displayOrder": -5
          }
        ]
      }
      """
    Given path createdCollectionId, 'recipes', 'reorder'
    And request reorderRequest
    When method PUT
    Then status 400
    And match response.error == 'Validation error'

  Scenario: Reorder verifies idempotency - same order twice
    * def reorderRequest =
      """
      {
        "recipes": [
          {
            "recipeId": 1,
            "displayOrder": 15
          },
          {
            "recipeId": 2,
            "displayOrder": 25
          }
        ]
      }
      """
    # First reorder
    Given path createdCollectionId, 'recipes', 'reorder'
    And request reorderRequest
    When method PUT
    Then status 200

    # Second reorder with same values - should succeed with same result
    Given path createdCollectionId, 'recipes', 'reorder'
    And request reorderRequest
    When method PUT
    Then status 200
    And match response[0].displayOrder == 15
    And match response[1].displayOrder == 25

  Scenario: Reorder in ALL_USERS collaboration mode - success
    # Create a new collection with ALL_USERS mode
    * def allUsersCollectionRequest =
      """
      {
        "name": "All Users Collection",
        "description": "Collection with all users mode",
        "visibility": "PUBLIC",
        "collaborationMode": "ALL_USERS"
      }
      """
    Given path ''
    And request allUsersCollectionRequest
    When method POST
    Then status 201
    * def allUsersCollectionId = response.collectionId

    # Add recipes to the new collection
    Given path allUsersCollectionId, 'recipes', 1
    When method POST
    Then status 201

    Given path allUsersCollectionId, 'recipes', 2
    When method POST
    Then status 201

    # Reorder recipes
    * def reorderRequest =
      """
      {
        "recipes": [
          {
            "recipeId": 1,
            "displayOrder": 30
          },
          {
            "recipeId": 2,
            "displayOrder": 20
          }
        ]
      }
      """
    Given path allUsersCollectionId, 'recipes', 'reorder'
    And request reorderRequest
    When method PUT
    Then status 200
    And match response[0].displayOrder == 20
    And match response[1].displayOrder == 30
