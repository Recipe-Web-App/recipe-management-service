package com.recipe_manager.model.dto.recipe;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.enums.DifficultyLevel;

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

/** Data Transfer Object for Recipe entity. Used for transferring recipe data between layers. */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public final class RecipeDto {
  /** The unique ID of the recipe. */
  private Long recipeId;

  /** The user ID of the recipe owner. */
  private UUID userId;

  /** The title of the recipe. */
  private String title;

  /** The description of the recipe. */
  private String description;

  /** The origin URL of the recipe. */
  private String originUrl;

  /** The number of servings. */
  private BigDecimal servings;

  /** Preparation time in minutes. */
  private Integer preparationTime;

  /** Cooking time in minutes. */
  private Integer cookingTime;

  /** The difficulty level. */
  private DifficultyLevel difficulty;

  /** The creation timestamp. */
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  private LocalDateTime updatedAt;

  /** The list of ingredients. */
  @Valid @Default private List<RecipeIngredientDto> ingredients = new ArrayList<>();

  /** The list of steps. */
  @Valid @Default private List<RecipeStepDto> steps = new ArrayList<>();

  /** The list of tags. */
  @Valid @Default private List<RecipeTagDto> tags = new ArrayList<>();

  /** The list of revisions. */
  @Valid @Default private List<RecipeRevisionDto> revisions = new ArrayList<>();

  /** The list of favorites. */
  @Valid @Default private List<RecipeFavoriteDto> favorites = new ArrayList<>();
}
