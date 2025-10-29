Feature: Get Collaborators Endpoint

  Background:
    * url baseUrl + '/recipe-management/collections'
    # Create a test collection with SPECIFIC_USERS collaboration mode
    * def createCollectionRequest =
      """
      {
        "name": "Test Collection for Collaborators",
        "description": "A collection created for testing the get collaborators endpoint",
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

  Scenario: Get collaborators for SPECIFIC_USERS collection - returns empty list
    # Get collaborators for the collection we just created (should be empty initially)
    Given path createdCollectionId, 'collaborators'
    When method GET
    Then status 200
    And match response == '#array'
    # Should be empty as no collaborators have been added yet
    And match response == []

  Scenario: Get collaborators verifies response structure when empty - success
    Given path createdCollectionId, 'collaborators'
    When method GET
    Then status 200
    And match response == '#array'
    And match response.length == 0

  Scenario: Get collaborators with non-existent collection ID - returns 404
    # Try to get collaborators for a collection that doesn't exist
    Given path 999999999, 'collaborators'
    When method GET
    Then status 404
    And match response.error == 'Resource not found'
    And match response.message == 'Collection not found'
    And match response.timestamp == '#string'
    And match response.path == '#string'

  Scenario: Get collaborators with invalid collection ID format - returns 400
    # Try to get collaborators with invalid ID (string instead of number)
    Given path 'invalid-id', 'collaborators'
    When method GET
    Then status 400

  Scenario: Get collaborators for non-SPECIFIC_USERS collection - returns 403
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

    # Try to get collaborators for OWNER_ONLY collection
    Given path ownerOnlyCollectionId, 'collaborators'
    When method GET
    Then status 403
    And match response.error == 'Access denied'
    And match response.message == 'You don\'t have permission to access this resource'
    And match response.timestamp == '#string'
    And match response.path == '#string'

  Scenario: Get collaborators for ALL_USERS collaboration mode - returns 403
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

    # Try to get collaborators for ALL_USERS collection
    Given path allUsersCollectionId, 'collaborators'
    When method GET
    Then status 403
    And match response.error == 'Access denied'
    And match response.message == 'You don\'t have permission to access this resource'

  Scenario: Get collaborators returns correct Content-Type header - success
    Given path createdCollectionId, 'collaborators'
    When method GET
    Then status 200
    And match header Content-Type contains 'application/json'

  Scenario: Get collaborators is idempotent - success
    # Get collaborators multiple times and verify we get the same result
    Given path createdCollectionId, 'collaborators'
    When method GET
    Then status 200
    * def firstResponse = response

    Given path createdCollectionId, 'collaborators'
    When method GET
    Then status 200
    * def secondResponse = response

    # Verify both responses are identical
    * match firstResponse == secondResponse

  Scenario: Get collaborators verifies array structure - success
    Given path createdCollectionId, 'collaborators'
    When method GET
    Then status 200
    And match response == '#array'
    # Even if empty, it should be a valid array
    * assert Array.isArray(response)

  Scenario: Get collaborators for SPECIFIC_USERS with PUBLIC visibility - success
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

    # Get collaborators should work for PUBLIC SPECIFIC_USERS collections
    Given path publicCollectionId, 'collaborators'
    When method GET
    Then status 200
    And match response == '#array'
