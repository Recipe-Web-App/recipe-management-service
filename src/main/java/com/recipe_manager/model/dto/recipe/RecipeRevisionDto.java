package com.recipe_manager.model.dto.recipe;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.dto.media.RecipeRevisionMediaDto;
import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object for RecipeRevision entity. Used for transferring recipe revision data
 * between layers.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
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
  @Valid @Default private List<RecipeRevisionMediaDto> media = new ArrayList<>();
}
