package com.recipe_manager.model.dto.request;

import java.util.List;

import com.recipe_manager.model.enums.DifficultyLevel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Request DTO for updating an existing recipe. All fields are optional.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
public final class UpdateRecipeRequest extends AbstractRecipeRequest {

  /**
   * All-args constructor for UpdateRecipeRequest.
   *
   * <p>WARNING: This constructor has many parameters. Prefer using the builder for clarity.
   *
   * @param title the title
   * @param description the description
   * @param originUrl the origin URL
   * @param servings the servings
   * @param preparationTime the preparation time
   * @param cookingTime the cooking time
   * @param difficulty the difficulty
   * @param ingredients the ingredients list
   * @param steps the steps list
   */
  // CHECKSTYLE:OFF: ParameterNumber
  public UpdateRecipeRequest(
      final String title,
      final String description,
      final String originUrl,
      final java.math.BigDecimal servings,
      final Integer preparationTime,
      final Integer cookingTime,
      final DifficultyLevel difficulty,
      final List<CreateRecipeIngredientRequest> ingredients,
      final List<CreateRecipeStepRequest> steps) {
    super(
        title,
        description,
        originUrl,
        servings,
        preparationTime,
        cookingTime,
        difficulty,
        ingredients,
        steps);
  }

  // CHECKSTYLE:ON: ParameterNumber

  /**
   * Builder constructor for UpdateRecipeRequest.
   *
   * @param b the builder
   */
  protected UpdateRecipeRequest(final UpdateRecipeRequestBuilder<?, ?> b) {
    super(b);
    this.setIngredients(safeList(this.getIngredients()));
    this.setSteps(safeList(this.getSteps()));
  }
}
