package com.recipe_manager.model.dto.response;

import java.util.List;

import com.recipe_manager.model.dto.recipe.RecipeIngredientDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Response DTO for recipe ingredients endpoint. Contains a list of ingredients for a specific
 * recipe.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class RecipeIngredientsResponse {
  /** The recipe ID. */
  private Long recipeId;

  /** List of ingredients for the recipe. */
  private List<RecipeIngredientDto> ingredients;

  /** Total count of ingredients. */
  private Integer totalCount;
}
