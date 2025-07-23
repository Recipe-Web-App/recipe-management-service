package com.recipe_manager.model.dto.media;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Base Data Transfer Object for media entities. Used for transferring media data between layers.
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
abstract class MediaDto {
  /** The media ID. */
  protected Long mediaId;

  /** The media URL. */
  protected String url;

  /** The alt text for the media. */
  protected String altText;

  /** The content type of the media. */
  protected String contentType;

  /** The file size in bytes. */
  protected Long fileSize;

  /** The creation timestamp. */
  protected LocalDateTime createdAt;

  /** The last update timestamp. */
  protected LocalDateTime updatedAt;
}
