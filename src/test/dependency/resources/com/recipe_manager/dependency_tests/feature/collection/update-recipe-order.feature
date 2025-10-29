Feature: Update Recipe Display Order Endpoint

  Background:
    * url baseUrl + '/recipe-management/collections'
    # Create a test collection to work with
    * def createCollectionRequest =
      """
      {
        "name": "Test Collection for Update Order",
        "description": "Collection for testing recipe order updates",
        "visibility": "PUBLIC",
        "collaborationMode": "OWNER_ONLY"
      }
      """
    Given path ''
    And request createCollectionRequest
    When method POST
    Then status 201
    * def createdCollectionId = response.collectionId
    * def createdCollection = response

    # Use recipe ID 1 which should exist in test data
    * def testRecipeId = 1

  Scenario: Update recipe display order when user is owner - success
    # First add the recipe to the collection
    Given path createdCollectionId, 'recipes', testRecipeId
    When method POST
    Then status 201
    * def originalDisplayOrder = response.displayOrder

    # Now update the display order
    * def updateRequest = { "displayOrder": 25 }
    Given path createdCollectionId, 'recipes', testRecipeId
    And request updateRequest
    When method PATCH
    Then status 200
    And match response.recipeId == testRecipeId
    And match response.displayOrder == 25
    And match response.recipeTitle == '#string'
    And match response.addedBy == '#string'
    And match response.addedAt == '#string'

  Scenario: Update recipe order with non-existent collection ID - returns 404
    * def nonExistentCollectionId = 999999999
    * def updateRequest = { "displayOrder": 15 }
    Given path nonExistentCollectionId, 'recipes', testRecipeId
    And request updateRequest
    When method PATCH
    Then status 404
    And match response.error == 'Resource not found'
    And match response.message == 'Collection not found'
    And match response.timestamp == '#string'
    And match response.path == '#string'

  Scenario: Update order for recipe not in collection - returns 404
    * def nonExistentRecipeId = 999999999
    * def updateRequest = { "displayOrder": 15 }
    Given path createdCollectionId, 'recipes', nonExistentRecipeId
    And request updateRequest
    When method PATCH
    Then status 404
    And match response.error == 'Resource not found'
    And match response.message == 'Recipe not found in this collection'
    And match response.timestamp == '#string'

  Scenario: Update recipe order in ALL_USERS collaboration mode - success
    # Create a new collection with ALL_USERS collaboration mode
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

    # Add recipe to collection
    Given path allUsersCollectionId, 'recipes', testRecipeId
    When method POST
    Then status 201

    # Update display order
    * def updateRequest = { "displayOrder": 30 }
    Given path allUsersCollectionId, 'recipes', testRecipeId
    And request updateRequest
    When method PATCH
    Then status 200
    And match response.displayOrder == 30

  Scenario: Update order with invalid display order (0) - returns 400
    # Add recipe first
    Given path createdCollectionId, 'recipes', testRecipeId
    When method POST
    Then status 201

    # Try to update with invalid display order
    * def invalidRequest = { "displayOrder": 0 }
    Given path createdCollectionId, 'recipes', testRecipeId
    And request invalidRequest
    When method PATCH
    Then status 400
    And match response.error == 'Validation failed'

  Scenario: Update order with negative display order - returns 400
    # Add recipe first
    Given path createdCollectionId, 'recipes', testRecipeId
    When method POST
    Then status 201

    # Try to update with negative display order
    * def invalidRequest = { "displayOrder": -5 }
    Given path createdCollectionId, 'recipes', testRecipeId
    And request invalidRequest
    When method PATCH
    Then status 400
    And match response.error == 'Validation failed'

  Scenario: Update order with null display order - returns 400
    # Add recipe first
    Given path createdCollectionId, 'recipes', testRecipeId
    When method POST
    Then status 201

    # Try to update with null display order
    * def invalidRequest = { "displayOrder": null }
    Given path createdCollectionId, 'recipes', testRecipeId
    And request invalidRequest
    When method PATCH
    Then status 400
    And match response.error == 'Validation failed'

  Scenario: Update recipe order is idempotent - same order twice
    # Add recipe first
    Given path createdCollectionId, 'recipes', testRecipeId
    When method POST
    Then status 201

    # Update display order first time
    * def updateRequest = { "displayOrder": 20 }
    Given path createdCollectionId, 'recipes', testRecipeId
    And request updateRequest
    When method PATCH
    Then status 200
    And match response.displayOrder == 20

    # Update display order second time with same value
    Given path createdCollectionId, 'recipes', testRecipeId
    And request updateRequest
    When method PATCH
    Then status 200
    And match response.displayOrder == 20

  Scenario: Update recipe order returns complete recipe metadata
    # Add recipe first
    Given path createdCollectionId, 'recipes', testRecipeId
    When method POST
    Then status 201

    # Update display order
    * def updateRequest = { "displayOrder": 35 }
    Given path createdCollectionId, 'recipes', testRecipeId
    And request updateRequest
    When method PATCH
    Then status 200
    # Verify all expected fields are present
    And match response.recipeId == testRecipeId
    And match response.recipeTitle == '#string'
    And match response.recipeDescription == '#present'
    And match response.displayOrder == 35
    And match response.addedBy == '#string'
    And match response.addedAt == '#string'
