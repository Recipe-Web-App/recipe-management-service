package com.recipe_manager.exception;

/**
 * Exception thrown when a requested resource is not found.
 *
 * <p>This exception is used to indicate that a requested entity or resource does not exist in the
 * system. It will be caught by the global exception handler and converted to an appropriate HTTP
 * 404 response.
 */
public class ResourceNotFoundException extends RuntimeException {

  /**
   * Constructs a new ResourceNotFoundException with the specified message.
   *
   * @param message The detail message
   */
  public ResourceNotFoundException(final String message) {
    super(message);
  }

  /**
   * Constructs a new ResourceNotFoundException with the specified message and cause.
   *
   * @param message The detail message
   * @param cause The cause of the exception
   */
  public ResourceNotFoundException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Creates a ResourceNotFoundException for a specific entity type and identifier.
   *
   * @param entityType The type of entity that was not found
   * @param identifier The identifier that was used to search for the entity
   * @return A new ResourceNotFoundException with a formatted message
   */
  public static ResourceNotFoundException forEntity(
      final String entityType, final Object identifier) {
    return new ResourceNotFoundException(
        String.format("%s with identifier '%s' was not found", entityType, identifier));
  }
}
