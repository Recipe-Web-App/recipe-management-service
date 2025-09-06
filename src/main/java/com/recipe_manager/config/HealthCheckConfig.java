package com.recipe_manager.config;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for custom health checks and monitoring.
 *
 * <p>This configuration provides:
 *
 * <ul>
 *   <li>Database connectivity health check
 *   <li>Disk space monitoring
 *   <li>Memory usage monitoring
 *   <li>Application-specific health indicators
 * </ul>
 */
@Configuration
public class HealthCheckConfig {

  /** Health threshold constant for 100. */
  private static final int THRESHOLD_100 = 100;

  /** Health threshold constant for 90. */
  private static final int THRESHOLD_90 = 90;

  /** Health threshold constant for 80. */
  private static final int THRESHOLD_80 = 80;

  /** Byte constant for 1024. */
  private static final int BYTES_1024 = 1024;

  /** Byte constant for 1024.0. */
  private static final double BYTES_1024_DOUBLE = 1024.0;

  /**
   * Creates a database health indicator that checks database connectivity with enhanced
   * information.
   *
   * @param databaseConnectionService The service managing database connection retry logic
   * @return HealthIndicator for database connectivity
   */
  @Bean
  public HealthIndicator databaseHealthIndicator(
      final com.recipe_manager.service.DatabaseConnectionService databaseConnectionService) {
    return () -> {
      boolean isConnected = databaseConnectionService.isConnected();
      java.time.LocalDateTime lastSuccessful =
          databaseConnectionService.getLastSuccessfulConnection();
      java.time.LocalDateTime lastAttempt = databaseConnectionService.getLastConnectionAttempt();
      String lastError = databaseConnectionService.getLastError();

      Health.Builder healthBuilder;

      if (isConnected) {
        healthBuilder = Health.up();
      } else {
        // Use DEGRADED status instead of DOWN to indicate the service can still
        // function
        healthBuilder = Health.status("DEGRADED");
      }

      healthBuilder
          .withDetail("database", "PostgreSQL")
          .withDetail("status", isConnected ? "connected" : "disconnected")
          .withDetail("connectionRetryEnabled", true);

      if (lastSuccessful != null) {
        healthBuilder.withDetail("lastSuccessfulConnection", lastSuccessful.toString());
      }

      if (lastAttempt != null) {
        healthBuilder.withDetail("lastConnectionAttempt", lastAttempt.toString());
      }

      if (lastError != null) {
        healthBuilder.withDetail("lastError", lastError);
      }

      if (!isConnected) {
        healthBuilder.withDetail(
            "message", "Database unavailable - service degraded but operational");
      }

      return healthBuilder.build();
    };
  }

  /**
   * Creates a disk space health indicator that monitors available disk space.
   *
   * @return HealthIndicator for disk space monitoring
   */
  @Bean
  public HealthIndicator diskSpaceHealthIndicator() {
    return () -> {
      // Use current working directory instead of hardcoded absolute path
      File root = new File(System.getProperty("user.dir"));
      long totalSpace = root.getTotalSpace();
      long freeSpace = root.getFreeSpace();
      long usedSpace = totalSpace - freeSpace;
      double usagePercentage = (double) usedSpace / totalSpace * THRESHOLD_100;

      if (usagePercentage > THRESHOLD_90) {
        return Health.down()
            .withDetail("disk.usage.percentage", String.format("%.2f%%", usagePercentage))
            .withDetail("disk.free.space", formatBytes(freeSpace))
            .withDetail("disk.total.space", formatBytes(totalSpace))
            .withDetail("message", "Disk usage is above 90%")
            .build();
      } else if (usagePercentage > THRESHOLD_80) {
        return Health.status("WARNING")
            .withDetail("disk.usage.percentage", String.format("%.2f%%", usagePercentage))
            .withDetail("disk.free.space", formatBytes(freeSpace))
            .withDetail("disk.total.space", formatBytes(totalSpace))
            .withDetail("message", "Disk usage is above 80%")
            .build();
      } else {
        return Health.up()
            .withDetail("disk.usage.percentage", String.format("%.2f%%", usagePercentage))
            .withDetail("disk.free.space", formatBytes(freeSpace))
            .withDetail("disk.total.space", formatBytes(totalSpace))
            .build();
      }
    };
  }

  /**
   * Creates a memory health indicator that monitors JVM memory usage.
   *
   * @return HealthIndicator for memory monitoring
   */
  @Bean
  public HealthIndicator memoryHealthIndicator() {
    return () -> {
      MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
      long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
      long heapMax = memoryBean.getHeapMemoryUsage().getMax();
      double heapUsagePercentage = (double) heapUsed / heapMax * THRESHOLD_100;

      if (heapUsagePercentage > THRESHOLD_90) {
        return Health.down()
            .withDetail(
                "memory.heap.usage.percentage", String.format("%.2f%%", heapUsagePercentage))
            .withDetail("memory.heap.used", formatBytes(heapUsed))
            .withDetail("memory.heap.max", formatBytes(heapMax))
            .withDetail("message", "Heap memory usage is above 90%")
            .build();
      } else if (heapUsagePercentage > THRESHOLD_80) {
        return Health.status("WARNING")
            .withDetail(
                "memory.heap.usage.percentage", String.format("%.2f%%", heapUsagePercentage))
            .withDetail("memory.heap.used", formatBytes(heapUsed))
            .withDetail("memory.heap.max", formatBytes(heapMax))
            .withDetail("message", "Heap memory usage is above 80%")
            .build();
      } else {
        return Health.up()
            .withDetail(
                "memory.heap.usage.percentage", String.format("%.2f%%", heapUsagePercentage))
            .withDetail("memory.heap.used", formatBytes(heapUsed))
            .withDetail("memory.heap.max", formatBytes(heapMax))
            .build();
      }
    };
  }

  /**
   * Creates an application health indicator for application-specific checks.
   *
   * @return HealthIndicator for application health
   */
  @Bean
  public HealthIndicator applicationHealthIndicator() {
    return () -> {
      // Add any application-specific health checks here
      // For example, check if required services are available

      return Health.up()
          .withDetail("application", "Recipe Management Service")
          .withDetail("version", "0.1.0")
          .withDetail("status", "running")
          .build();
    };
  }

  /**
   * Formats bytes into a human-readable string.
   *
   * @param bytes The number of bytes
   * @return Formatted string representation
   */
  private String formatBytes(final long bytes) {
    if (bytes < BYTES_1024) {
      return bytes + " B";
    } else if (bytes < BYTES_1024 * BYTES_1024) {
      return String.format("%.2f KB", bytes / BYTES_1024_DOUBLE);
    } else if (bytes < BYTES_1024 * BYTES_1024 * BYTES_1024) {
      return String.format("%.2f MB", bytes / (BYTES_1024_DOUBLE * BYTES_1024_DOUBLE));
    } else {
      return String.format(
          "%.2f GB", bytes / (BYTES_1024_DOUBLE * BYTES_1024_DOUBLE * BYTES_1024_DOUBLE));
    }
  }
}
