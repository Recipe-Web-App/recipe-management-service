package com.recipe_manager.model.dto.media;

import java.time.LocalDateTime;
import java.util.UUID;

import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object for Media entity. Contains full media information retrieved from the
 * database for get operations.
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
  /** The unique ID of the media. */
  private Long mediaId;

  /** The user ID of the media owner. */
  @NotNull private UUID userId;

  /** The media type (MIME type). */
  @NotNull private MediaType mediaType;

  /** The original filename when uploaded. */
  private String originalFilename;

  /** The file size in bytes. */
  private Long fileSize;

  /** The content hash for integrity checking. */
  private String contentHash;

  /** The processing status of the media. */
  @NotNull private ProcessingStatus processingStatus;

  /** The creation timestamp. */
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  private LocalDateTime updatedAt;
}
