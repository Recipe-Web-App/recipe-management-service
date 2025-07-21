package com.recipe_manager.model.dto.recipe;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.recipe_manager.model.dto.media.RecipeRevisionMediaDto;
import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object for RecipeRevision entity. Used for transferring recipe revision data
 * between layers.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
public final class RecipeRevisionDto {

  /** The revision ID. */
  private Long revisionId;

  /** The recipe ID. */
  private Long recipeId;

  /** The user ID. */
  private UUID userId;

  /** The revision category. */
  private RevisionCategory revisionCategory;

  /** The revision type. */
  private RevisionType revisionType;

  /** The previous data. */
  private String previousData;

  /** The new data. */
  private String newData;

  /** The change comment. */
  private String changeComment;

  /** The creation timestamp. */
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  private LocalDateTime updatedAt;

  /** The list of media associated with this revision. */
  @Valid private List<RecipeRevisionMediaDto> media;

  /** Builder for RecipeRevisionDto. Use to construct instances with clarity and safety. */
  public static final class RecipeRevisionDtoBuilder {
    /**
     * Sets the media list.
     *
     * @param media the list of media
     * @return this builder
     */
    public RecipeRevisionDtoBuilder media(final List<RecipeRevisionMediaDto> media) {
      this.media = media == null ? new ArrayList<>() : new ArrayList<>(media);
      return this;
    }
  }

  /**
   * Returns an unmodifiable list of media.
   *
   * @return the media list
   */
  public List<RecipeRevisionMediaDto> getMedia() {
    return Collections.unmodifiableList(media);
  }

  /**
   * Sets the media list.
   *
   * @param media the list of media
   */
  public void setMedia(final List<RecipeRevisionMediaDto> media) {
    this.media = media != null ? new ArrayList<>(media) : new ArrayList<>();
  }

  /**
   * All-args constructor with defensive copying for mutable fields.
   *
   * <p>WARNING: This constructor has many parameters. Prefer using the builder for clarity.
   *
   * @param revisionId the revision ID
   * @param recipeId the recipe ID
   * @param userId the user ID
   * @param revisionCategory the revision category
   * @param revisionType the revision type
   * @param previousData the previous data
   * @param newData the new data
   * @param changeComment the change comment
   * @param createdAt the creation timestamp
   * @param updatedAt the update timestamp
   * @param media the media list
   */
  // CHECKSTYLE:OFF: ParameterNumber
  public RecipeRevisionDto(
      final Long revisionId,
      final Long recipeId,
      final UUID userId,
      final RevisionCategory revisionCategory,
      final RevisionType revisionType,
      final String previousData,
      final String newData,
      final String changeComment,
      final LocalDateTime createdAt,
      final LocalDateTime updatedAt,
      final List<RecipeRevisionMediaDto> media) {
    this.revisionId = revisionId;
    this.recipeId = recipeId;
    this.userId = userId;
    this.revisionCategory = revisionCategory;
    this.revisionType = revisionType;
    this.previousData = previousData;
    this.newData = newData;
    this.changeComment = changeComment;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.media = media != null ? new ArrayList<>(media) : new ArrayList<>();
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
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final RecipeRevisionDto that = (RecipeRevisionDto) obj;
    return Objects.equals(revisionId, that.revisionId)
        && Objects.equals(recipeId, that.recipeId)
        && Objects.equals(userId, that.userId)
        && revisionCategory == that.revisionCategory
        && revisionType == that.revisionType
        && Objects.equals(previousData, that.previousData)
        && Objects.equals(newData, that.newData)
        && Objects.equals(changeComment, that.changeComment)
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
    return Objects.hash(
        revisionId,
        recipeId,
        userId,
        revisionCategory,
        revisionType,
        previousData,
        newData,
        changeComment,
        createdAt,
        updatedAt,
        media);
  }
}
