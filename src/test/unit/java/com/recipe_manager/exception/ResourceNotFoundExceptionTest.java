package com.recipe_manager.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Test class for ResourceNotFoundException.
 * Verifies that resource not found exceptions work correctly.
 */
@Tag("unit")
class ResourceNotFoundExceptionTest {

  /**
   * Test that resource not found exception can be instantiated with message.
   */
  @Test
  @Tag("error-processing")
  @DisplayName("Should create resource not found exception with message")
  void shouldCreateResourceNotFoundExceptionWithMessage() {
    // Given
    String message = "Resource not found";

    // When
    ResourceNotFoundException exception = new ResourceNotFoundException(message);

    // Then
    assertNotNull(exception);
    assertEquals(message, exception.getMessage());
  }

  /**
   * Test that resource not found exception can be instantiated with message and
   * cause.
   */
  @Test
  @Tag("error-processing")
  @DisplayName("Should create resource not found exception with message and cause")
  void shouldCreateResourceNotFoundExceptionWithMessageAndCause() {
    // Given
    String message = "Resource not found";
    Throwable cause = new RuntimeException("Root cause");

    // When
    ResourceNotFoundException exception = new ResourceNotFoundException(message, cause);

    // Then
    assertNotNull(exception);
    assertEquals(message, exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  /**
   * Test that resource not found exception can be instantiated with cause only.
   */
  @Test
  @Tag("error-processing")
  @DisplayName("Should create resource not found exception with cause only")
  void shouldCreateResourceNotFoundExceptionWithCauseOnly() {
    // Given
    Throwable cause = new RuntimeException("Root cause");
    String message = "Resource not found";

    // When
    ResourceNotFoundException exception = new ResourceNotFoundException(message, cause);

    // Then
    assertNotNull(exception);
    assertEquals(cause, exception.getCause());
  }

  /**
   * Test that resource not found exception can be thrown.
   */
  @Test
  @Tag("error-processing")
  @DisplayName("Should throw resource not found exception")
  void shouldThrowResourceNotFoundException() {
    // Given
    String message = "Resource not found";

    // When & Then
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      throw new ResourceNotFoundException(message);
    });

    assertEquals(message, exception.getMessage());
  }

  /**
   * Test that resource not found exception can be instantiated with resource type
   * and ID.
   */
  @Test
  @Tag("error-processing")
  @DisplayName("Should create resource not found exception with resource type and ID")
  void shouldCreateResourceNotFoundExceptionWithResourceTypeAndId() {
    // Given
    String resourceType = "Recipe";
    String resourceId = "123";

    // When
    ResourceNotFoundException exception = ResourceNotFoundException.forEntity(resourceType, resourceId);

    // Then
    assertNotNull(exception);
    assertTrue(exception.getMessage().contains(resourceType));
    assertTrue(exception.getMessage().contains(resourceId));
  }
}
