package com.recipe_manager.model.dto.external.mediamanager.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Standard error response DTO from the media management service. Provides consistent error
 * information across all endpoints.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class ErrorResponseDto {

  /** Error type identifier (e.g., "Not Found", "Bad Request"). */
  @JsonProperty("error")
  private String error;

  /** Human-readable error description. */
  @JsonProperty("message")
  private String message;

  /** Additional error context, optional. */
  @JsonProperty("details")
  private Map<String, Object> details;
}
