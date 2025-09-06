Feature: Prometheus Metrics Endpoint

  Scenario: Prometheus metrics endpoint should return metrics in Prometheus format
    Given url baseUrl + '/api/v1/recipe-management/actuator/prometheus'
    When method GET
    Then status 200
    And match response contains '#string'
