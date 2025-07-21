package com.recipe_manager.model.dto.media;

import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Data Transfer Object for RecipeIngredient media. Used for transferring recipe ingredient media
 * data between layers.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public final class RecipeIngredientMediaDto extends MediaDto {

  /** The recipe ID. */
  private Long recipeId;

  /** The ingredient ID. */
  private Long ingredientId;

  /**
   * All-args constructor for RecipeIngredientMediaDto.
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
   * @param ingredientId the ingredient ID
   */
  // CHECKSTYLE:OFF: ParameterNumber
  public RecipeIngredientMediaDto(
      final Long mediaId,
      final String url,
      final String altText,
      final String contentType,
      final Long fileSize,
      final LocalDateTime createdAt,
      final LocalDateTime updatedAt,
      final Long recipeId,
      final Long ingredientId) {
    super(mediaId, url, altText, contentType, fileSize, createdAt, updatedAt);
    this.recipeId = recipeId;
    this.ingredientId = ingredientId;
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
    final RecipeIngredientMediaDto that = (RecipeIngredientMediaDto) obj;
    return Objects.equals(recipeId, that.recipeId)
        && Objects.equals(ingredientId, that.ingredientId);
  }

  /**
   * Computes hash code based on all fields.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), recipeId, ingredientId);
  }
}
