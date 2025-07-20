package com.recipe_manager.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Test class for BusinessException.
 * Verifies that business exceptions work correctly.
 */
@Tag("unit")
class BusinessExceptionTest {

  /**
   * Test that business exception can be instantiated with message.
   */
  @Test
  @Tag("error-processing")
  @DisplayName("Should create business exception with message")
  void shouldCreateBusinessExceptionWithMessage() {
    // Given
    String message = "Business error occurred";

    // When
    BusinessException exception = new BusinessException(message);

    // Then
    assertNotNull(exception);
    assertEquals(message, exception.getMessage());
  }

  /**
   * Test that business exception can be instantiated with message and cause.
   */
  @Test
  @Tag("error-processing")
  @DisplayName("Should create business exception with message and cause")
  void shouldCreateBusinessExceptionWithMessageAndCause() {
    // Given
    String message = "Business error occurred";
    Throwable cause = new RuntimeException("Root cause");

    // When
    BusinessException exception = new BusinessException(message, cause);

    // Then
    assertNotNull(exception);
    assertEquals(message, exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  /**
   * Test that business exception can be instantiated with cause only.
   */
  @Test
  @Tag("error-processing")
  @DisplayName("Should create business exception with cause only")
  void shouldCreateBusinessExceptionWithCauseOnly() {
    // Given
    Throwable cause = new RuntimeException("Root cause");
    String message = "Business error occurred";

    // When
    BusinessException exception = new BusinessException(message, cause);

    // Then
    assertNotNull(exception);
    assertEquals(cause, exception.getCause());
  }

  /**
   * Test that business exception can be thrown.
   */
  @Test
  @Tag("error-processing")
  @DisplayName("Should throw business exception")
  void shouldThrowBusinessException() {
    // Given
    String message = "Business error occurred";

    // When & Then
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      throw new BusinessException(message);
    });

    assertEquals(message, exception.getMessage());
  }
}
