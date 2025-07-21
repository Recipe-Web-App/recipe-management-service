package com.recipe_manager.model.dto.media;

import java.time.LocalDateTime;
import java.util.Objects;

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
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class MediaDto {

  /** The media ID. */
  private Long mediaId;

  /** The media URL. */
  private String url;

  /** The alt text for the media. */
  private String altText;

  /** The content type of the media. */
  private String contentType;

  /** The file size in bytes. */
  private Long fileSize;

  /** The creation timestamp. */
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  private LocalDateTime updatedAt;

  /**
   * All-args constructor for MediaDto.
   *
   * <p>WARNING: This constructor has many parameters. Prefer using the builder for clarity.
   *
   * @param mediaId the media ID
   * @param url the media URL
   * @param altText the alt text
   * @param contentType the content type
   * @param fileSize the file size
   * @param createdAt the creation timestamp
   * @param updatedAt the update timestamp
   */
  public MediaDto(
      final Long mediaId,
      final String url,
      final String altText,
      final String contentType,
      final Long fileSize,
      final LocalDateTime createdAt,
      final LocalDateTime updatedAt) {
    this.mediaId = mediaId;
    this.url = url;
    this.altText = altText;
    this.contentType = contentType;
    this.fileSize = fileSize;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  /**
   * Checks equality based on all fields.
   *
   * @param obj the object to compare
   * @return true if equal, false otherwise
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final MediaDto that = (MediaDto) obj;
    return Objects.equals(mediaId, that.mediaId)
        && Objects.equals(url, that.url)
        && Objects.equals(altText, that.altText)
        && Objects.equals(contentType, that.contentType)
        && Objects.equals(fileSize, that.fileSize)
        && Objects.equals(createdAt, that.createdAt)
        && Objects.equals(updatedAt, that.updatedAt);
  }

  /**
   * Computes hash code based on all fields.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(mediaId, url, altText, contentType, fileSize, createdAt, updatedAt);
  }
}
