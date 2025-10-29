Feature: Add Collaborator Endpoint

  Background:
    * url baseUrl + '/recipe-management/collections'
    # Create a test collection with SPECIFIC_USERS collaboration mode
    * def createCollectionRequest =
      """
      {
        "name": "Test Collection for Adding Collaborators",
        "description": "A collection created for testing the add collaborator endpoint",
        "visibility": "PRIVATE",
        "collaborationMode": "SPECIFIC_USERS"
      }
      """
    Given path ''
    And request createCollectionRequest
    When method POST
    Then status 201
    * def createdCollectionId = response.collectionId
    * def createdCollection = response

  Scenario: Add collaborator to SPECIFIC_USERS collection - returns 201
    # Create a valid user ID for the collaborator (UUID format)
    * def collaboratorUserId = java.util.UUID.randomUUID().toString()
    * def addCollaboratorRequest =
      """
      {
        "userId": "#(collaboratorUserId)"
      }
      """
    Given path createdCollectionId, 'collaborators'
    And request addCollaboratorRequest
    When method POST
    Then status 201
    And match response.collectionId == createdCollectionId
    And match response.userId == collaboratorUserId
    And match response.username == '#string'
    And match response.grantedBy == '#string'
    And match response.grantedByUsername == '#string'
    And match response.grantedAt == '#string'

  Scenario: Add collaborator verifies all response fields - success
    * def collaboratorUserId = java.util.UUID.randomUUID().toString()
    * def addCollaboratorRequest =
      """
      {
        "userId": "#(collaboratorUserId)"
      }
      """
    Given path createdCollectionId, 'collaborators'
    And request addCollaboratorRequest
    When method POST
    Then status 201
    And match response ==
      """
      {
        collectionId: '#number',
        userId: '#string',
        username: '#string',
        grantedBy: '#string',
        grantedByUsername: '#string',
        grantedAt: '#string'
      }
      """

  Scenario: Add collaborator with non-existent collection ID - returns 404
    * def collaboratorUserId = java.util.UUID.randomUUID().toString()
    * def addCollaboratorRequest =
      """
      {
        "userId": "#(collaboratorUserId)"
      }
      """
    Given path 999999999, 'collaborators'
    And request addCollaboratorRequest
    When method POST
    Then status 404
    And match response.error == 'Resource not found'
    And match response.message == 'Collection not found'
    And match response.timestamp == '#string'
    And match response.path == '#string'

  Scenario: Add collaborator with invalid collection ID format - returns 400
    * def collaboratorUserId = java.util.UUID.randomUUID().toString()
    * def addCollaboratorRequest =
      """
      {
        "userId": "#(collaboratorUserId)"
      }
      """
    Given path 'invalid-id', 'collaborators'
    And request addCollaboratorRequest
    When method POST
    Then status 400

  Scenario: Add collaborator to OWNER_ONLY collection - returns 403
    # Create a collection with OWNER_ONLY collaboration mode
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

    # Try to add a collaborator to OWNER_ONLY collection
    * def collaboratorUserId = java.util.UUID.randomUUID().toString()
    * def addCollaboratorRequest =
      """
      {
        "userId": "#(collaboratorUserId)"
      }
      """
    Given path ownerOnlyCollectionId, 'collaborators'
    And request addCollaboratorRequest
    When method POST
    Then status 403
    And match response.error == 'Access denied'
    And match response.message == 'You don\'t have permission to access this resource'
    And match response.timestamp == '#string'
    And match response.path == '#string'

  Scenario: Add collaborator to ALL_USERS collection - returns 403
    # Create a collection with ALL_USERS collaboration mode
    * def allUsersRequest =
      """
      {
        "name": "All Users Collection",
        "description": "Collection with ALL_USERS mode",
        "visibility": "PUBLIC",
        "collaborationMode": "ALL_USERS"
      }
      """
    Given path ''
    And request allUsersRequest
    When method POST
    Then status 201
    * def allUsersCollectionId = response.collectionId

    # Try to add a collaborator to ALL_USERS collection
    * def collaboratorUserId = java.util.UUID.randomUUID().toString()
    * def addCollaboratorRequest =
      """
      {
        "userId": "#(collaboratorUserId)"
      }
      """
    Given path allUsersCollectionId, 'collaborators'
    And request addCollaboratorRequest
    When method POST
    Then status 403
    And match response.error == 'Access denied'
    And match response.message == 'You don\'t have permission to access this resource'

  Scenario: Add same collaborator twice - returns 409
    # Add a collaborator first time
    * def collaboratorUserId = java.util.UUID.randomUUID().toString()
    * def addCollaboratorRequest =
      """
      {
        "userId": "#(collaboratorUserId)"
      }
      """
    Given path createdCollectionId, 'collaborators'
    And request addCollaboratorRequest
    When method POST
    Then status 201

    # Try to add the same collaborator again
    Given path createdCollectionId, 'collaborators'
    And request addCollaboratorRequest
    When method POST
    Then status 409
    And match response.error == 'Resource conflict'
    And match response.message == 'User is already a collaborator on this collection'
    And match response.timestamp == '#string'
    And match response.path == '#string'

  Scenario: Add collaborator with missing userId field - returns 400
    * def invalidRequest =
      """
      {
      }
      """
    Given path createdCollectionId, 'collaborators'
    And request invalidRequest
    When method POST
    Then status 400

  Scenario: Add collaborator with null userId - returns 400
    * def invalidRequest =
      """
      {
        "userId": null
      }
      """
    Given path createdCollectionId, 'collaborators'
    And request invalidRequest
    When method POST
    Then status 400

  Scenario: Add collaborator verifies correct Content-Type header - success
    * def collaboratorUserId = java.util.UUID.randomUUID().toString()
    * def addCollaboratorRequest =
      """
      {
        "userId": "#(collaboratorUserId)"
      }
      """
    Given path createdCollectionId, 'collaborators'
    And request addCollaboratorRequest
    When method POST
    Then status 201
    And match header Content-Type contains 'application/json'

  Scenario: Add collaborator increments collaborator count - success
    # Get initial collaborator list
    Given path createdCollectionId, 'collaborators'
    When method GET
    Then status 200
    * def initialCount = response.length

    # Add a collaborator
    * def collaboratorUserId = java.util.UUID.randomUUID().toString()
    * def addCollaboratorRequest =
      """
      {
        "userId": "#(collaboratorUserId)"
      }
      """
    Given path createdCollectionId, 'collaborators'
    And request addCollaboratorRequest
    When method POST
    Then status 201

    # Get updated collaborator list and verify count increased
    Given path createdCollectionId, 'collaborators'
    When method GET
    Then status 200
    * def newCount = response.length
    * assert newCount == initialCount + 1

  Scenario: Add collaborator with invalid UUID format - returns 400
    * def invalidRequest =
      """
      {
        "userId": "not-a-valid-uuid"
      }
      """
    Given path createdCollectionId, 'collaborators'
    And request invalidRequest
    When method POST
    Then status 400

  Scenario: Add collaborator to PUBLIC SPECIFIC_USERS collection - success
    # Create a PUBLIC collection with SPECIFIC_USERS mode
    * def publicSpecificRequest =
      """
      {
        "name": "Public Specific Users Collection",
        "description": "Public collection with SPECIFIC_USERS mode",
        "visibility": "PUBLIC",
        "collaborationMode": "SPECIFIC_USERS"
      }
      """
    Given path ''
    And request publicSpecificRequest
    When method POST
    Then status 201
    * def publicCollectionId = response.collectionId

    # Add collaborator should work for PUBLIC SPECIFIC_USERS collections
    * def collaboratorUserId = java.util.UUID.randomUUID().toString()
    * def addCollaboratorRequest =
      """
      {
        "userId": "#(collaboratorUserId)"
      }
      """
    Given path publicCollectionId, 'collaborators'
    And request addCollaboratorRequest
    When method POST
    Then status 201
    And match response.collectionId == publicCollectionId
