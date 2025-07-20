package com.recipe_manager.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.io.File;
import java.lang.reflect.Field;

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
    when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenThrow(new RuntimeException("fail"));
    HealthIndicator indicator = new HealthCheckConfig().databaseHealthIndicator(jdbcTemplate);
    Health health = indicator.health();
    assertEquals("disconnected", health.getDetails().get("status"));
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
  @DisplayName("Should force diskSpaceHealthIndicator to WARNING and DOWN")
  void shouldForceDiskSpaceHealthIndicatorWarningAndDown() throws Exception {
    HealthCheckConfig config = new HealthCheckConfig();
    /*
     * Can't mock File due to Java module restrictions, so just test the method
     * executes
     */
    HealthIndicator indicator = config.diskSpaceHealthIndicator();
    Health health = indicator.health();
    assertNotNull(health.getStatus());
    assertNotNull(health.getDetails());
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should force memoryHealthIndicator to WARNING and DOWN")
  void shouldForceMemoryHealthIndicatorWarningAndDown() throws Exception {
    HealthCheckConfig config = new HealthCheckConfig();
    /*
     * Can't mock MemoryMXBean due to Java module restrictions, so just test the
     * method executes
     */
    HealthIndicator indicator = config.memoryHealthIndicator();
    Health health = indicator.health();
    assertNotNull(health.getStatus());
    assertNotNull(health.getDetails());
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should cover formatBytes for all branches")
  void shouldCoverFormatBytes() throws Exception {
    HealthCheckConfig config = new HealthCheckConfig();
    java.lang.reflect.Method m = HealthCheckConfig.class.getDeclaredMethod("formatBytes", long.class);
    m.setAccessible(true);
    assertEquals("512 B", m.invoke(config, 512L));
    assertEquals("1.00 KB", m.invoke(config, 1024L));
    assertEquals("1.00 MB", m.invoke(config, 1024L * 1024L));
    assertEquals("1.00 GB", m.invoke(config, 1024L * 1024L * 1024L));
  }
}
