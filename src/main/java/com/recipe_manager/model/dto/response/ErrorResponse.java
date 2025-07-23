package com.recipe_manager.model.dto.response;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

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
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
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
  @Default
  private Map<String, String> details = new HashMap<>();
}
