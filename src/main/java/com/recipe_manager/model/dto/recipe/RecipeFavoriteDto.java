package com.recipe_manager.model.dto.recipe;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.recipe_manager.model.dto.media.RecipeFavoriteMediaDto;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object for RecipeFavorite entity. Used for transferring recipe favorite data
 * between layers.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
public class RecipeFavoriteDto {

  /** The recipe ID. */
  private Long recipeId;

  /** The user ID. */
  private UUID userId;

  /** The timestamp when the recipe was favorited. */
  private LocalDateTime favoritedAt;

  /** The creation timestamp. */
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  private LocalDateTime updatedAt;

  /** The list of media associated with this favorite. */
  @Valid private List<RecipeFavoriteMediaDto> media;

  /** Builder for RecipeFavoriteDto. Use to construct instances with clarity and safety. */
  public static final class RecipeFavoriteDtoBuilder {
    /**
     * Sets the media list.
     *
     * @param media the list of media
     * @return this builder
     */
    public RecipeFavoriteDtoBuilder media(final List<RecipeFavoriteMediaDto> media) {
      this.media = media == null ? new ArrayList<>() : new ArrayList<>(media);
      return this;
    }
  }

  /**
   * Returns an unmodifiable list of media.
   *
   * @return the media list
   */
  public List<RecipeFavoriteMediaDto> getMedia() {
    return Collections.unmodifiableList(media);
  }

  /**
   * Sets the media list.
   *
   * @param media the list of media
   */
  public void setMedia(final List<RecipeFavoriteMediaDto> media) {
    this.media = media != null ? new ArrayList<>(media) : new ArrayList<>();
  }

  /**
   * All-args constructor with defensive copying for mutable fields.
   *
   * <p>WARNING: This constructor has many parameters. Prefer using the builder for clarity.
   *
   * @param recipeId the recipe ID
   * @param userId the user ID
   * @param favoritedAt the favorited timestamp
   * @param createdAt the creation timestamp
   * @param updatedAt the update timestamp
   * @param media the media list
   */
  public RecipeFavoriteDto(
      final Long recipeId,
      final UUID userId,
      final LocalDateTime favoritedAt,
      final LocalDateTime createdAt,
      final LocalDateTime updatedAt,
      final List<RecipeFavoriteMediaDto> media) {
    this.recipeId = recipeId;
    this.userId = userId;
    this.favoritedAt = favoritedAt;
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
    final RecipeFavoriteDto that = (RecipeFavoriteDto) obj;
    return Objects.equals(recipeId, that.recipeId)
        && Objects.equals(userId, that.userId)
        && Objects.equals(favoritedAt, that.favoritedAt)
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
    return Objects.hash(recipeId, userId, favoritedAt, createdAt, updatedAt, media);
  }
}
