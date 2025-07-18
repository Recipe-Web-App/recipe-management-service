package com.recipe_manager.util;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Utility class for common logging operations and request tracking.
 *
 * <p>This utility provides:
 *
 * <ul>
 *   <li>Request ID generation and management
 *   <li>Structured logging helpers
 *   <li>Performance measurement utilities
 *   <li>Security event logging
 * </ul>
 */
public final class LoggingUtils {

  /** Logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(LoggingUtils.class);

  private LoggingUtils() {
    // Utility class - prevent instantiation
  }

  /**
   * Generates a unique request ID for tracking requests.
   *
   * @return A unique request identifier
   */
  public static String generateRequestId() {
    return UUID.randomUUID().toString();
  }

  /**
   * Gets the current request ID from MDC context.
   *
   * @return The current request ID or null if not set
   */
  public static String getCurrentRequestId() {
    return MDC.get("requestId");
  }

  /**
   * Gets the current user ID from MDC context.
   *
   * @return The current user ID or null if not set
   */
  public static String getCurrentUserId() {
    return MDC.get("userId");
  }

  /**
   * Logs a security event with appropriate context.
   *
   * @param event The security event type
   * @param details Additional event details
   * @param userId The user ID (can be null for anonymous events)
   */
  public static void logSecurityEvent(
      final String event, final String details, final String userId) {
    LOGGER.warn(
        "SECURITY_EVENT: {} - {} - User: {} - RequestId: {}",
        event,
        details,
        userId != null ? userId : "anonymous",
        getCurrentRequestId());
  }

  /**
   * Logs a performance measurement.
   *
   * @param operation The operation being measured
   * @param durationMs The duration in milliseconds
   * @param additionalInfo Additional information about the operation
   */
  public static void logPerformance(
      final String operation, final long durationMs, final String additionalInfo) {
    LOGGER.info(
        "PERFORMANCE: {} - {}ms - {} - RequestId: {}",
        operation,
        durationMs,
        additionalInfo,
        getCurrentRequestId());
  }

  /**
   * Logs a business operation with structured information.
   *
   * @param operation The business operation
   * @param entityType The type of entity being operated on
   * @param entityId The ID of the entity
   * @param result The result of the operation
   */
  public static void logBusinessOperation(
      final String operation, final String entityType, final String entityId, final String result) {
    LOGGER.info(
        "BUSINESS_OPERATION: {} - Entity: {} - ID: {} - Result: {} - User: {} - RequestId: {}",
        operation,
        entityType,
        entityId,
        result,
        getCurrentUserId(),
        getCurrentRequestId());
  }

  /**
   * Logs an error with full context information.
   *
   * @param error The error message
   * @param exception The exception (can be null)
   * @param context Additional context information
   */
  public static void logError(final String error, final Throwable exception, final String context) {
    LOGGER.error(
        "ERROR: {} - Context: {} - User: {} - RequestId: {}",
        error,
        context,
        getCurrentUserId(),
        getCurrentRequestId(),
        exception);
  }

  /**
   * Logs a database operation for monitoring.
   *
   * @param operation The database operation (SELECT, INSERT, UPDATE, DELETE)
   * @param table The table name
   * @param durationMs The duration in milliseconds
   * @param rowsAffected The number of rows affected (for write operations)
   */
  public static void logDatabaseOperation(
      final String operation, final String table, final long durationMs, final int rowsAffected) {
    LOGGER.debug(
        "DATABASE: {} - Table: {} - Duration: {}ms - Rows: {} - RequestId: {}",
        operation,
        table,
        durationMs,
        rowsAffected,
        getCurrentRequestId());
  }

  /**
   * Logs an API request with structured information.
   *
   * @param method The HTTP method
   * @param path The request path
   * @param statusCode The response status code
   * @param durationMs The request duration in milliseconds
   * @param userAgent The user agent string
   */
  public static void logApiRequest(
      final String method,
      final String path,
      final int statusCode,
      final long durationMs,
      final String userAgent) {
    LOGGER.info(
        "API_REQUEST: {} {} - Status: {} - Duration: {}ms - User: {} - RequestId: {} - UserAgent: {}",
        method,
        path,
        statusCode,
        durationMs,
        getCurrentUserId(),
        getCurrentRequestId(),
        userAgent);
  }

  /**
   * Logs a cache operation for monitoring.
   *
   * @param operation The cache operation (GET, PUT, EVICT)
   * @param cacheName The cache name
   * @param key The cache key
   * @param hit Whether it was a cache hit
   */
  public static void logCacheOperation(
      final String operation, final String cacheName, final String key, final boolean hit) {
    LOGGER.debug(
        "CACHE: {} - Cache: {} - Key: {} - Hit: {} - RequestId: {}",
        operation,
        cacheName,
        key,
        hit,
        getCurrentRequestId());
  }
}
