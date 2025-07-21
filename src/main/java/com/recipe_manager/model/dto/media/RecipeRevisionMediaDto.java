package com.recipe_manager.model.dto.media;

import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Data Transfer Object for RecipeRevision media. Used for transferring recipe revision media data
 * between layers.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public final class RecipeRevisionMediaDto extends MediaDto {

  /** The recipe ID. */
  private Long recipeId;

  /** The revision number. */
  private Integer revisionNumber;

  /**
   * All-args constructor for RecipeRevisionMediaDto.
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
   * @param recipeId the recipe ID
   * @param revisionNumber the revision number
   */
  // CHECKSTYLE:OFF: ParameterNumber
  public RecipeRevisionMediaDto(
      final Long mediaId,
      final String url,
      final String altText,
      final String contentType,
      final Long fileSize,
      final LocalDateTime createdAt,
      final LocalDateTime updatedAt,
      final Long recipeId,
      final Integer revisionNumber) {
    super(mediaId, url, altText, contentType, fileSize, createdAt, updatedAt);
    this.recipeId = recipeId;
    this.revisionNumber = revisionNumber;
  }

  // CHECKSTYLE:ON: ParameterNumber

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
    if (!super.equals(obj)) {
      return false;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final RecipeRevisionMediaDto that = (RecipeRevisionMediaDto) obj;
    return Objects.equals(recipeId, that.recipeId)
        && Objects.equals(revisionNumber, that.revisionNumber);
  }

  /**
   * Computes hash code based on all fields.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), recipeId, revisionNumber);
  }
}
