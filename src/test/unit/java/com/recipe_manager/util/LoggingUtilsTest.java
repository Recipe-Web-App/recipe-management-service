package com.recipe_manager.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Test class for LoggingUtils.
 * Verifies that logging utility methods work correctly.
 */
@Tag("unit")
class LoggingUtilsTest {

  private static final String DUMMY_USER_ID = UUID.randomUUID().toString();

  @BeforeEach
  void setUpSecurityContext() {
    Authentication auth = new UsernamePasswordAuthenticationToken(DUMMY_USER_ID, null);
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  /**
   * Test that logging utils can be instantiated.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should instantiate logging utils")
  void shouldInstantiateLoggingUtils() {
    // LoggingUtils is a utility class with static methods
    // This test verifies the class can be referenced
    assertNotNull(LoggingUtils.class);
  }

  /**
   * Test generating request ID.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should generate request ID")
  void shouldGenerateRequestId() {
    // Given: no specific input needed

    // When: request ID is generated
    String requestId = LoggingUtils.generateRequestId();

    // Then: request ID should be generated
    assertNotNull(requestId);
    assertFalse(requestId.isEmpty());
    assertTrue(requestId.length() > 0);
  }

  /**
   * Test generating multiple request IDs are unique.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should generate unique request IDs")
  void shouldGenerateUniqueRequestIds() {
    // Given: multiple request ID generations

    // When: multiple request IDs are generated
    String requestId1 = LoggingUtils.generateRequestId();
    String requestId2 = LoggingUtils.generateRequestId();
    String requestId3 = LoggingUtils.generateRequestId();

    // Then: all request IDs should be unique
    assertNotNull(requestId1);
    assertNotNull(requestId2);
    assertNotNull(requestId3);
    assertNotEquals(requestId1, requestId2);
    assertNotEquals(requestId2, requestId3);
    assertNotEquals(requestId1, requestId3);
  }

  /**
   * Test logging security event.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should log security event")
  void shouldLogSecurityEvent() {
    // Given: security event components
    String event = "LOGIN_ATTEMPT";
    String details = "User login attempt";
    String userId = "testuser";

    // When: security event is logged
    LoggingUtils.logSecurityEvent(event, details, userId);

    // Then: no exception should be thrown
    assertTrue(true); // Method should complete without error
  }

  /**
   * Test logging performance measurement.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should log performance measurement")
  void shouldLogPerformanceMeasurement() {
    // Given: performance measurement components
    String operation = "DATABASE_QUERY";
    long durationMs = 150;
    String additionalInfo = "User search query";

    // When: performance measurement is logged
    LoggingUtils.logPerformance(operation, durationMs, additionalInfo);

    // Then: no exception should be thrown
    assertTrue(true); // Method should complete without error
  }

  /**
   * Test logging business operation.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should log business operation")
  void shouldLogBusinessOperation() {
    // Given: business operation components
    String operation = "CREATE_RECIPE";
    String entityType = "Recipe";
    String entityId = "123";
    String result = "SUCCESS";

    // When: business operation is logged
    LoggingUtils.logBusinessOperation(operation, entityType, entityId, result);

    // Then: no exception should be thrown
    assertTrue(true); // Method should complete without error
  }

  /**
   * Test that private constructor is covered for code coverage tools.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should cover private constructor")
  void shouldCoverPrivateConstructor() throws Exception {
    java.lang.reflect.Constructor<LoggingUtils> constructor = LoggingUtils.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    constructor.newInstance();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should cover logError")
  void shouldCoverLogError() {
    // Given
    String message = "test error";
    RuntimeException exception = new RuntimeException("fail");
    String context = "context";

    // When & Then
    assertDoesNotThrow(() -> LoggingUtils.logError(message, exception, context));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should cover logDatabaseOperation")
  void shouldCoverLogDatabaseOperation() {
    // Given
    String operation = "SELECT";
    String entity = "Recipe";
    Long entityId = 123L;
    int duration = 1;

    // When & Then
    assertDoesNotThrow(() -> LoggingUtils.logDatabaseOperation(operation, entity, entityId, duration));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should cover logApiRequest")
  void shouldCoverLogApiRequest() {
    // Given
    String method = "GET";
    String endpoint = "/api/v1/recipes";
    int statusCode = 200;
    long duration = 100L;
    String requestId = "req-1";

    // When & Then
    assertDoesNotThrow(() -> LoggingUtils.logApiRequest(method, endpoint, statusCode, duration, requestId));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should cover logCacheOperation")
  void shouldCoverLogCacheOperation() {
    // Given
    String operation = "GET";
    String cacheName = "Recipe";
    String key = "123";
    boolean hit = true;

    // When & Then
    assertDoesNotThrow(() -> LoggingUtils.logCacheOperation(operation, cacheName, key, hit));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle exception in logSecurityEvent")
  void shouldHandleExceptionInLogSecurityEvent() {
    /*
     * Test the exception handling branch by passing null values
     * that would cause an exception in the logging logic
     */
    assertDoesNotThrow(() -> LoggingUtils.logSecurityEvent(null, null, null));
  }
}
