Feature: Search Recipes Endpoint

  Background:
    * url baseUrl + '/recipe-management/recipes/search'
    * def emptySearchRequest = {}
    * def titleSearchRequest =
      """
      {
        "recipeNameQuery": "pasta"
      }
      """
    * def fullSearchRequest =
      """
      {
        "recipeNameQuery": "chicken",
        "difficulty": "EASY",
        "maxCookingTime": 30,
        "maxPreparationTime": 20,
        "minServings": 2,
        "maxServings": 6,
        "ingredientNames": ["chicken", "rice"],
        "ingredientMatchMode": "AND"
      }
      """
    * def invalidSearchRequest =
      """
      {
        "maxCookingTime": -1,
        "maxPreparationTime": -1,
        "minServings": -1
      }
      """

  @smoke
  Scenario: Successfully search recipes with empty criteria
    Given request emptySearchRequest
    When method POST
    Then status 200
    And match response.recipes == '#array'
    And match response.page == '#number'
    And match response.size == '#number'
    And match response.totalElements == '#number'
    And match response.totalPages == '#number'
    And match response.first == '#boolean'
    And match response.last == '#boolean'
    And match response.numberOfElements == '#number'
    And match response.empty == '#boolean'

  @positive
  Scenario: Successfully search recipes by title
    Given request titleSearchRequest
    When method POST
    Then status 200
    And match response.recipes == '#array'
    And match response.page == 0
    And match response.size == 20

  @positive
  Scenario: Successfully search recipes with all criteria
    Given request fullSearchRequest
    When method POST
    Then status 200
    And match response.recipes == '#array'
    And match response.page == 0
    And match response.size == 20

  @positive
  Scenario: Successfully search recipes with pagination
    Given request emptySearchRequest
    And param page = 0
    And param size = 5
    When method POST
    Then status 200
    And match response.recipes == '#array'
    And match response.size == 5
    And match response.page == 0

  @positive
  Scenario: Successfully search recipes with custom sort
    Given request emptySearchRequest
    And param sort = 'title,asc'
    When method POST
    Then status 200
    And match response.recipes == '#array'

  @positive
  Scenario: Search returns empty results when no recipes match
    * def noMatchRequest = { "recipeNameQuery": "nonexistentrecipe12345" }
    Given request noMatchRequest
    When method POST
    Then status 200
    And match response.recipes == '#[0]'
    And match response.totalElements == 0
    And match response.empty == true

  @negative
  Scenario: Return 400 for invalid search criteria
    Given request invalidSearchRequest
    When method POST
    Then status 400

  @negative
  Scenario: Return 400 for malformed JSON request body
    Given request 'invalid json'
    When method POST
    Then status 400

  @negative
  Scenario: Return 415 for unsupported content type
    Given request emptySearchRequest
    And header Content-Type = 'text/plain'
    When method POST
    Then status 415

  @boundary
  Scenario: Search with maximum allowed page size
    Given request emptySearchRequest
    And param size = 100
    When method POST
    Then status 200
    And match response.size <= 100

  @boundary
  Scenario: Search with minimum page number
    Given request emptySearchRequest
    And param page = 0
    When method POST
    Then status 200
    And match response.page == 0

  @performance
  Scenario: Search response time is acceptable
    Given request emptySearchRequest
    When method POST
    Then status 200
    And assert responseTime < 5000

  @integration
  Scenario: Search with ingredient match mode AND
    * def andSearchRequest =
      """
      {
        "ingredientNames": ["chicken", "rice"],
        "ingredientMatchMode": "AND"
      }
      """
    Given request andSearchRequest
    When method POST
    Then status 200
    And match response.recipes == '#array'

  @integration
  Scenario: Search with ingredient match mode OR
    * def orSearchRequest =
      """
      {
        "ingredientNames": ["chicken", "rice"],
        "ingredientMatchMode": "OR"
      }
      """
    Given request orSearchRequest
    When method POST
    Then status 200
    And match response.recipes == '#array'

  @integration
  Scenario: Search with difficulty filter
    * def difficultySearchRequest = { "difficulty": "EASY" }
    Given request difficultySearchRequest
    When method POST
    Then status 200
    And match response.recipes == '#array'

  @integration
  Scenario: Search with time constraints
    * def timeSearchRequest =
      """
      {
        "maxCookingTime": 15,
        "maxPreparationTime": 10
      }
      """
    Given request timeSearchRequest
    When method POST
    Then status 200
    And match response.recipes == '#array'

  @integration
  Scenario: Search with serving size range
    * def servingSearchRequest =
      """
      {
        "minServings": 2,
        "maxServings": 4
      }
      """
    Given request servingSearchRequest
    When method POST
    Then status 200
    And match response.recipes == '#array'
