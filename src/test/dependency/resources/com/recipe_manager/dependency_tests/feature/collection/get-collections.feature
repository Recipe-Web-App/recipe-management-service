Feature: Get Collections Endpoint

  Background:
    * url baseUrl + '/recipe-management/collections'

  Scenario: Get accessible collections with default pagination - success
    # Get all accessible collections
    Given path ''
    When method GET
    Then status 200
    And match response.content == '#array'
    And match response.pageable == '#present'
    And match response.totalElements == '#number'
    And match response.totalPages == '#number'
    And match response.size == 20
    And match response.number == 0
    And match response.first == true
    And match response.empty == '#boolean'
    # Verify collection structure if collections exist
    * if (response.content.length > 0) karate.match("response.content[0].collectionId", '#number')
    * if (response.content.length > 0) karate.match("response.content[0].userId", '#uuid')
    * if (response.content.length > 0) karate.match("response.content[0].name", '#string')
    * if (response.content.length > 0) karate.match("response.content[0].visibility", '#string')
    * if (response.content.length > 0) karate.match("response.content[0].collaborationMode", '#string')
    * if (response.content.length > 0) karate.match("response.content[0].recipeCount", '#number')
    * if (response.content.length > 0) karate.match("response.content[0].collaboratorCount", '#number')
    * if (response.content.length > 0) karate.match("response.content[0].createdAt", '#string')
    * if (response.content.length > 0) karate.match("response.content[0].updatedAt", '#string')

  Scenario: Get accessible collections with custom pagination - success
    Given path ''
    And param page = 0
    And param size = 5
    When method GET
    Then status 200
    And match response.content == '#array'
    And match response.size == 5
    And match response.number == 0
    And match response.totalElements == '#number'
    And match response.totalPages == '#number'
    And match response.first == true

  Scenario: Get accessible collections with second page - success
    Given path ''
    And param page = 1
    And param size = 10
    When method GET
    Then status 200
    And match response.content == '#array'
    And match response.size == 10
    And match response.number == 1
    And match response.first == false
    And match response.totalElements == '#number'
    And match response.totalPages == '#number'

  Scenario: Get accessible collections with sorting - success
    Given path ''
    And param sort = 'name,asc'
    When method GET
    Then status 200
    And match response.content == '#array'
    And match response.totalElements == '#number'
    # If there are multiple collections, verify they are sorted
    * def isSorted =
      """
      function(collections) {
        if (collections.length <= 1) return true;
        for (var i = 0; i < collections.length - 1; i++) {
          if (collections[i].name > collections[i + 1].name) {
            return false;
          }
        }
        return true;
      }
      """
    * if (response.content.length > 1) assert isSorted(response.content)

  Scenario: Get accessible collections with large page size - success
    Given path ''
    And param page = 0
    And param size = 100
    When method GET
    Then status 200
    And match response.content == '#array'
    And match response.size == 100
    And match response.number == 0
    And match response.content.length <= 100

  Scenario: Verify collection visibility field values - success
    Given path ''
    When method GET
    Then status 200
    And match response.content == '#array'
    # Verify visibility is one of the valid enum values
    * def validVisibilities = ['PUBLIC', 'PRIVATE', 'FRIENDS_ONLY']
    * def hasValidVisibility =
      """
      function(collections) {
        for (var i = 0; i < collections.length; i++) {
          if (!validVisibilities.includes(collections[i].visibility)) {
            return false;
          }
        }
        return true;
      }
      """
    * if (response.content.length > 0) assert hasValidVisibility(response.content)

  Scenario: Verify collection collaboration mode field values - success
    Given path ''
    When method GET
    Then status 200
    And match response.content == '#array'
    # Verify collaborationMode is one of the valid enum values
    * def validModes = ['OWNER_ONLY', 'SPECIFIC_USERS', 'ALL_USERS']
    * def hasValidMode =
      """
      function(collections) {
        for (var i = 0; i < collections.length; i++) {
          if (!validModes.includes(collections[i].collaborationMode)) {
            return false;
          }
        }
        return true;
      }
      """
    * if (response.content.length > 0) assert hasValidMode(response.content)

  Scenario: Get accessible collections with invalid page number - should handle gracefully
    Given path ''
    And param page = -1
    And param size = 20
    When method GET
    Then status 200
    And match response.content == '#array'
    # Spring Data handles negative page numbers by defaulting to page 0
    And match response.number >= 0

  Scenario: Verify pagination metadata consistency - success
    Given path ''
    And param page = 0
    And param size = 10
    When method GET
    Then status 200
    And match response.content == '#array'
    # Verify pagination metadata is consistent
    * def contentSize = response.content.length
    * assert contentSize <= response.size
    * assert response.number >= 0
    * assert response.totalPages >= 0
    * assert response.totalElements >= 0
    * if (response.totalElements > 0) assert response.totalPages >= 1
    * if (contentSize > 0) assert response.empty == false
    * if (contentSize == 0) assert response.empty == true
