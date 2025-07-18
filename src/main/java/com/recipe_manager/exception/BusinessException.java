package com.recipe_manager.exception;

/**
 * Exception thrown when a business rule is violated or business logic fails.
 *
 * <p>This exception is used to indicate that a business operation could not be completed due to
 * business rule violations or logical errors. It will be caught by the global exception handler and
 * converted to an appropriate HTTP 400 response.
 */
public class BusinessException extends RuntimeException {

  /**
   * Constructs a new BusinessException with the specified message.
   *
   * @param message The detail message
   */
  public BusinessException(final String message) {
    super(message);
  }

  /**
   * Constructs a new BusinessException with the specified message and cause.
   *
   * @param message The detail message
   * @param cause The cause of the exception
   */
  public BusinessException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
