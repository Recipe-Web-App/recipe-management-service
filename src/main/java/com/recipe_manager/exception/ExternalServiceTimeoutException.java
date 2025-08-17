package com.recipe_manager.exception;

import com.recipe_manager.model.enums.ExternalServiceName;

/**
 * Exception thrown when an external service call times out. Indicates that the service did not
 * respond within the configured timeout period.
 */
public final class ExternalServiceTimeoutException extends ExternalServiceException {

  /** Duration in milliseconds after which the service timed out. */
  private final long timeoutDuration;

  public ExternalServiceTimeoutException(
      final ExternalServiceName serviceName, final long timeoutDuration) {
    super(
        serviceName,
        String.format(
            "External service %s timed out after %d milliseconds", serviceName, timeoutDuration));
    this.timeoutDuration = timeoutDuration;
  }

  public ExternalServiceTimeoutException(
      final ExternalServiceName serviceName, final long timeoutDuration, final Throwable cause) {
    super(
        serviceName,
        String.format(
            "External service %s timed out after %d milliseconds", serviceName, timeoutDuration),
        cause);
    this.timeoutDuration = timeoutDuration;
  }

  /**
   * Gets the timeout duration that was exceeded.
   *
   * @return timeout duration in milliseconds
   */
  public long getTimeoutDuration() {
    return timeoutDuration;
  }

  /**
   * Determines if this timeout exception is retryable.
   *
   * @return true as timeouts are generally retryable
   */
  @Override
  public boolean isRetryable() {
    return true; // Timeouts are generally retryable
  }
}
