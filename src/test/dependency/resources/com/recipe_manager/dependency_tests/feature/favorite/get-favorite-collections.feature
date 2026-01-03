Feature: Get Favorite Collections Endpoint

  Background:
    * url baseUrl + '/recipe-management/favorites/collections'

  Scenario: Get own favorite collections with no favorites - returns empty list
    Given path ''
    When method GET
    Then status 200
    And match response.content == '#array'
    And match response.totalElements == '#number'
    And match response.number == '#number'
    And match response.size == '#number'

  Scenario: Get favorite collections with pagination parameters - success
    Given path ''
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And match response.number == 0
    And match response.size == 10

  Scenario: Get favorite collections validates page structure - success
    # Create and favorite a collection for testing
    * def createCollectionRequest =
      """
      {
        "name": "Test Collection for Get Favorites",
        "description": "A collection created for testing get favorites endpoint",
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

    # Favorite the collection
    * url baseUrl + '/recipe-management/favorites/collections'
    Given path createdCollectionId
    When method POST
    Then status 201

    # Get favorites
    Given path ''
    And param page = 0
    And param size = 20
    When method GET
    Then status 200
    And match response.content == '#array'
    And match response.totalElements >= 1
    And match response.number == '#number'
    And match response.size == '#number'
    And match response.first == '#boolean'
    And match response.last == '#boolean'

  Scenario: Get favorite collections contains correct collection data - success
    # Create and favorite a collection
    * def createCollectionRequest =
      """
      {
        "name": "Verifiable Collection",
        "description": "Collection to verify in favorites list",
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
    * def createdCollectionName = response.name

    # Favorite the collection
    * url baseUrl + '/recipe-management/favorites/collections'
    Given path createdCollectionId
    When method POST
    Then status 201

    # Get favorites and verify the collection is present
    Given path ''
    When method GET
    Then status 200
    And match response.content[*].collectionId contains createdCollectionId
    And match response.content[*].name contains createdCollectionName

  Scenario: Get favorite collections with default pagination - success
    Given path ''
    When method GET
    Then status 200
    And match response.size == 20  # Default page size

  Scenario: Get favorite collections with custom page size - success
    Given path ''
    And param page = 0
    And param size = 5
    When method GET
    Then status 200
    And match response.size == 5

  Scenario: Get favorite collections returns correct Content-Type header
    Given path ''
    When method GET
    Then status 200
    And match header Content-Type contains 'application/json'

  Scenario: Get favorite collections is idempotent - success
    Given path ''
    When method GET
    Then status 200
    * def firstResponse = response

    Given path ''
    When method GET
    Then status 200
    * def secondResponse = response

    # Total elements should be the same
    * match firstResponse.totalElements == secondResponse.totalElements

  Scenario: Get favorite collections page navigation - success
    Given path ''
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And match response.number == 0
    And match response.first == true

  Scenario: Get favorite collections validates content structure - success
    # Create and favorite multiple collections
    * def createCollectionRequest1 =
      """
      {
        "name": "Structure Test Collection 1",
        "description": "Testing collection structure",
        "visibility": "PUBLIC",
        "collaborationMode": "OWNER_ONLY"
      }
      """
    * url baseUrl + '/recipe-management/collections'
    Given path ''
    And request createCollectionRequest1
    When method POST
    Then status 201
    * def collectionId1 = response.collectionId

    # Favorite the collection
    * url baseUrl + '/recipe-management/favorites/collections'
    Given path collectionId1
    When method POST
    Then status 201

    # Get favorites and verify structure
    Given path ''
    When method GET
    Then status 200
    And match each response.content contains { collectionId: '#number' }
    And match each response.content contains { name: '#string' }
