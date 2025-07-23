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
 * Data Transfer Object for RecipeIngredient media. Used for transferring recipe ingredient media
 * data between layers.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class RecipeIngredientMediaDto extends MediaDto {
  /** The recipe ID. */
  private Long recipeId;

  /** The ingredient ID. */
  private Long ingredientId;
}
