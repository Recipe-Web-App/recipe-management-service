Feature: Unfavorite Collection Endpoint

  Background:
    * url baseUrl + '/recipe-management/favorites/collections'
    # Create a test collection to work with
    * def createCollectionRequest =
      """
      {
        "name": "Test Collection for Unfavorite",
        "description": "A collection created for testing the unfavorite endpoint",
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
    # Favorite the collection first
    Given path createdCollectionId
    When method POST
    Then status 201

  Scenario: Unfavorite collection successfully - returns 204 No Content
    Given path createdCollectionId
    When method DELETE
    Then status 204
    And match response == ''

  Scenario: Unfavorite collection removes the favorite - success
    # First, verify the favorite exists
    Given path createdCollectionId + '/is-favorited'
    When method GET
    Then status 200
    And match response == true

    # Unfavorite the collection
    Given path createdCollectionId
    When method DELETE
    Then status 204

    # Verify the favorite was removed
    Given path createdCollectionId + '/is-favorited'
    When method GET
    Then status 200
    And match response == false

  Scenario: Unfavorite non-favorited collection - returns 404 Not Found
    # First, remove the favorite
    Given path createdCollectionId
    When method DELETE
    Then status 204

    # Try to unfavorite again
    Given path createdCollectionId
    When method DELETE
    Then status 404
    And match response.error == 'Resource not found'
    And match response.message == 'Favorite not found for this user and collection'
    And match response.timestamp == '#string'

  Scenario: Unfavorite non-existent collection - returns 404 Not Found
    Given path 999999999
    When method DELETE
    Then status 404
    And match response.error == 'Resource not found'
    And match response.message contains 'Favorite not found'

  Scenario: Unfavorite collection with invalid ID format - returns 400
    Given path 'invalid-id'
    When method DELETE
    Then status 400

  Scenario: Unfavorite collection returns empty response body
    Given path createdCollectionId
    When method DELETE
    Then status 204
    And match response == ''

  Scenario: Unfavorite collection is idempotent on state - success
    # Unfavorite the collection
    Given path createdCollectionId
    When method DELETE
    Then status 204

    # Verify state remains unfavorited
    Given path createdCollectionId + '/is-favorited'
    When method GET
    Then status 200
    And match response == false
