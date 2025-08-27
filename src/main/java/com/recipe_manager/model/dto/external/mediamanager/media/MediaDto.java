package com.recipe_manager.model.dto.external.mediamanager.media;

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
 * Main media file representation DTO containing metadata about a media file stored in the media
 * management service.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class MediaDto {

  /** Unique identifier for the media file. */
  @JsonProperty("id")
  private Long id;

  /** SHA-256 hash of the file content for integrity and deduplication. */
  @JsonProperty("content_hash")
  private String contentHash;

  /** Original filename as uploaded by the user. */
  @JsonProperty("original_filename")
  private String originalFilename;

  /** Media type information with format-specific metadata. */
  @JsonProperty("media_type")
  private MediaTypeDto mediaType;

  /** File size in bytes. */
  @JsonProperty("file_size")
  private Long fileSize;

  /** Current processing status of the media. */
  @JsonProperty("processing_status")
  private ProcessingStatus processingStatus;

  /** ISO 8601 timestamp when the file was uploaded. */
  @JsonProperty("uploaded_at")
  private LocalDateTime uploadedAt;

  /** ISO 8601 timestamp when the file was last updated. */
  @JsonProperty("updated_at")
  private LocalDateTime updatedAt;
}
