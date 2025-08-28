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
 * Response DTO returned when initiating a presigned upload session. Contains the upload URL, token,
 * and expiration details for secure file uploads.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class InitiateUploadResponseDto {

  /** Unique identifier for the media placeholder created for this upload. */
  @JsonProperty("media_id")
  private Long mediaId;

  /** Presigned URL for file upload with embedded security parameters. */
  @JsonProperty("upload_url")
  private String uploadUrl;

  /** Unique token identifying the upload session. */
  @JsonProperty("upload_token")
  private String uploadToken;

  /** ISO 8601 timestamp when the upload URL expires. */
  @JsonProperty("expires_at")
  private LocalDateTime expiresAt;

  /** Initial status of the upload session (typically INITIATED). */
  @JsonProperty("status")
  private ProcessingStatus status;
}
