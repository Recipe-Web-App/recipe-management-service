package com.recipe_manager.exception;

/** Exception thrown when there is an error serializing or deserializing revision data. */
public class RevisionSerializationException extends RuntimeException {

  /**
   * Constructs a new RevisionSerializationException with the specified detail message.
   *
   * @param message the detail message
   */
  public RevisionSerializationException(String message) {
    super(message);
  }

  /**
   * Constructs a new RevisionSerializationException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause
   */
  public RevisionSerializationException(String message, Throwable cause) {
    super(message, cause);
  }
}
