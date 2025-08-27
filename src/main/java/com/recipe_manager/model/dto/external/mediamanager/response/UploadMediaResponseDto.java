package com.recipe_manager.model.dto.external.mediamanager.response;

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
 * Response DTO returned after successfully uploading media to the media management service.
 * Contains the media ID, content hash, processing status, and optional upload URL.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class UploadMediaResponseDto {

  /** Unique identifier for the uploaded media file. */
  @JsonProperty("media_id")
  private Long mediaId;

  /** SHA-256 hash of the uploaded file content for integrity verification. */
  @JsonProperty("content_hash")
  private String contentHash;

  /** Current processing status of the uploaded media. */
  @JsonProperty("processing_status")
  private ProcessingStatus processingStatus;

  /** Optional direct URL to access the uploaded file. */
  @JsonProperty("upload_url")
  private String uploadUrl;
}
