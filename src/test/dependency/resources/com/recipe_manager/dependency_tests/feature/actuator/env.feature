Feature: Environment Endpoint

  Scenario: Environment endpoint should return environment variables
    Given url baseUrl + '/api/v1/recipe-management/actuator/env'
    When method GET
    Then status 200
    And match response contains { propertySources: '#array' }
