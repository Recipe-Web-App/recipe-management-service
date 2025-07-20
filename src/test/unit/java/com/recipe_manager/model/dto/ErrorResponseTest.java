package com.recipe_manager.model.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Test class for ErrorResponse.
 * Verifies that error response DTO works correctly.
 */
@Tag("unit")
class ErrorResponseTest {

  /**
   * Test that error response can be instantiated with all fields.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should create error response with all fields")
  void shouldCreateErrorResponseWithAllFields() {
    // Given
    LocalDateTime timestamp = LocalDateTime.now();
    int status = 400;
    String error = "Bad Request";
    String message = "This is a test error message";
    String path = "/api/test";
    String requestId = "test-request-id";
    Map<String, String> details = new HashMap<>();
    details.put("field1", "error1");
    details.put("field2", "error2");

    // When
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(timestamp)
        .status(status)
        .error(error)
        .message(message)
        .path(path)
        .requestId(requestId)
        .details(details)
        .build();

    // Then
    assertNotNull(errorResponse);
    assertEquals(timestamp, errorResponse.getTimestamp());
    assertEquals(status, errorResponse.getStatus());
    assertEquals(error, errorResponse.getError());
    assertEquals(message, errorResponse.getMessage());
    assertEquals(path, errorResponse.getPath());
    assertEquals(requestId, errorResponse.getRequestId());
    assertEquals(details, errorResponse.getDetails());
  }

  /**
   * Test that error response can be instantiated without details.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should create error response without details")
  void shouldCreateErrorResponseWithoutDetails() {
    // Given
    LocalDateTime timestamp = LocalDateTime.now();
    int status = 404;
    String error = "Not Found";
    String message = "This is a test error message";
    String path = "/api/test";
    String requestId = "test-request-id";

    // When
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(timestamp)
        .status(status)
        .error(error)
        .message(message)
        .path(path)
        .requestId(requestId)
        .build();

    // Then
    assertNotNull(errorResponse);
    assertEquals(timestamp, errorResponse.getTimestamp());
    assertEquals(status, errorResponse.getStatus());
    assertEquals(error, errorResponse.getError());
    assertEquals(message, errorResponse.getMessage());
    assertEquals(path, errorResponse.getPath());
    assertEquals(requestId, errorResponse.getRequestId());
    assertNull(errorResponse.getDetails());
  }

  /**
   * Test that error response can be instantiated with minimal fields.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should create error response with minimal fields")
  void shouldCreateErrorResponseWithMinimalFields() {
    // Given
    LocalDateTime timestamp = LocalDateTime.now();
    int status = 500;
    String error = "Internal Server Error";
    String message = "This is a test error message";

    // When
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(timestamp)
        .status(status)
        .error(error)
        .message(message)
        .build();

    // Then
    assertNotNull(errorResponse);
    assertEquals(timestamp, errorResponse.getTimestamp());
    assertEquals(status, errorResponse.getStatus());
    assertEquals(error, errorResponse.getError());
    assertEquals(message, errorResponse.getMessage());
  }

  /**
   * Test that error response can be instantiated with empty details map.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should create error response with empty details map")
  void shouldCreateErrorResponseWithEmptyDetailsMap() {
    // Given
    LocalDateTime timestamp = LocalDateTime.now();
    int status = 422;
    String error = "Unprocessable Entity";
    String message = "This is a test error message";
    String path = "/api/test";
    String requestId = "test-request-id";
    Map<String, String> details = new HashMap<>();

    // When
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(timestamp)
        .status(status)
        .error(error)
        .message(message)
        .path(path)
        .requestId(requestId)
        .details(details)
        .build();

    // Then
    assertNotNull(errorResponse);
    assertEquals(timestamp, errorResponse.getTimestamp());
    assertEquals(status, errorResponse.getStatus());
    assertEquals(error, errorResponse.getError());
    assertEquals(message, errorResponse.getMessage());
    assertEquals(path, errorResponse.getPath());
    assertEquals(requestId, errorResponse.getRequestId());
    assertNotNull(errorResponse.getDetails());
    assertTrue(errorResponse.getDetails().isEmpty());
  }

  /**
   * Test that error response can be instantiated with null request ID.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should create error response with null request ID")
  void shouldCreateErrorResponseWithNullRequestId() {
    // Given
    LocalDateTime timestamp = LocalDateTime.now();
    int status = 400;
    String error = "Bad Request";
    String message = "This is a test error message";

    // When
    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(timestamp)
        .status(status)
        .error(error)
        .message(message)
        .build();

    // Then
    assertNotNull(errorResponse);
    assertEquals(timestamp, errorResponse.getTimestamp());
    assertEquals(status, errorResponse.getStatus());
    assertEquals(error, errorResponse.getError());
    assertEquals(message, errorResponse.getMessage());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return unmodifiable details map")
  void shouldReturnUnmodifiableDetailsMap() {
    Map<String, String> details = new HashMap<>();
    details.put("field", "error");
    ErrorResponse errorResponse = ErrorResponse.builder().details(details).build();
    Map<String, String> returned = errorResponse.getDetails();
    assertNotNull(returned);
    assertTrue(returned.containsKey("field"));
    assertThrows(UnsupportedOperationException.class, () -> returned.put("x", "y"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should copy details map defensively in setDetails")
  void shouldCopyDetailsMapDefensively() {
    Map<String, String> details = new HashMap<>();
    details.put("field", "error");
    ErrorResponse errorResponse = ErrorResponse.builder().build();
    errorResponse.setDetails(details);
    details.put("field2", "error2");
    assertFalse(errorResponse.getDetails().containsKey("field2"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should set details to null when null parameter")
  void shouldSetDetailsToNullWhenNullParameter() {
    ErrorResponse errorResponse = ErrorResponse.builder()
        .error("Test Error")
        .message("Test Message")
        .build();

    errorResponse.setDetails(null);
    assertNull(errorResponse.getDetails());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    LocalDateTime timestamp = LocalDateTime.now();
    ErrorResponse e1 = ErrorResponse.builder().timestamp(timestamp).status(400).error("e").message("m").build();
    ErrorResponse e2 = ErrorResponse.builder().timestamp(timestamp).status(400).error("e").message("m").build();
    assertEquals(e1, e2);
    assertEquals(e1.hashCode(), e2.hashCode());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should implement toString")
  void shouldImplementToString() {
    ErrorResponse e = ErrorResponse.builder().status(400).error("e").message("m").build();
    assertTrue(e.toString().contains("400"));
    assertTrue(e.toString().contains("e"));
    assertTrue(e.toString().contains("m"));
  }
}
