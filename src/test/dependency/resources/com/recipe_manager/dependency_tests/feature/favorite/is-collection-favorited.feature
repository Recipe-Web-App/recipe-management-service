Feature: Is Collection Favorited Endpoint

  Background:
    * url baseUrl + '/recipe-management/favorites/collections'
    # Create a test collection to work with
    * def createCollectionRequest =
      """
      {
        "name": "Test Collection for Is Favorited Check",
        "description": "A collection created for testing the is-favorited endpoint",
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

  Scenario: Check if unfavorited collection is favorited - returns false
    Given path createdCollectionId + '/is-favorited'
    When method GET
    Then status 200
    And match response == false

  Scenario: Check if favorited collection is favorited - returns true
    # First, favorite the collection
    Given path createdCollectionId
    When method POST
    Then status 201

    # Check if favorited
    Given path createdCollectionId + '/is-favorited'
    When method GET
    Then status 200
    And match response == true

  Scenario: Is-favorited returns boolean type - success
    Given path createdCollectionId + '/is-favorited'
    When method GET
    Then status 200
    # Response should be a boolean (true or false)
    * assert response === true || response === false

  Scenario: Is-favorited for non-existent collection - returns false
    # For a non-existent collection, should return false (not favorited)
    Given path '999999999/is-favorited'
    When method GET
    Then status 200
    And match response == false

  Scenario: Is-favorited with invalid ID format - returns 400
    Given path 'invalid-id/is-favorited'
    When method GET
    Then status 400

  Scenario: Is-favorited returns correct Content-Type header
    Given path createdCollectionId + '/is-favorited'
    When method GET
    Then status 200
    And match header Content-Type contains 'application/json'

  Scenario: Is-favorited is idempotent - returns same result
    # First check
    Given path createdCollectionId + '/is-favorited'
    When method GET
    Then status 200
    * def firstResult = response

    # Second check
    Given path createdCollectionId + '/is-favorited'
    When method GET
    Then status 200
    * def secondResult = response

    # Results should be identical
    * match firstResult == secondResult

  Scenario: Is-favorited reflects state changes - success
    # Initially should be false
    Given path createdCollectionId + '/is-favorited'
    When method GET
    Then status 200
    And match response == false

    # Favorite the collection
    Given path createdCollectionId
    When method POST
    Then status 201

    # Now should be true
    Given path createdCollectionId + '/is-favorited'
    When method GET
    Then status 200
    And match response == true

    # Unfavorite the collection
    Given path createdCollectionId
    When method DELETE
    Then status 204

    # Now should be false again
    Given path createdCollectionId + '/is-favorited'
    When method GET
    Then status 200
    And match response == false
