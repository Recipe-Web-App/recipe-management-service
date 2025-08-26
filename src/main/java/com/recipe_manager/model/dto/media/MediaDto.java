package com.recipe_manager.model.dto.media;

import java.time.LocalDateTime;
import java.util.UUID;

import com.recipe_manager.model.enums.MediaType;
import com.recipe_manager.model.enums.ProcessingStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Data Transfer Object for Media entity. Used for transferring media data between layers.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@ToString
public class MediaDto {
  /** The media ID. */
  private Long mediaId;

  /** The user ID of the media owner. */
  private UUID userId;

  /** The type of media content. */
  private MediaType mediaType;

  /** The file path or URL of the media. */
  private String mediaPath;

  /** The file size in bytes. */
  private Long fileSize;

  /** The content hash for integrity verification. */
  private String contentHash;

  /** The original filename when uploaded. */
  private String originalFilename;

  /** The processing status of the media. */
  private ProcessingStatus processingStatus;

  /** The creation timestamp. */
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  private LocalDateTime updatedAt;
}
