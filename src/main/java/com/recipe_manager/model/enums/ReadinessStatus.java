package com.recipe_manager.model.enums;

/**
 * Enum representing the readiness status of the media management service. Used in readiness probe
 * endpoints for Kubernetes to determine if the service should receive traffic.
 */
public enum ReadinessStatus {
  /** Service is ready to accept traffic with all dependencies operational. */
  READY,
  /** Service is not ready to accept traffic due to dependency failures. */
  NOT_READY,
  /** A dependency check has timed out during readiness check. */
  TIMEOUT
}
