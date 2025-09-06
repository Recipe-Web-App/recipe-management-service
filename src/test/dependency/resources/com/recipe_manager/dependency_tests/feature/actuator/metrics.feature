Feature: Metrics Endpoint

  Scenario: Metrics endpoint should return available metrics
    Given url baseUrl + '/api/v1/recipe-management/actuator/metrics'
    When method GET
    Then status 200
    And match response contains { names: '#array' }
