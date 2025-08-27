package com.recipe_manager.model.dto.external.mediamanager.health;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.recipe_manager.model.enums.HealthStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Health check response DTO from the media management service. Provides comprehensive information
 * about service health and dependency status.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class HealthResponseDto {

  /** Overall health status (HEALTHY, DEGRADED, UNHEALTHY). */
  @JsonProperty("status")
  private HealthStatus status;

  /** ISO 8601 timestamp of the health check. */
  @JsonProperty("timestamp")
  private LocalDateTime timestamp;

  /** Service name identifier. */
  @JsonProperty("service")
  private String service;

  /** Service version. */
  @JsonProperty("version")
  private String version;

  /** Total time taken for health check in milliseconds. */
  @JsonProperty("response_time_ms")
  private Integer responseTimeMs;

  /** Individual dependency health checks. */
  @JsonProperty("checks")
  private HealthChecksDto checks;

  /** Inner class for health check details. */
  @Data
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @EqualsAndHashCode
  @ToString
  public static final class HealthChecksDto {

    /** Database health check status. */
    @JsonProperty("database")
    private DependencyCheckDto database;

    /** Storage health check status. */
    @JsonProperty("storage")
    private DependencyCheckDto storage;

    /** Overall assessment of all dependency checks. */
    @JsonProperty("overall")
    private HealthStatus overall;
  }
}
