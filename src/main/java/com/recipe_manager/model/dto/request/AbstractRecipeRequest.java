package com.recipe_manager.model.dto.request;

import java.math.BigDecimal;
import java.util.List;

import com.recipe_manager.model.dto.recipe.RecipeIngredientDto;
import com.recipe_manager.model.dto.recipe.RecipeStepDto;
import com.recipe_manager.model.dto.recipe.RecipeTagDto;
import com.recipe_manager.model.enums.DifficultyLevel;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Abstract base class for recipe request DTOs (create/update). Contains all shared fields,
 * constructors, and logic.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AbstractRecipeRequest {
  /** The recipe title. */
  private String title;

  /** The recipe description. */
  private String description;

  /** The origin URL. */
  private String originUrl;

  /** The servings. */
  private BigDecimal servings;

  /** The preparation time. */
  private Integer preparationTime;

  /** The cooking time. */
  private Integer cookingTime;

  /** The difficulty level. */
  private DifficultyLevel difficulty;

  /** The list of ingredients. */
  @Valid private List<RecipeIngredientDto> ingredients;

  /** The list of steps. */
  @Valid private List<RecipeStepDto> steps;

  /** The list of tags. */
  @Valid private List<RecipeTagDto> tags;
}
