package com.recipe_manager.model.enums;

/**
 * Enum representing external service names. Provides type-safe service identification for external
 * service integrations and error handling.
 */
public enum ExternalServiceName {
  /** Recipe scraper service for pricing and ingredient information. */
  RECIPE_SCRAPER("recipe-scraper"),

  /** User management service for authentication and user data. */
  USER_MANAGEMENT("user-management"),

  /** Media service for image and file management. */
  MEDIA_SERVICE("media-service"),

  /** Notification service for email and push notifications. */
  NOTIFICATION_SERVICE("notification-service");

  /** The service name identifier. */
  private final String serviceName;

  ExternalServiceName(final String serviceName) {
    this.serviceName = serviceName;
  }

  /**
   * Gets the service name as a string.
   *
   * @return service name
   */
  public String getServiceName() {
    return serviceName;
  }

  @Override
  public String toString() {
    return serviceName;
  }
}
