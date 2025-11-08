package com.recipe_manager.model.dto.external.notificationservice.response;

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
 * Standard error response DTO from the notification service. Provides consistent error information
 * across all endpoints, including validation errors and detailed error context.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class NotificationErrorResponseDto {

  /**
   * Error code identifier (e.g., "bad_request", "unauthorized", "not_found"). Used for programmatic
   * error handling.
   */
  @JsonProperty("error")
  private String error;

  /** Human-readable error description. Main error message for display. */
  @JsonProperty("message")
  private String message;

  /**
   * Additional error context or details, optional. Provides more specific information about what
   * went wrong.
   */
  @JsonProperty("detail")
  private String detail;

  /**
   * Field-specific validation errors, optional. Maps field names to their validation error
   * messages. Typically populated for 400 Bad Request responses with validation failures.
   */
  @JsonProperty("errors")
  private Map<String, Object> errors;
}
