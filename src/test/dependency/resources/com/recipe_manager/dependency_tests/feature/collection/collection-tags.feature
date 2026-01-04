Feature: Collection Tags Endpoints

  Background:
    * url baseUrl + '/recipe-management/collections'
    # Create a test collection for tag operations
    * def createCollectionRequest =
      """
      {
        "name": "Test Collection for Tags",
        "description": "A collection created for testing tag endpoints",
        "visibility": "PUBLIC",
        "collaborationMode": "OWNER_ONLY"
      }
      """
    Given path ''
    And request createCollectionRequest
    When method POST
    Then status 201
    * def createdCollectionId = response.collectionId

  # ==========================================
  # GET /collections/{collectionId}/tags Tests
  # ==========================================

  Scenario: Get tags for collection with no tags - returns empty array
    Given path createdCollectionId, 'tags'
    When method GET
    Then status 200
    And match response.collectionId == createdCollectionId
    And match response.tags == '#array'
    And match response.tags == []

  Scenario: Get tags for collection - returns tag list
    # First add a tag
    * def addTagRequest = { "name": "breakfast" }
    Given path createdCollectionId, 'tags'
    And request addTagRequest
    When method POST
    Then status 201

    # Get tags and verify
    Given path createdCollectionId, 'tags'
    When method GET
    Then status 200
    And match response.collectionId == createdCollectionId
    And match response.tags == '#array'
    And match response.tags[0].tagId == '#number'
    And match response.tags[0].name == 'breakfast'

  Scenario: Get tags for non-existent collection - returns 404
    Given path 999999999, 'tags'
    When method GET
    Then status 404
    And match response.error == 'Resource not found'
    And match response.message == 'Collection not found'

  Scenario: Get tags with invalid collection ID format - returns 400
    Given path 'invalid-id', 'tags'
    When method GET
    Then status 400

  Scenario: Get tags verifies correct Content-Type header
    Given path createdCollectionId, 'tags'
    When method GET
    Then status 200
    And match header Content-Type contains 'application/json'

  # ============================================
  # POST /collections/{collectionId}/tags Tests
  # ============================================

  Scenario: Add tag to collection - returns 201
    * def addTagRequest = { "name": "quick-meals" }
    Given path createdCollectionId, 'tags'
    And request addTagRequest
    When method POST
    Then status 201
    And match response.collectionId == createdCollectionId
    And match response.tags == '#array'
    And match response.tags[*].name contains 'quick-meals'

  Scenario: Add tag verifies response structure
    * def addTagRequest = { "name": "healthy" }
    Given path createdCollectionId, 'tags'
    And request addTagRequest
    When method POST
    Then status 201
    And match response ==
      """
      {
        collectionId: '#number',
        tags: '#array'
      }
      """

  Scenario: Add tag with special characters - success
    * def addTagRequest = { "name": "gluten-free" }
    Given path createdCollectionId, 'tags'
    And request addTagRequest
    When method POST
    Then status 201
    And match response.tags[*].name contains 'gluten-free'

  Scenario: Add duplicate tag - returns 409
    # Add tag first time
    * def addTagRequest = { "name": "duplicate-tag" }
    Given path createdCollectionId, 'tags'
    And request addTagRequest
    When method POST
    Then status 201

    # Try to add same tag again
    Given path createdCollectionId, 'tags'
    And request addTagRequest
    When method POST
    Then status 409
    And match response.error == 'Resource conflict'

  Scenario: Add tag with case-insensitive duplicate - returns 409
    # Add tag first time
    * def addTagRequest = { "name": "CaseSensitive" }
    Given path createdCollectionId, 'tags'
    And request addTagRequest
    When method POST
    Then status 201

    # Try to add same tag with different case
    * def duplicateRequest = { "name": "casesensitive" }
    Given path createdCollectionId, 'tags'
    And request duplicateRequest
    When method POST
    Then status 409

  Scenario: Add tag to non-existent collection - returns 404
    * def addTagRequest = { "name": "orphan-tag" }
    Given path 999999999, 'tags'
    And request addTagRequest
    When method POST
    Then status 404
    And match response.error == 'Resource not found'
    And match response.message == 'Collection not found'

  Scenario: Add tag with missing name field - returns 400
    * def invalidRequest = {}
    Given path createdCollectionId, 'tags'
    And request invalidRequest
    When method POST
    Then status 400

  Scenario: Add tag with empty name - returns 400
    * def invalidRequest = { "name": "" }
    Given path createdCollectionId, 'tags'
    And request invalidRequest
    When method POST
    Then status 400

  Scenario: Add tag with null name - returns 400
    * def invalidRequest = { "name": null }
    Given path createdCollectionId, 'tags'
    And request invalidRequest
    When method POST
    Then status 400

  Scenario: Add tag with whitespace-only name - returns 400
    * def invalidRequest = { "name": "   " }
    Given path createdCollectionId, 'tags'
    And request invalidRequest
    When method POST
    Then status 400

  Scenario: Add multiple tags sequentially - success
    * def tag1 = { "name": "tag-one" }
    * def tag2 = { "name": "tag-two" }
    * def tag3 = { "name": "tag-three" }

    Given path createdCollectionId, 'tags'
    And request tag1
    When method POST
    Then status 201

    Given path createdCollectionId, 'tags'
    And request tag2
    When method POST
    Then status 201

    Given path createdCollectionId, 'tags'
    And request tag3
    When method POST
    Then status 201
    And match response.tags == '#[3]'

  Scenario: Add tag verifies correct Content-Type header
    * def addTagRequest = { "name": "content-type-test" }
    Given path createdCollectionId, 'tags'
    And request addTagRequest
    When method POST
    Then status 201
    And match header Content-Type contains 'application/json'

  # ==============================================
  # DELETE /collections/{collectionId}/tags Tests
  # ==============================================

  Scenario: Remove tag from collection - returns 200
    # First add a tag
    * def addTagRequest = { "name": "to-remove" }
    Given path createdCollectionId, 'tags'
    And request addTagRequest
    When method POST
    Then status 201
    * def initialTagCount = response.tags.length

    # Remove the tag
    * def removeTagRequest = { "name": "to-remove" }
    Given path createdCollectionId, 'tags'
    And request removeTagRequest
    When method DELETE
    Then status 200
    And match response.collectionId == createdCollectionId
    And match response.tags == '#array'
    And match response.tags[*].name !contains 'to-remove'

  Scenario: Remove non-existent tag - returns 404
    * def removeTagRequest = { "name": "non-existent-tag" }
    Given path createdCollectionId, 'tags'
    And request removeTagRequest
    When method DELETE
    Then status 404
    And match response.error == 'Resource not found'

  Scenario: Remove tag from non-existent collection - returns 404
    * def removeTagRequest = { "name": "orphan-tag" }
    Given path 999999999, 'tags'
    And request removeTagRequest
    When method DELETE
    Then status 404
    And match response.error == 'Resource not found'
    And match response.message == 'Collection not found'

  Scenario: Remove tag with missing name field - returns 400
    * def invalidRequest = {}
    Given path createdCollectionId, 'tags'
    And request invalidRequest
    When method DELETE
    Then status 400

  Scenario: Remove tag with empty name - returns 400
    * def invalidRequest = { "name": "" }
    Given path createdCollectionId, 'tags'
    And request invalidRequest
    When method DELETE
    Then status 400

  Scenario: Remove tag verifies correct Content-Type header
    # First add a tag
    * def addTagRequest = { "name": "content-type-remove" }
    Given path createdCollectionId, 'tags'
    And request addTagRequest
    When method POST
    Then status 201

    # Remove and verify header
    * def removeTagRequest = { "name": "content-type-remove" }
    Given path createdCollectionId, 'tags'
    And request removeTagRequest
    When method DELETE
    Then status 200
    And match header Content-Type contains 'application/json'

  Scenario: Remove tag and verify count decreases
    # Add two tags
    * def tag1 = { "name": "count-test-1" }
    * def tag2 = { "name": "count-test-2" }

    Given path createdCollectionId, 'tags'
    And request tag1
    When method POST
    Then status 201

    Given path createdCollectionId, 'tags'
    And request tag2
    When method POST
    Then status 201
    * def countAfterAdd = response.tags.length

    # Remove one tag
    * def removeTagRequest = { "name": "count-test-1" }
    Given path createdCollectionId, 'tags'
    And request removeTagRequest
    When method DELETE
    Then status 200
    * def countAfterRemove = response.tags.length
    * assert countAfterRemove == countAfterAdd - 1

  # =====================================================
  # Create Collection with Tags Tests (POST /collections)
  # =====================================================

  Scenario: Create collection with tags during creation - success
    * def createWithTagsRequest =
      """
      {
        "name": "Collection With Initial Tags",
        "description": "Created with tags",
        "visibility": "PUBLIC",
        "collaborationMode": "OWNER_ONLY",
        "tags": ["breakfast", "quick", "healthy"]
      }
      """
    Given path ''
    And request createWithTagsRequest
    When method POST
    Then status 201
    And match response.collectionId == '#number'
    And match response.name == 'Collection With Initial Tags'

    # Verify tags were added by getting them
    * def newCollectionId = response.collectionId
    Given path newCollectionId, 'tags'
    When method GET
    Then status 200
    And match response.tags == '#[3]'
    And match response.tags[*].name contains 'breakfast'
    And match response.tags[*].name contains 'quick'
    And match response.tags[*].name contains 'healthy'

  Scenario: Create collection with empty tags array - success
    * def createWithEmptyTagsRequest =
      """
      {
        "name": "Collection With Empty Tags",
        "description": "Created with empty tags array",
        "visibility": "PUBLIC",
        "collaborationMode": "OWNER_ONLY",
        "tags": []
      }
      """
    Given path ''
    And request createWithEmptyTagsRequest
    When method POST
    Then status 201

    # Verify no tags
    * def newCollectionId = response.collectionId
    Given path newCollectionId, 'tags'
    When method GET
    Then status 200
    And match response.tags == []

  Scenario: Create collection with duplicate tags - deduplicates
    * def createWithDuplicatesRequest =
      """
      {
        "name": "Collection With Duplicate Tags",
        "description": "Created with duplicate tags",
        "visibility": "PUBLIC",
        "collaborationMode": "OWNER_ONLY",
        "tags": ["breakfast", "BREAKFAST", "Breakfast"]
      }
      """
    Given path ''
    And request createWithDuplicatesRequest
    When method POST
    Then status 201

    # Verify only one tag after deduplication
    * def newCollectionId = response.collectionId
    Given path newCollectionId, 'tags'
    When method GET
    Then status 200
    And match response.tags == '#[1]'

  # =====================================================
  # Get Collection By ID includes Tags Tests
  # =====================================================

  Scenario: Get collection by ID includes tags in response
    # Create collection with tags
    * def createWithTagsRequest =
      """
      {
        "name": "Collection For Get Test",
        "description": "Testing get includes tags",
        "visibility": "PUBLIC",
        "collaborationMode": "OWNER_ONLY",
        "tags": ["dinner", "vegetarian"]
      }
      """
    Given path ''
    And request createWithTagsRequest
    When method POST
    Then status 201
    * def newCollectionId = response.collectionId

    # Get collection by ID and verify tags included
    Given path newCollectionId
    When method GET
    Then status 200
    And match response.collectionId == newCollectionId
    And match response.tags == '#array'
    And match response.tags == '#[2]'
    And match response.tags[*].name contains 'dinner'
    And match response.tags[*].name contains 'vegetarian'
    And match response.tags[0] ==
      """
      {
        tagId: '#number',
        name: '#string'
      }
      """
