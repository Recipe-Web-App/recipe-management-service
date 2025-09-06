Feature: Info Endpoint

  Scenario: Info endpoint should return application information
    Given url baseUrl + '/api/v1/recipe-management/actuator/info'
    When method GET
    Then status 200
    And match response contains { app: '#object' }
