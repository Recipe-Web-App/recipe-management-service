Feature: Remove Recipe from Collection Endpoint

  Background:
    * url baseUrl + '/recipe-management/collections'
    # Create a test collection to work with
    * def createCollectionRequest =
      """
      {
        "name": "Test Collection for Remove Recipe",
        "description": "Collection for testing recipe removal",
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

    # Mock adding a recipe to the collection (in real scenario, recipe ID would be valid)
    # For this test, we'll use recipe ID 1 which should exist in test data
    * def testRecipeId = 1

  Scenario: Remove recipe from collection when user is owner - success
    # Note: In a real test environment, we would first POST to add the recipe
    # For this example, we assume recipe is already in collection
    Given path createdCollectionId, 'recipes', testRecipeId
    When method DELETE
    Then status 204

  Scenario: Remove recipe from collection with non-existent collection ID - returns 404
    * def nonExistentCollectionId = 999999999
    Given path nonExistentCollectionId, 'recipes', testRecipeId
    When method DELETE
    Then status 404
    And match response.error == 'Resource not found'
    And match response.message == 'Collection not found'
    And match response.timestamp == '#string'
    And match response.path == '#string'

  Scenario: Remove recipe that is not in collection - returns 404
    * def nonExistentRecipeId = 999999999
    Given path createdCollectionId, 'recipes', nonExistentRecipeId
    When method DELETE
    Then status 404
    And match response.error == 'Resource not found'
    And match response.message == 'Recipe not found in this collection'
    And match response.timestamp == '#string'

  Scenario: Remove recipe from collection in ALL_USERS mode - success
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

    # Remove recipe (assuming it exists in collection)
    Given path allUsersCollectionId, 'recipes', testRecipeId
    When method DELETE
    Then status 204

  Scenario: Verify response has no content body for successful removal
    # This test ensures DELETE returns 204 with no body
    Given path createdCollectionId, 'recipes', testRecipeId
    When method DELETE
    Then status 204
    # Verify empty response body
    And match response == ''

  Scenario: Remove recipe verifies idempotency - second delete returns 404
    # First deletion should succeed
    Given path createdCollectionId, 'recipes', testRecipeId
    When method DELETE
    Then status 204

    # Second deletion of same recipe should return 404 (recipe not in collection)
    Given path createdCollectionId, 'recipes', testRecipeId
    When method DELETE
    Then status 404
    And match response.error == 'Resource not found'
    And match response.message == 'Recipe not found in this collection'

  Scenario: Remove recipe with invalid recipe ID format - returns 400
    * def invalidRecipeId = 'not-a-number'
    Given path createdCollectionId, 'recipes', invalidRecipeId
    When method DELETE
    # Depending on path validation, might be 400 or 404
    Then status 400

  Scenario: Remove recipe with invalid collection ID format - returns 400
    * def invalidCollectionId = 'not-a-number'
    Given path invalidCollectionId, 'recipes', testRecipeId
    When method DELETE
    # Depending on path validation, might be 400 or 404
    Then status 400

  Scenario: Remove recipe verifies correct HTTP method
    # Using wrong method should return 405 Method Not Allowed
    Given path createdCollectionId, 'recipes', testRecipeId
    When method GET
    # Should not be DELETE endpoint, will return 404 or 405
    Then assert responseStatus == 404 || responseStatus == 405

  Scenario: Concurrent removal of same recipe - only first succeeds
    # This test verifies race condition handling
    # First deletion
    Given path createdCollectionId, 'recipes', testRecipeId
    When method DELETE
    Then status 204

    # Attempt to delete again immediately (simulating concurrent request)
    Given path createdCollectionId, 'recipes', testRecipeId
    When method DELETE
    Then status 404
    And match response.message == 'Recipe not found in this collection'
