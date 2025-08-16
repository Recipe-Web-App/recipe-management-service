package com.recipe_manager.exception;

import org.springframework.http.HttpStatus;

import com.recipe_manager.model.enums.ExternalServiceName;

/**
 * Base exception for all external service-related errors. Provides a foundation for handling
 * failures in downstream service integrations.
 */
public class ExternalServiceException extends RuntimeException {

  /** Name of the external service that failed. */
  private final ExternalServiceName serviceName;

  /** HTTP status code from the failed service call, if available. */
  private final int statusCode;

  public ExternalServiceException(final ExternalServiceName serviceName, final String message) {
    super(message);
    this.serviceName = serviceName;
    this.statusCode = 0;
  }

  public ExternalServiceException(
      final ExternalServiceName serviceName, final String message, final Throwable cause) {
    super(message, cause);
    this.serviceName = serviceName;
    this.statusCode = 0;
  }

  public ExternalServiceException(
      final ExternalServiceName serviceName, final int statusCode, final String message) {
    super(message);
    this.serviceName = serviceName;
    this.statusCode = statusCode;
  }

  public ExternalServiceException(
      final ExternalServiceName serviceName,
      final int statusCode,
      final String message,
      final Throwable cause) {
    super(message, cause);
    this.serviceName = serviceName;
    this.statusCode = statusCode;
  }

  /**
   * Gets the external service that failed.
   *
   * @return service name enum
   */
  public ExternalServiceName getServiceName() {
    return serviceName;
  }

  /**
   * Gets the HTTP status code if available.
   *
   * @return HTTP status code, or 0 if not available
   */
  public int getStatusCode() {
    return statusCode;
  }

  /**
   * Checks if this exception represents a retryable error.
   *
   * @return true if the error is potentially retryable
   */
  public boolean isRetryable() {
    return statusCode == 0
        || HttpStatus.valueOf(statusCode).is5xxServerError()
        || statusCode == HttpStatus.TOO_MANY_REQUESTS.value();
  }
}
