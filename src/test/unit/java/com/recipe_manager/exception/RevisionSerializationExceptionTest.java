package com.recipe_manager.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RevisionSerializationException class.
 */
@Tag("unit")
class RevisionSerializationExceptionTest {

  @Test
  void testConstructorWithMessage() {
    String message = "Test error message";
    RevisionSerializationException exception = new RevisionSerializationException(message);

    assertNotNull(exception);
    assertEquals(message, exception.getMessage());
  }

  @Test
  void testConstructorWithMessageAndCause() {
    String message = "Test error message";
    Throwable cause = new RuntimeException("Underlying cause");
    RevisionSerializationException exception = new RevisionSerializationException(message, cause);

    assertNotNull(exception);
    assertEquals(message, exception.getMessage());
    assertSame(cause, exception.getCause());
  }

  @Test
  void testExceptionIsRuntimeException() {
    RevisionSerializationException exception = new RevisionSerializationException("Test");

    // Verify it's a RuntimeException (unchecked exception)
    assertNotNull(exception);
    assertEquals(RuntimeException.class, exception.getClass().getSuperclass());
  }
}
