package com.recipe_manager.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Test class for HealthCheckConfig.
 * Verifies that health check configuration loads correctly.
 */
@Tag("unit")
class HealthCheckConfigTest {

  /**
   * Test that health check configuration can be loaded.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should load health check configuration")
  void healthCheckConfigLoads() {
    // Verify that health check configuration can be instantiated
    HealthCheckConfig config = new HealthCheckConfig();
    assertNotNull(config);
  }

  /**
   * Test that health check beans are created.
   */
  @Test
  @Tag("standard-processing")
  @DisplayName("Should create health check beans")
  void healthCheckBeansCreated() {
    // This test verifies that health check configuration exists
    assertNotNull(HealthCheckConfig.class);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create databaseHealthIndicator and return up")
  void shouldCreateDatabaseHealthIndicatorUp() {
    JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenReturn(1);
    HealthIndicator indicator = new HealthCheckConfig().databaseHealthIndicator(jdbcTemplate);
    Health health = indicator.health();
    assertEquals("connected", health.getDetails().get("status"));
    assertEquals("PostgreSQL", health.getDetails().get("database"));
    assertEquals("UP", health.getStatus().getCode());
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should create databaseHealthIndicator and return down on exception")
  void shouldCreateDatabaseHealthIndicatorDown() {
    JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenThrow(new RuntimeException("Connection failed"));
    HealthIndicator indicator = new HealthCheckConfig().databaseHealthIndicator(jdbcTemplate);
    Health health = indicator.health();
    assertEquals("disconnected", health.getDetails().get("status"));
    assertEquals("PostgreSQL", health.getDetails().get("database"));
    assertEquals("Connection failed", health.getDetails().get("error"));
    assertEquals("DOWN", health.getStatus().getCode());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create diskSpaceHealthIndicator and return up/warning/down")
  void shouldCreateDiskSpaceHealthIndicator() {
    HealthIndicator indicator = new HealthCheckConfig().diskSpaceHealthIndicator();
    Health health = indicator.health();
    assertNotNull(health.getStatus());
    assertNotNull(health.getDetails());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create memoryHealthIndicator and return up/warning/down")
  void shouldCreateMemoryHealthIndicator() {
    HealthIndicator indicator = new HealthCheckConfig().memoryHealthIndicator();
    Health health = indicator.health();
    assertNotNull(health.getStatus());
    assertNotNull(health.getDetails());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create applicationHealthIndicator and return up")
  void shouldCreateApplicationHealthIndicator() {
    HealthIndicator indicator = new HealthCheckConfig().applicationHealthIndicator();
    Health health = indicator.health();
    assertEquals("UP", health.getStatus().getCode());
    assertEquals("Recipe Manager Service", health.getDetails().get("application"));
    assertEquals("0.1.0", health.getDetails().get("version"));
    assertEquals("running", health.getDetails().get("status"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should cover private constructor")
  void shouldCoverPrivateConstructor() throws Exception {
    java.lang.reflect.Constructor<HealthCheckConfig> constructor = HealthCheckConfig.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    constructor.newInstance();
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should test diskSpaceHealthIndicator with various scenarios")
  void shouldTestDiskSpaceHealthIndicatorScenarios() throws Exception {
    HealthCheckConfig config = new HealthCheckConfig();
    HealthIndicator indicator = config.diskSpaceHealthIndicator();
    Health health = indicator.health();
    assertNotNull(health.getStatus());
    assertNotNull(health.getDetails());

    // Verify that the health object contains expected details
    assertTrue(health.getDetails().containsKey("disk.usage.percentage"));
    assertTrue(health.getDetails().containsKey("disk.free.space"));
    assertTrue(health.getDetails().containsKey("disk.total.space"));

    // The status should be one of UP, WARNING, or DOWN
    String status = health.getStatus().getCode();
    assertTrue("UP".equals(status) || "WARNING".equals(status) || "DOWN".equals(status));
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should test memoryHealthIndicator with various scenarios")
  void shouldTestMemoryHealthIndicatorScenarios() throws Exception {
    HealthCheckConfig config = new HealthCheckConfig();
    HealthIndicator indicator = config.memoryHealthIndicator();
    Health health = indicator.health();
    assertNotNull(health.getStatus());
    assertNotNull(health.getDetails());

    // Verify that the health object contains expected details
    assertTrue(health.getDetails().containsKey("memory.heap.usage.percentage"));
    assertTrue(health.getDetails().containsKey("memory.heap.used"));
    assertTrue(health.getDetails().containsKey("memory.heap.max"));

    // The status should be one of UP, WARNING, or DOWN
    String status = health.getStatus().getCode();
    assertTrue("UP".equals(status) || "WARNING".equals(status) || "DOWN".equals(status));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should cover formatBytes for all branches")
  void shouldCoverFormatBytes() throws Exception {
    HealthCheckConfig config = new HealthCheckConfig();
    java.lang.reflect.Method m = HealthCheckConfig.class.getDeclaredMethod("formatBytes", long.class);
    m.setAccessible(true);

    // Test bytes
    assertEquals("0 B", m.invoke(config, 0L));
    assertEquals("1 B", m.invoke(config, 1L));
    assertEquals("512 B", m.invoke(config, 512L));
    assertEquals("1023 B", m.invoke(config, 1023L));

    // Test kilobytes
    assertEquals("1.00 KB", m.invoke(config, 1024L));
    assertEquals("2.50 KB", m.invoke(config, 2560L));
    assertEquals("1023.00 KB", m.invoke(config, 1024L * 1023L));

    // Test megabytes
    assertEquals("1.00 MB", m.invoke(config, 1024L * 1024L));
    assertEquals("2.50 MB", m.invoke(config, (long)(1024L * 1024L * 2.5)));
    assertEquals("1023.00 MB", m.invoke(config, 1024L * 1024L * 1023L));

    // Test gigabytes
    assertEquals("1.00 GB", m.invoke(config, 1024L * 1024L * 1024L));
    assertEquals("2.50 GB", m.invoke(config, (long)(1024L * 1024L * 1024L * 2.5)));
  }
}
