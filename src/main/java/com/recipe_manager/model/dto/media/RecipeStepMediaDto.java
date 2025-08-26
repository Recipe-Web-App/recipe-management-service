package com.recipe_manager.model.dto.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Data Transfer Object for RecipeStep media relationship. Used for transferring recipe step-media
 * relationship data between layers.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@ToString
public final class RecipeStepMediaDto {
  /** The media ID. */
  private Long mediaId;

  /** The recipe ID. */
  private Long recipeId;

  /** The step ID. */
  private Long stepId;

  /** The media details. */
  private MediaDto media;
}
