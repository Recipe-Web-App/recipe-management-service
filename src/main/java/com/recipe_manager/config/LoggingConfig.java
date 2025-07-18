package com.recipe_manager.config;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * Configuration for structured logging and request tracking.
 *
 * <p>This configuration sets up:
 *
 * <ul>
 *   <li>Structured logging with JSON format
 *   <li>Request/response logging
 *   <li>MDC (Mapped Diagnostic Context) for request tracking
 *   <li>Custom logging patterns
 * </ul>
 */
@Configuration
public class LoggingConfig {

  /** Default queue size for logging. */
  private static final int DEFAULT_QUEUE_SIZE = 10000;

  /**
   * Creates a request logging filter for tracking HTTP requests and responses.
   *
   * @return CommonsRequestLoggingFilter configured for detailed request logging
   */
  @Bean
  public CommonsRequestLoggingFilter requestLoggingFilter() {
    CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
    filter.setIncludeQueryString(true);
    filter.setIncludePayload(true);
    filter.setMaxPayloadLength(DEFAULT_QUEUE_SIZE);
    filter.setIncludeHeaders(true);
    filter.setAfterMessagePrefix("REQUEST DATA: ");
    filter.setBeforeMessagePrefix("REQUEST DATA: ");
    return filter;
  }

  /**
   * Generates a unique request ID for tracking requests across the application.
   *
   * @return A unique request identifier
   */
  public static String generateRequestId() {
    return UUID.randomUUID().toString();
  }

  /**
   * Sets up MDC context for the current request with request ID and other metadata.
   *
   * @param requestId The unique request identifier
   * @param userId The user identifier (can be null for anonymous requests)
   * @param sessionId The session identifier (can be null)
   */
  public static void setupMdcContext(
      final String requestId, final String userId, final String sessionId) {
    MDC.put("requestId", requestId);
    if (userId != null) {
      MDC.put("userId", userId);
    }
    if (sessionId != null) {
      MDC.put("sessionId", sessionId);
    }
    MDC.put("timestamp", String.valueOf(System.currentTimeMillis()));
  }

  /** Clears the MDC context for the current request. */
  public static void clearMdcContext() {
    MDC.clear();
  }
}
