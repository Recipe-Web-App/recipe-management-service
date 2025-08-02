package com.recipe_manager.model.dto.request;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.recipe_manager.model.dto.recipe.RecipeIngredientDto;
import com.recipe_manager.model.dto.recipe.RecipeStepDto;
import com.recipe_manager.model.dto.recipe.RecipeTagDto;
import com.recipe_manager.model.enums.DifficultyLevel;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Abstract base class for recipe request DTOs (create/update). Contains all shared fields,
 * constructors, and logic.
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@ToString
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
  @Valid @Default private List<RecipeIngredientDto> ingredients = new ArrayList<>();

  /** The list of steps. */
  @Valid @Default private List<RecipeStepDto> steps = new ArrayList<>();

  /** The list of tags. */
  @Valid @Default private List<RecipeTagDto> tags = new ArrayList<>();
}
