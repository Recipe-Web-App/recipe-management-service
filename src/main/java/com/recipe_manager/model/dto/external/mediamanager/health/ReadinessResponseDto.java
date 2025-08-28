package com.recipe_manager.model.dto.external.mediamanager.health;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.recipe_manager.model.enums.ReadinessStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Readiness check response DTO from the media management service. Provides binary ready/not-ready
 * status for Kubernetes traffic routing decisions.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class ReadinessResponseDto {

  /** Binary readiness status (READY, NOT_READY). */
  @JsonProperty("status")
  private ReadinessStatus status;

  /** ISO 8601 timestamp of the readiness check. */
  @JsonProperty("timestamp")
  private LocalDateTime timestamp;

  /** Service name identifier. */
  @JsonProperty("service")
  private String service;

  /** Service version. */
  @JsonProperty("version")
  private String version;

  /** Total time taken for readiness check in milliseconds. */
  @JsonProperty("response_time_ms")
  private Integer responseTimeMs;

  /** Individual dependency readiness checks. */
  @JsonProperty("checks")
  private ReadinessChecksDto checks;

  /** Inner class for readiness check details. */
  @Data
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @EqualsAndHashCode
  @ToString
  public static final class ReadinessChecksDto {

    /** Database readiness check status. */
    @JsonProperty("database")
    private ReadinessDependencyCheckDto database;

    /** Storage readiness check status. */
    @JsonProperty("storage")
    private ReadinessDependencyCheckDto storage;

    /** Overall readiness assessment - ready only if ALL dependencies ready. */
    @JsonProperty("overall")
    private ReadinessStatus overall;
  }
}
