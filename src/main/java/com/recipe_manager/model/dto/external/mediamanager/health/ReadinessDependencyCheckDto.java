package com.recipe_manager.model.dto.external.mediamanager.health;

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
 * Individual dependency readiness check status DTO. Reports the readiness status and response time
 * for a specific service dependency.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class ReadinessDependencyCheckDto {

  /** Readiness status of individual dependency check (READY, NOT_READY, TIMEOUT). */
  @JsonProperty("status")
  private ReadinessStatus status;

  /** Time taken for dependency check in milliseconds. */
  @JsonProperty("response_time_ms")
  private Integer responseTimeMs;
}
