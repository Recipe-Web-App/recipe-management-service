package com.recipe_manager.model.dto.external.mediamanager.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.recipe_manager.model.enums.ProcessingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Response DTO for retrieving the current status of a media upload, including processing progress,
 * error information, and download URL when complete.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class UploadStatusResponseDto {

  /** Unique identifier for the media file. */
  @JsonProperty("media_id")
  private Long mediaId;

  /** Current processing status of the media. */
  @JsonProperty("status")
  private ProcessingStatus status;

  /** Processing progress percentage (0-100), null if not applicable. */
  @JsonProperty("progress")
  private Integer progress;

  /** Error description if status is FAILED, null otherwise. */
  @JsonProperty("error_message")
  private String errorMessage;

  /** Download URL when processing is complete, null otherwise. */
  @JsonProperty("download_url")
  private String downloadUrl;

  /** Total processing time in milliseconds, null if not completed. */
  @JsonProperty("processing_time_ms")
  private Long processingTimeMs;

  /** ISO 8601 timestamp when file was uploaded, null if not uploaded. */
  @JsonProperty("uploaded_at")
  private LocalDateTime uploadedAt;

  /** ISO 8601 timestamp when processing completed, null if not completed. */
  @JsonProperty("completed_at")
  private LocalDateTime completedAt;
}
