package com.recipe_manager.model.dto.request;

import java.util.List;

import com.recipe_manager.model.enums.DifficultyLevel;
import com.recipe_manager.model.enums.IngredientMatchMode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for searching recipes.
 *
 * <p>Contains search criteria that will be sent in the POST request body. Pagination parameters
 * (page, size, sort) are handled separately via query parameters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRecipesRequest {

  /** Text search query for recipe title and description. Case-insensitive partial matching. */
  private String recipeNameQuery;

  /** List of ingredient names to search for. */
  private List<String> ingredients;

  /**
   * Logic for ingredient matching. Defaults to AND (recipes must contain ALL specified
   * ingredients).
   */
  @Builder.Default private IngredientMatchMode ingredientMatchMode = IngredientMatchMode.AND;

  /** Filter by difficulty level. */
  private DifficultyLevel difficulty;

  /** Filter by maximum cooking time in minutes. */
  private Integer maxCookingTime;

  /** Filter by maximum preparation time in minutes. */
  private Integer maxPreparationTime;

  /** Filter by minimum number of servings. */
  private Integer minServings;

  /** Filter by maximum number of servings. */
  private Integer maxServings;
}
