package com.recipe_manager.model.enums;

/**
 * Enum representing the health status of the media management service. Used in health check
 * endpoints to indicate the overall service and dependency status.
 */
public enum HealthStatus {
  /** Service and all dependencies are fully operational. */
  HEALTHY,
  /** Service is partially operational with some dependencies failing. */
  DEGRADED,
  /** Service is non-operational with critical dependencies failing. */
  UNHEALTHY,
  /** A dependency check has timed out. */
  TIMEOUT
}
