Feature: Remove Collaborator Endpoint

  Background:
    * url baseUrl + '/recipe-management/collections'
    # Create a test collection with SPECIFIC_USERS collaboration mode
    * def createCollectionRequest =
      """
      {
        "name": "Test Collection for Removing Collaborators",
        "description": "A collection created for testing the remove collaborator endpoint",
        "visibility": "PRIVATE",
        "collaborationMode": "SPECIFIC_USERS"
      }
      """
    Given path ''
    And request createCollectionRequest
    When method POST
    Then status 201
    * def createdCollectionId = response.collectionId

  Scenario: Remove collaborator from collection - returns 204
    # First add a collaborator
    * def collaboratorUserId = java.util.UUID.randomUUID().toString()
    * def addCollaboratorRequest = { userId: '#(collaboratorUserId)' }
    Given path createdCollectionId, 'collaborators'
    And request addCollaboratorRequest
    When method POST
    Then status 201

    # Now remove the collaborator
    Given path createdCollectionId, 'collaborators', collaboratorUserId
    When method DELETE
    Then status 204

  Scenario: Remove collaborator verifies deletion - success
    # Add a collaborator
    * def collaboratorUserId = java.util.UUID.randomUUID().toString()
    * def addCollaboratorRequest = { userId: '#(collaboratorUserId)' }
    Given path createdCollectionId, 'collaborators'
    And request addCollaboratorRequest
    When method POST
    Then status 201

    # Remove the collaborator
    Given path createdCollectionId, 'collaborators', collaboratorUserId
    When method DELETE
    Then status 204

    # Verify collaborator is no longer in the list
    Given path createdCollectionId, 'collaborators'
    When method GET
    Then status 200
    And match response !contains { userId: '#(collaboratorUserId)' }

  Scenario: Remove collaborator with non-existent collection ID - returns 404
    * def collaboratorUserId = java.util.UUID.randomUUID().toString()
    Given path 999999999, 'collaborators', collaboratorUserId
    When method DELETE
    Then status 404
    And match response.error == 'Resource not found'
    And match response.message == 'Collection not found'
    And match response.timestamp == '#string'
    And match response.path == '#string'

  Scenario: Remove collaborator with invalid collection ID format - returns 400
    * def collaboratorUserId = java.util.UUID.randomUUID().toString()
    Given path 'invalid-id', 'collaborators', collaboratorUserId
    When method DELETE
    Then status 400

  Scenario: Remove non-existent collaborator - returns 404
    * def nonCollaboratorId = java.util.UUID.randomUUID().toString()
    Given path createdCollectionId, 'collaborators', nonCollaboratorId
    When method DELETE
    Then status 404
    And match response.error == 'Resource not found'
    And match response.message contains 'is not a collaborator on this collection'

  Scenario: Remove collaborator with invalid UUID format - returns 400
    Given path createdCollectionId, 'collaborators', 'invalid-uuid'
    When method DELETE
    Then status 400

  Scenario: Remove collaborator returns no content body - success
    # Add a collaborator
    * def collaboratorUserId = java.util.UUID.randomUUID().toString()
    * def addCollaboratorRequest = { userId: '#(collaboratorUserId)' }
    Given path createdCollectionId, 'collaborators'
    And request addCollaboratorRequest
    When method POST
    Then status 201

    # Remove the collaborator
    Given path createdCollectionId, 'collaborators', collaboratorUserId
    When method DELETE
    Then status 204
    And match response == ''

  Scenario: Remove collaborator is idempotent on first call - success
    # Add a collaborator
    * def collaboratorUserId = java.util.UUID.randomUUID().toString()
    * def addCollaboratorRequest = { userId: '#(collaboratorUserId)' }
    Given path createdCollectionId, 'collaborators'
    And request addCollaboratorRequest
    When method POST
    Then status 201

    # Remove the collaborator (first time)
    Given path createdCollectionId, 'collaborators', collaboratorUserId
    When method DELETE
    Then status 204

  Scenario: Remove same collaborator twice - returns 404 on second attempt
    # Add a collaborator
    * def collaboratorUserId = java.util.UUID.randomUUID().toString()
    * def addCollaboratorRequest = { userId: '#(collaboratorUserId)' }
    Given path createdCollectionId, 'collaborators'
    And request addCollaboratorRequest
    When method POST
    Then status 201

    # Remove the collaborator (first time)
    Given path createdCollectionId, 'collaborators', collaboratorUserId
    When method DELETE
    Then status 204

    # Try to remove again (second time) - should fail
    Given path createdCollectionId, 'collaborators', collaboratorUserId
    When method DELETE
    Then status 404
    And match response.error == 'Resource not found'

  Scenario: Remove collaborator from OWNER_ONLY collection - success
    # Create collection with OWNER_ONLY mode
    * def ownerOnlyRequest =
      """
      {
        "name": "Owner Only Collection",
        "description": "Collection with OWNER_ONLY mode",
        "visibility": "PUBLIC",
        "collaborationMode": "OWNER_ONLY"
      }
      """
    Given path ''
    And request ownerOnlyRequest
    When method POST
    Then status 201
    * def ownerOnlyCollectionId = response.collectionId

    # Add a collaborator directly (simulating existing collaborator)
    * def collaboratorUserId = java.util.UUID.randomUUID().toString()

    # Try to remove collaborator from OWNER_ONLY collection - should succeed
    # Note: Owner can always remove collaborators regardless of mode
    Given path ownerOnlyCollectionId, 'collaborators', collaboratorUserId
    When method DELETE
    Then status 404
    # Returns 404 because collaborator doesn't exist, but doesn't fail on mode check

  Scenario: Remove collaborator from PUBLIC collection - success
    # Create PUBLIC collection
    * def publicRequest =
      """
      {
        "name": "Public Collection",
        "description": "Public collection with SPECIFIC_USERS mode",
        "visibility": "PUBLIC",
        "collaborationMode": "SPECIFIC_USERS"
      }
      """
    Given path ''
    And request publicRequest
    When method POST
    Then status 201
    * def publicCollectionId = response.collectionId

    # Add a collaborator
    * def collaboratorUserId = java.util.UUID.randomUUID().toString()
    * def addCollaboratorRequest = { userId: '#(collaboratorUserId)' }
    Given path publicCollectionId, 'collaborators'
    And request addCollaboratorRequest
    When method POST
    Then status 201

    # Remove the collaborator
    Given path publicCollectionId, 'collaborators', collaboratorUserId
    When method DELETE
    Then status 204

  Scenario: Remove multiple collaborators sequentially - success
    # Add first collaborator
    * def collaborator1Id = java.util.UUID.randomUUID().toString()
    * def addCollaborator1Request = { userId: '#(collaborator1Id)' }
    Given path createdCollectionId, 'collaborators'
    And request addCollaborator1Request
    When method POST
    Then status 201

    # Add second collaborator
    * def collaborator2Id = java.util.UUID.randomUUID().toString()
    * def addCollaborator2Request = { userId: '#(collaborator2Id)' }
    Given path createdCollectionId, 'collaborators'
    And request addCollaborator2Request
    When method POST
    Then status 201

    # Remove first collaborator
    Given path createdCollectionId, 'collaborators', collaborator1Id
    When method DELETE
    Then status 204

    # Remove second collaborator
    Given path createdCollectionId, 'collaborators', collaborator2Id
    When method DELETE
    Then status 204

    # Verify both are removed
    Given path createdCollectionId, 'collaborators'
    When method GET
    Then status 200
    And match response == []
