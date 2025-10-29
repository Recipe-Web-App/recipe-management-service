Feature: Update Collection Endpoint

  Background:
    * url baseUrl + '/recipe-management/collections'
    # Create a test collection to work with
    * def createCollectionRequest =
      """
      {
        "name": "Original Collection Name",
        "description": "Original description for testing updates",
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
    * def originalCreatedAt = response.createdAt
    * def originalUserId = response.userId

  Scenario: Update collection with all fields - success
    * def updateRequest =
      """
      {
        "name": "Updated Collection Name",
        "description": "Updated description text",
        "visibility": "PRIVATE",
        "collaborationMode": "ALL_USERS"
      }
      """
    Given path createdCollectionId
    And request updateRequest
    When method PUT
    Then status 200
    And match response.collectionId == createdCollectionId
    And match response.userId == originalUserId
    And match response.name == 'Updated Collection Name'
    And match response.description == 'Updated description text'
    And match response.visibility == 'PRIVATE'
    And match response.collaborationMode == 'ALL_USERS'
    And match response.createdAt == originalCreatedAt
    # updatedAt should be present (may or may not be different from createdAt depending on timing)
    And match response.updatedAt == '#string'

  Scenario: Update collection with name only (partial update) - success
    * def updateRequest =
      """
      {
        "name": "New Name Only"
      }
      """
    Given path createdCollectionId
    And request updateRequest
    When method PUT
    Then status 200
    And match response.collectionId == createdCollectionId
    And match response.name == 'New Name Only'
    # Verify other fields remain unchanged from original
    And match response.description == 'Original description for testing updates'
    And match response.visibility == 'PUBLIC'
    And match response.collaborationMode == 'OWNER_ONLY'
    And match response.createdAt == originalCreatedAt

  Scenario: Update collection with description only (partial update) - success
    * def updateRequest =
      """
      {
        "description": "Only description changed"
      }
      """
    Given path createdCollectionId
    And request updateRequest
    When method PUT
    Then status 200
    And match response.collectionId == createdCollectionId
    And match response.description == 'Only description changed'
    # Verify other fields remain unchanged
    And match response.name == 'Original Collection Name'
    And match response.visibility == 'PUBLIC'
    And match response.collaborationMode == 'OWNER_ONLY'

  Scenario: Update collection with visibility only (partial update) - success
    * def updateRequest =
      """
      {
        "visibility": "FRIENDS_ONLY"
      }
      """
    Given path createdCollectionId
    And request updateRequest
    When method PUT
    Then status 200
    And match response.visibility == 'FRIENDS_ONLY'
    # Verify other fields remain unchanged
    And match response.name == 'Original Collection Name'
    And match response.description == 'Original description for testing updates'
    And match response.collaborationMode == 'OWNER_ONLY'

  Scenario: Update collection with collaboration mode only (partial update) - success
    * def updateRequest =
      """
      {
        "collaborationMode": "SPECIFIC_USERS"
      }
      """
    Given path createdCollectionId
    And request updateRequest
    When method PUT
    Then status 200
    And match response.collaborationMode == 'SPECIFIC_USERS'
    # Verify other fields remain unchanged
    And match response.name == 'Original Collection Name'
    And match response.description == 'Original description for testing updates'
    And match response.visibility == 'PUBLIC'

  Scenario: Update collection with empty description to clear it - success
    * def updateRequest =
      """
      {
        "description": ""
      }
      """
    Given path createdCollectionId
    And request updateRequest
    When method PUT
    Then status 200
    And match response.description == ''

  Scenario: Update collection with non-existent ID - returns 404
    * def updateRequest =
      """
      {
        "name": "Trying to update non-existent collection"
      }
      """
    Given path 999999999
    And request updateRequest
    When method PUT
    Then status 404
    And match response.error == 'Resource not found'
    And match response.message == 'Collection not found'
    And match response.timestamp == '#string'
    And match response.path == '#string'

  Scenario: Update collection with empty name - returns 400
    * def updateRequest =
      """
      {
        "name": ""
      }
      """
    Given path createdCollectionId
    And request updateRequest
    When method PUT
    Then status 400
    And match response.error == 'Validation failed'
    And match response.message == 'One or more fields failed validation'
    And match response.details == '#object'

  Scenario: Update collection with name exceeding max length - returns 400
    * def longName = karate.repeat('a', 256)
    * def updateRequest =
      """
      {
        "name": "#(longName)"
      }
      """
    Given path createdCollectionId
    And request updateRequest
    When method PUT
    Then status 400
    And match response.error == 'Validation failed'
    And match response.message == 'One or more fields failed validation'
    And match response.details.name == 'Collection name must be between 1 and 255 characters'

  Scenario: Update collection with description exceeding max length - returns 400
    * def longDescription = karate.repeat('a', 2001)
    * def updateRequest =
      """
      {
        "description": "#(longDescription)"
      }
      """
    Given path createdCollectionId
    And request updateRequest
    When method PUT
    Then status 400
    And match response.error == 'Validation failed'
    And match response.message == 'One or more fields failed validation'
    And match response.details.description == 'Collection description must not exceed 2000 characters'

  Scenario: Update collection with invalid visibility value - returns 400
    * def updateRequest =
      """
      {
        "visibility": "INVALID_VALUE"
      }
      """
    Given path createdCollectionId
    And request updateRequest
    When method PUT
    Then status 400

  Scenario: Update collection with invalid collaboration mode - returns 400
    * def updateRequest =
      """
      {
        "collaborationMode": "INVALID_MODE"
      }
      """
    Given path createdCollectionId
    And request updateRequest
    When method PUT
    Then status 400

  Scenario: Update collection verifies createdAt timestamp does not change - success
    * def updateRequest =
      """
      {
        "name": "Name changed to verify timestamps"
      }
      """
    Given path createdCollectionId
    And request updateRequest
    When method PUT
    Then status 200
    # CreatedAt should remain exactly the same
    And match response.createdAt == originalCreatedAt
    # UpdatedAt should be present
    And match response.updatedAt == '#string'

  Scenario: Update collection verifies all fields are present in response - success
    * def updateRequest =
      """
      {
        "name": "Updated Name"
      }
      """
    Given path createdCollectionId
    And request updateRequest
    When method PUT
    Then status 200
    And match response.collectionId == '#present'
    And match response.userId == '#present'
    And match response.name == '#present'
    And match response.description == '#present'
    And match response.visibility == '#present'
    And match response.collaborationMode == '#present'
    And match response.recipeCount == '#present'
    And match response.collaboratorCount == '#present'
    And match response.createdAt == '#present'
    And match response.updatedAt == '#present'

  Scenario: Update collection verifies enum values after update - success
    * def updateRequest =
      """
      {
        "visibility": "PRIVATE",
        "collaborationMode": "ALL_USERS"
      }
      """
    Given path createdCollectionId
    And request updateRequest
    When method PUT
    Then status 200
    # Verify visibility is one of the valid enum values
    * def validVisibilities = ['PUBLIC', 'PRIVATE', 'FRIENDS_ONLY']
    * assert validVisibilities.includes(response.visibility)
    # Verify collaborationMode is one of the valid enum values
    * def validModes = ['OWNER_ONLY', 'SPECIFIC_USERS', 'ALL_USERS']
    * assert validModes.includes(response.collaborationMode)

  Scenario: Update collection returns correct Content-Type header - success
    * def updateRequest =
      """
      {
        "name": "Testing Content-Type"
      }
      """
    Given path createdCollectionId
    And request updateRequest
    When method PUT
    Then status 200
    And match header Content-Type contains 'application/json'

  Scenario: Update collection is idempotent - success
    * def updateRequest =
      """
      {
        "name": "Idempotent Update Test",
        "visibility": "PRIVATE"
      }
      """
    # First update
    Given path createdCollectionId
    And request updateRequest
    When method PUT
    Then status 200
    * def firstResponse = response

    # Second update with same data
    Given path createdCollectionId
    And request updateRequest
    When method PUT
    Then status 200
    * def secondResponse = response

    # Verify both responses have the same field values
    * match firstResponse.collectionId == secondResponse.collectionId
    * match firstResponse.userId == secondResponse.userId
    * match firstResponse.name == secondResponse.name
    * match firstResponse.description == secondResponse.description
    * match firstResponse.visibility == secondResponse.visibility
    * match firstResponse.collaborationMode == secondResponse.collaborationMode

  Scenario: Update collection verifies counts remain unchanged - success
    * def updateRequest =
      """
      {
        "name": "Updated to check counts"
      }
      """
    Given path createdCollectionId
    And request updateRequest
    When method PUT
    Then status 200
    # Verify counts are still present and non-negative
    And match response.recipeCount >= 0
    And match response.collaboratorCount >= 0
    # For a newly created collection, counts should be 0
    And match response.recipeCount == 0
    And match response.collaboratorCount == 0

  Scenario: Update collection with invalid ID format - returns 400
    * def updateRequest =
      """
      {
        "name": "Test"
      }
      """
    Given path 'invalid-id'
    And request updateRequest
    When method PUT
    Then status 400
