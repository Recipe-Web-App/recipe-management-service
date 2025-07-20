Feature: Configuration Properties Endpoint

  Scenario: Configuration properties endpoint should return configuration
    Given url baseUrl + '/api/v1/recipe-manager/actuator/configprops'
    When method GET
    Then status 200
    And match response contains { contexts: '#object' }
