package com.recipe_manager.exception;

/**
 * Exception thrown when attempting to create a resource that already exists.
 *
 * <p>This exception is used to indicate that a requested operation would create a duplicate
 * resource that conflicts with an existing one. It will be caught by the global exception handler
 * and converted to an appropriate HTTP 409 Conflict response.
 */
public class DuplicateResourceException extends RuntimeException {

  /**
   * Constructs a new DuplicateResourceException with the specified message.
   *
   * @param message The detail message
   */
  public DuplicateResourceException(final String message) {
    super(message);
  }

  /**
   * Constructs a new DuplicateResourceException with the specified message and cause.
   *
   * @param message The detail message
   * @param cause The cause of the exception
   */
  public DuplicateResourceException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Creates a DuplicateResourceException for a specific resource type and identifier.
   *
   * @param entityType The type of entity that already exists
   * @param identifier The identifier that caused the conflict
   * @return A new DuplicateResourceException with a formatted message
   */
  public static DuplicateResourceException forEntity(
      final String entityType, final Object identifier) {
    return new DuplicateResourceException(
        String.format("%s with identifier '%s' already exists", entityType, identifier));
  }
}
