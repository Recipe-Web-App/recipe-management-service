package com.recipe_manager.model.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

/**
 * Standardized error response DTO for API error responses.
 *
 * <p>This DTO provides a consistent structure for all error responses including:
 *
 * <ul>
 *   <li>Timestamp of when the error occurred
 *   <li>HTTP status code
 *   <li>Error title and message
 *   <li>Request path that caused the error
 *   <li>Request ID for tracking
 *   <li>Optional validation details
 * </ul>
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

  /** Timestamp of when the error occurred. */
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
  private LocalDateTime timestamp;

  /** HTTP status code. */
  private int status;

  /** Error title. */
  private String error;

  /** Error message. */
  private String message;

  /** Request path that caused the error. */
  private String path;

  /** Request ID for tracking. */
  private String requestId;

  /** Optional validation details. */
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, String> details;

  /**
   * Gets the details map with defensive copying.
   *
   * @return Unmodifiable copy of the details map
   */
  public Map<String, String> getDetails() {
    return details != null ? Collections.unmodifiableMap(details) : null;
  }

  /**
   * Sets the details map with defensive copying.
   *
   * @param details The details map to set
   */
  public void setDetails(final Map<String, String> details) {
    this.details = details != null ? Map.copyOf(details) : null;
  }

  /**
   * Custom builder method for details to implement defensive copying.
   *
   * @return This builder instance
   */
  public static final class ErrorResponseBuilder {
    public ErrorResponseBuilder details(final Map<String, String> details) {
      this.details = details != null ? Map.copyOf(details) : null;
      return this;
    }
  }
}
