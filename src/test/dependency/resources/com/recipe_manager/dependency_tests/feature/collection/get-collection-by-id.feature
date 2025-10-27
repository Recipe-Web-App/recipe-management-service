Feature: Get Collection By ID Endpoint

  Background:
    * url baseUrl + '/recipe-management/collections'
    # Create a test collection to work with
    * def createCollectionRequest =
      """
      {
        "name": "Test Collection for Get By ID",
        "description": "A collection created for testing the get by ID endpoint",
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

  Scenario: Get collection by ID - success
    # Get the collection we just created
    Given path createdCollectionId
    When method GET
    Then status 200
    And match response.collectionId == createdCollectionId
    And match response.userId == '#uuid'
    And match response.name == 'Test Collection for Get By ID'
    And match response.description == 'A collection created for testing the get by ID endpoint'
    And match response.visibility == 'PUBLIC'
    And match response.collaborationMode == 'OWNER_ONLY'
    And match response.recipeCount == '#number'
    And match response.collaboratorCount == '#number'
    And match response.recipes == '#array'
    And match response.createdAt == '#string'
    And match response.updatedAt == '#string'
    # Verify the response matches the originally created collection
    And match response.userId == createdCollection.userId
    And match response.name == createdCollection.name
    And match response.visibility == createdCollection.visibility

  Scenario: Get collection by ID with all fields populated - success
    # The collection should have all required fields
    Given path createdCollectionId
    When method GET
    Then status 200
    And match response.collectionId == '#present'
    And match response.userId == '#present'
    And match response.name == '#present'
    And match response.description == '#present'
    And match response.visibility == '#present'
    And match response.collaborationMode == '#present'
    And match response.recipeCount == '#present'
    And match response.collaboratorCount == '#present'
    And match response.recipes == '#present'
    And match response.createdAt == '#present'
    And match response.updatedAt == '#present'

  Scenario: Get collection by ID verifies enum values - success
    Given path createdCollectionId
    When method GET
    Then status 200
    # Verify visibility is one of the valid enum values
    * def validVisibilities = ['PUBLIC', 'PRIVATE', 'FRIENDS_ONLY']
    * assert validVisibilities.includes(response.visibility)
    # Verify collaborationMode is one of the valid enum values
    * def validModes = ['OWNER_ONLY', 'SPECIFIC_USERS', 'ALL_USERS']
    * assert validModes.includes(response.collaborationMode)

  Scenario: Get collection by ID returns proper recipe list structure - success
    Given path createdCollectionId
    When method GET
    Then status 200
    And match response.recipes == '#array'
    # If recipes exist, verify their structure
    * if (response.recipes.length > 0) karate.match("response.recipes[0].recipeId", '#number')
    * if (response.recipes.length > 0) karate.match("response.recipes[0].recipeTitle", '#string')
    * if (response.recipes.length > 0) karate.match("response.recipes[0].displayOrder", '#number')
    * if (response.recipes.length > 0) karate.match("response.recipes[0].addedBy", '#uuid')
    * if (response.recipes.length > 0) karate.match("response.recipes[0].addedAt", '#string')

  Scenario: Get collection by ID with non-existent ID - returns 404
    # Try to get a collection that definitely doesn't exist
    Given path 999999999
    When method GET
    Then status 404
    And match response.error == 'Resource not found'
    And match response.message == 'Collection not found or access denied'
    And match response.timestamp == '#string'
    And match response.path == '#string'

  Scenario: Get collection by ID with invalid ID format - returns 400
    # Try to get a collection with invalid ID (string instead of number)
    Given path 'invalid-id'
    When method GET
    Then status 400

  Scenario: Get collection by ID verifies timestamp formats - success
    Given path createdCollectionId
    When method GET
    Then status 200
    # Verify timestamps are in ISO-8601 format
    * def timestampPattern = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/
    * match response.createdAt == '#regex ' + timestampPattern
    * match response.updatedAt == '#regex ' + timestampPattern
    # Verify createdAt and updatedAt are not null
    * assert response.createdAt != null
    * assert response.updatedAt != null

  Scenario: Get collection by ID verifies counts are non-negative - success
    Given path createdCollectionId
    When method GET
    Then status 200
    And match response.recipeCount >= 0
    And match response.collaboratorCount >= 0

  Scenario: Get collection by ID is idempotent - success
    # Get the collection multiple times and verify we get the same result
    Given path createdCollectionId
    When method GET
    Then status 200
    * def firstResponse = response

    Given path createdCollectionId
    When method GET
    Then status 200
    * def secondResponse = response

    # Verify both responses are identical
    * match firstResponse.collectionId == secondResponse.collectionId
    * match firstResponse.name == secondResponse.name
    * match firstResponse.visibility == secondResponse.visibility
    * match firstResponse.collaborationMode == secondResponse.collaborationMode
    * match firstResponse.recipeCount == secondResponse.recipeCount
    * match firstResponse.collaboratorCount == secondResponse.collaboratorCount

  Scenario: Get collection by ID returns correct Content-Type header - success
    Given path createdCollectionId
    When method GET
    Then status 200
    And match header Content-Type contains 'application/json'
