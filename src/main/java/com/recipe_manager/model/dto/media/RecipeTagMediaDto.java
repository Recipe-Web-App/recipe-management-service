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
 * Data Transfer Object for RecipeTag media. Used for transferring recipe tag media data between
 * layers.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@ToString
public final class RecipeTagMediaDto {
  /** The media ID. */
  private Long mediaId;

  /** The recipe ID. */
  private Long recipeId;

  /** The tag ID. */
  private Long tagId;

  /** The media details. */
  private MediaDto media;
}
