package com.recipe_manager.service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Service for managing database connection health and retry logic.
 *
 * <p>This service periodically attempts to reconnect to the database when it's unavailable, tracks
 * connection status, and provides information about the last successful connection.
 */
@Service
@ConditionalOnProperty(
    name = "app.database.connection-retry.enabled",
    havingValue = "true",
    matchIfMissing = true)
public final class DatabaseConnectionService {

  /** Logger for database connection monitoring. */
  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConnectionService.class);

  /** The JdbcTemplate used for database connectivity checks. */
  private final JdbcTemplate jdbcTemplate;

  /** Tracks whether the database is currently connected. */
  private final AtomicBoolean isConnected = new AtomicBoolean(false);

  /** Timestamp of the last successful database connection. */
  private final AtomicReference<LocalDateTime> lastSuccessfulConnection =
      new AtomicReference<>(null);

  /** Timestamp of the last connection attempt. */
  private final AtomicReference<LocalDateTime> lastConnectionAttempt = new AtomicReference<>(null);

  /** The last error message encountered during connection attempts. */
  private final AtomicReference<String> lastError = new AtomicReference<>(null);

  /**
   * Creates a new DatabaseConnectionService.
   *
   * @param jdbcTemplate The JdbcTemplate for database operations
   */
  @Autowired
  public DatabaseConnectionService(final JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Periodically attempts to check database connectivity.
   *
   * <p>This method runs every 30 seconds and attempts to execute a simple query to verify database
   * connectivity. Connection status is updated accordingly.
   */
  /** Database connection check interval in milliseconds. */
  private static final int CONNECTION_CHECK_INTERVAL_MS = 30000;

  /** Initial delay before first connection check in milliseconds. */
  private static final int INITIAL_DELAY_MS = 5000;

  @Scheduled(fixedDelay = CONNECTION_CHECK_INTERVAL_MS, initialDelay = INITIAL_DELAY_MS)
  public void checkDatabaseConnection() {
    lastConnectionAttempt.set(LocalDateTime.now());

    try {
      jdbcTemplate.queryForObject("SELECT 1", Integer.class);
      boolean wasDisconnected = !isConnected.get();
      isConnected.set(true);
      lastSuccessfulConnection.set(LocalDateTime.now());
      lastError.set(null);

      if (wasDisconnected) {
        LOGGER.info("Database connection restored successfully");
      } else {
        LOGGER.debug("Database connection health check passed");
      }

    } catch (Exception e) {
      boolean wasConnected = isConnected.get();
      isConnected.set(false);
      lastError.set(e.getMessage());

      if (wasConnected) {
        LOGGER.warn("Database connection lost: {}", e.getMessage());
      } else {
        LOGGER.debug("Database connection attempt failed: {}", e.getMessage());
      }
    }
  }

  /**
   * Checks if the database is currently connected.
   *
   * @return true if the last connection attempt was successful, false otherwise
   */
  public boolean isConnected() {
    return isConnected.get();
  }

  /**
   * Gets the timestamp of the last successful database connection.
   *
   * @return LocalDateTime of last successful connection, or null if never connected
   */
  public LocalDateTime getLastSuccessfulConnection() {
    return lastSuccessfulConnection.get();
  }

  /**
   * Gets the timestamp of the last connection attempt.
   *
   * @return LocalDateTime of last connection attempt, or null if no attempts made
   */
  public LocalDateTime getLastConnectionAttempt() {
    return lastConnectionAttempt.get();
  }

  /**
   * Gets the last error message from a failed connection attempt.
   *
   * @return String containing the last error message, or null if no errors
   */
  public String getLastError() {
    return lastError.get();
  }

  /**
   * Performs an immediate database connection check.
   *
   * @return true if the connection is successful, false otherwise
   */
  public boolean performImmediateCheck() {
    checkDatabaseConnection();
    return isConnected();
  }
}
