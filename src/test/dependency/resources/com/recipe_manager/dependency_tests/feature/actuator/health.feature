Feature: Health Endpoint

  Scenario: Health endpoint should return UP status
    Given url baseUrl + '/api/v1/recipe-manager/actuator/health'
    When method GET
    Then status 200
    And match response.status == 'UP'

  Scenario: Health endpoint should return detailed health information
    Given url baseUrl + '/api/v1/recipe-manager/actuator/health'
    When method GET
    Then status 200
    And match response contains { status: '#string', components: '#object' }
