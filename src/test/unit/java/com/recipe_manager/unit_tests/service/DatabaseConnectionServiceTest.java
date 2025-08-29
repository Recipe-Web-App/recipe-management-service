package com.recipe_manager.unit_tests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.recipe_manager.service.DatabaseConnectionService;

/**
 * Unit tests for {@link DatabaseConnectionService}.
 *
 * <p>Tests database connection monitoring, retry logic, and status tracking functionality.
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class DatabaseConnectionServiceTest {

  @Mock private JdbcTemplate jdbcTemplate;

  private DatabaseConnectionService databaseConnectionService;

  @BeforeEach
  void setUp() {
    databaseConnectionService = new DatabaseConnectionService(jdbcTemplate);
  }

  @Test
  void shouldInitializeWithDisconnectedState() {
    // Given - new service instance
    // When - checking initial state
    // Then
    assertFalse(databaseConnectionService.isConnected());
    assertNull(databaseConnectionService.getLastSuccessfulConnection());
    assertNull(databaseConnectionService.getLastConnectionAttempt());
    assertNull(databaseConnectionService.getLastError());
  }

  @Test
  void shouldUpdateStateOnSuccessfulConnection() {
    // Given
    when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenReturn(1);

    // When
    LocalDateTime before = LocalDateTime.now();
    databaseConnectionService.checkDatabaseConnection();
    LocalDateTime after = LocalDateTime.now();

    // Then
    assertTrue(databaseConnectionService.isConnected());
    assertNotNull(databaseConnectionService.getLastSuccessfulConnection());
    assertNotNull(databaseConnectionService.getLastConnectionAttempt());
    assertNull(databaseConnectionService.getLastError());

    // Verify timestamps are reasonable
    LocalDateTime lastSuccessful = databaseConnectionService.getLastSuccessfulConnection();
    LocalDateTime lastAttempt = databaseConnectionService.getLastConnectionAttempt();

    assertTrue(lastSuccessful.isAfter(before.minusSeconds(1)));
    assertTrue(lastSuccessful.isBefore(after.plusSeconds(1)));
    assertTrue(lastAttempt.isAfter(before.minusSeconds(1)));
    assertTrue(lastAttempt.isBefore(after.plusSeconds(1)));
  }

  @Test
  void shouldUpdateStateOnFailedConnection() {
    // Given
    String errorMessage = "Connection refused";
    when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
        .thenThrow(new DataAccessException(errorMessage) {});

    // When
    LocalDateTime before = LocalDateTime.now();
    databaseConnectionService.checkDatabaseConnection();
    LocalDateTime after = LocalDateTime.now();

    // Then
    assertFalse(databaseConnectionService.isConnected());
    assertNull(databaseConnectionService.getLastSuccessfulConnection());
    assertNotNull(databaseConnectionService.getLastConnectionAttempt());
    assertEquals(errorMessage, databaseConnectionService.getLastError());

    // Verify timestamp is reasonable
    LocalDateTime lastAttempt = databaseConnectionService.getLastConnectionAttempt();
    assertTrue(lastAttempt.isAfter(before.minusSeconds(1)));
    assertTrue(lastAttempt.isBefore(after.plusSeconds(1)));
  }

  @Test
  void shouldClearErrorOnSuccessfulReconnection() {
    // Given - initial failed connection
    String errorMessage = "Connection refused";
    when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
        .thenThrow(new DataAccessException(errorMessage) {})
        .thenReturn(1);

    // When - first call fails
    databaseConnectionService.checkDatabaseConnection();
    assertFalse(databaseConnectionService.isConnected());
    assertEquals(errorMessage, databaseConnectionService.getLastError());

    // When - second call succeeds
    databaseConnectionService.checkDatabaseConnection();

    // Then
    assertTrue(databaseConnectionService.isConnected());
    assertNotNull(databaseConnectionService.getLastSuccessfulConnection());
    assertNull(databaseConnectionService.getLastError());
  }

  @Test
  void shouldPerformImmediateCheckSuccessfully() {
    // Given
    when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenReturn(1);

    // When
    boolean result = databaseConnectionService.performImmediateCheck();

    // Then
    assertTrue(result);
    assertTrue(databaseConnectionService.isConnected());
    assertNotNull(databaseConnectionService.getLastSuccessfulConnection());
  }

  @Test
  void shouldPerformImmediateCheckOnFailure() {
    // Given
    when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
        .thenThrow(new DataAccessException("Connection failed") {});

    // When
    boolean result = databaseConnectionService.performImmediateCheck();

    // Then
    assertFalse(result);
    assertFalse(databaseConnectionService.isConnected());
    assertNotNull(databaseConnectionService.getLastError());
  }

  @Test
  void shouldMaintainConnectionHistory() {
    // Given - successful connection
    when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenReturn(1);
    databaseConnectionService.checkDatabaseConnection();

    LocalDateTime firstSuccessful = databaseConnectionService.getLastSuccessfulConnection();
    assertTrue(databaseConnectionService.isConnected());

    // When - connection fails
    when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
        .thenThrow(new DataAccessException("Network error") {});
    databaseConnectionService.checkDatabaseConnection();

    // Then - last successful connection timestamp should be preserved
    assertFalse(databaseConnectionService.isConnected());
    assertEquals(firstSuccessful, databaseConnectionService.getLastSuccessfulConnection());
    assertEquals("Network error", databaseConnectionService.getLastError());
  }

  @Test
  void shouldHandleMultipleConsecutiveFailures() {
    // Given
    when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
        .thenThrow(new DataAccessException("Error 1") {})
        .thenThrow(new DataAccessException("Error 2") {})
        .thenThrow(new DataAccessException("Error 3") {});

    // When - multiple failures
    databaseConnectionService.checkDatabaseConnection();
    assertEquals("Error 1", databaseConnectionService.getLastError());

    databaseConnectionService.checkDatabaseConnection();
    assertEquals("Error 2", databaseConnectionService.getLastError());

    databaseConnectionService.checkDatabaseConnection();
    assertEquals("Error 3", databaseConnectionService.getLastError());

    // Then
    assertFalse(databaseConnectionService.isConnected());
    assertNull(databaseConnectionService.getLastSuccessfulConnection());
  }
}
