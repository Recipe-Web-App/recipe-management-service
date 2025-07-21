package com.recipe_manager.model.dto.recipe;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.recipe_manager.model.dto.media.RecipeTagMediaDto;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object for RecipeTag entity. Used for transferring recipe tag data between layers.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
public class RecipeTagDto {

  /** The recipe ID. */
  private Long recipeId;

  /** The tag ID. */
  private Long tagId;

  /** The tag name. */
  private String name;

  /** The creation timestamp. */
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  private LocalDateTime updatedAt;

  /** The list of media associated with this tag. */
  @Valid private List<RecipeTagMediaDto> media;

  /** Builder for RecipeTagDto. Use to construct instances with clarity and safety. */
  public static final class RecipeTagDtoBuilder {
    /**
     * Sets the media list.
     *
     * @param media the list of media
     * @return this builder
     */
    public RecipeTagDtoBuilder media(final List<RecipeTagMediaDto> media) {
      this.media = media == null ? new ArrayList<>() : new ArrayList<>(media);
      return this;
    }
  }

  /**
   * Returns an unmodifiable list of media.
   *
   * @return the media list
   */
  public List<RecipeTagMediaDto> getMedia() {
    return Collections.unmodifiableList(media);
  }

  /**
   * Sets the media list.
   *
   * @param media the list of media
   */
  public void setMedia(final List<RecipeTagMediaDto> media) {
    this.media = media != null ? new ArrayList<>(media) : new ArrayList<>();
  }

  /**
   * All-args constructor with defensive copying for mutable fields.
   *
   * <p>WARNING: This constructor has many parameters. Prefer using the builder for clarity.
   *
   * @param recipeId the recipe ID
   * @param tagId the tag ID
   * @param name the tag name
   * @param createdAt the creation timestamp
   * @param updatedAt the update timestamp
   * @param media the media list
   */
  public RecipeTagDto(
      final Long recipeId,
      final Long tagId,
      final String name,
      final LocalDateTime createdAt,
      final LocalDateTime updatedAt,
      final List<RecipeTagMediaDto> media) {
    this.recipeId = recipeId;
    this.tagId = tagId;
    this.name = name;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.media = media != null ? new ArrayList<>(media) : new ArrayList<>();
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
    final RecipeTagDto that = (RecipeTagDto) obj;
    return Objects.equals(recipeId, that.recipeId)
        && Objects.equals(tagId, that.tagId)
        && Objects.equals(name, that.name)
        && Objects.equals(createdAt, that.createdAt)
        && Objects.equals(updatedAt, that.updatedAt)
        && Objects.equals(media, that.media);
  }

  /**
   * Computes hash code based on all fields.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(recipeId, tagId, name, createdAt, updatedAt, media);
  }
}
