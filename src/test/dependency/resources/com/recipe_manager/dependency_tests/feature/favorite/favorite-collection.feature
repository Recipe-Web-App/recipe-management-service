Feature: Favorite Collection Endpoint

  Background:
    * url baseUrl + '/recipe-management/favorites/collections'
    # Create a test collection to work with
    * def createCollectionRequest =
      """
      {
        "name": "Test Collection for Favorite",
        "description": "A collection created for testing the favorite endpoint",
        "visibility": "PUBLIC",
        "collaborationMode": "OWNER_ONLY"
      }
      """
    * url baseUrl + '/recipe-management/collections'
    Given path ''
    And request createCollectionRequest
    When method POST
    Then status 201
    * def createdCollectionId = response.collectionId
    * url baseUrl + '/recipe-management/favorites/collections'

  Scenario: Favorite collection successfully - returns 201 Created
    Given path createdCollectionId
    When method POST
    Then status 201
    And match response.userId == '#uuid'
    And match response.collectionId == createdCollectionId
    And match response.favoritedAt == '#string'

  Scenario: Favorite collection validates response fields - success
    Given path createdCollectionId
    When method POST
    Then status 201
    And match response.userId == '#present'
    And match response.collectionId == '#present'
    And match response.favoritedAt == '#present'
    # Verify timestamp format
    * def timestampPattern = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/
    * match response.favoritedAt == '#regex ' + timestampPattern

  Scenario: Favorite already favorited collection - returns 400 Bad Request
    # First, favorite the collection
    Given path createdCollectionId
    When method POST
    Then status 201

    # Try to favorite again
    Given path createdCollectionId
    When method POST
    Then status 400
    And match response.error == 'Business error'
    And match response.message == 'User has already favorited this collection'
    And match response.timestamp == '#string'

  Scenario: Favorite non-existent collection - returns 404 Not Found
    Given path 999999999
    When method POST
    Then status 404
    And match response.error == 'Resource not found'
    And match response.message contains 'Collection not found'
    And match response.timestamp == '#string'

  Scenario: Favorite collection with invalid ID format - returns 400
    Given path 'invalid-id'
    When method POST
    Then status 400

  Scenario: Favorite collection returns correct Content-Type header
    Given path createdCollectionId
    When method POST
    Then status 201
    And match header Content-Type contains 'application/json'

  Scenario: Favorite collection is idempotent check - returns same state
    # Favorite the collection
    Given path createdCollectionId
    When method POST
    Then status 201
    * def firstResponse = response

    # Verify the favorite exists by checking is-favorited
    * url baseUrl + '/recipe-management/favorites/collections'
    Given path createdCollectionId + '/is-favorited'
    When method GET
    Then status 200
    And match response == true
