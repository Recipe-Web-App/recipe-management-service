package com.recipe_manager.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Generic response wrapper for media operations. Provides consistent response structure with
 * success status, message, and optional data payload.
 *
 * @param <T> the type of the data payload
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class MediaResponse<T> {
  /** Whether the operation was successful. */
  private boolean success;

  /** Human-readable message about the operation result. */
  private String message;

  /** Optional data payload returned by the operation. */
  private T data;
}
