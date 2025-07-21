package com.recipe_manager.model.dto.request;

import java.util.List;

import com.recipe_manager.model.enums.DifficultyLevel;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Request DTO for creating a new recipe. All fields are required.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
public final class CreateRecipeRequest extends AbstractRecipeRequest {

  /**
   * Returns the recipe title.
   *
   * @return the title
   */
  @Override
  @NotBlank
  public String getTitle() {
    return super.getTitle();
  }

  /**
   * Returns the recipe description.
   *
   * @return the description
   */
  @Override
  @NotBlank
  public String getDescription() {
    return super.getDescription();
  }

  /**
   * Returns the servings.
   *
   * @return the servings
   */
  @Override
  @NotNull
  public java.math.BigDecimal getServings() {
    return super.getServings();
  }

  /**
   * Returns the preparation time.
   *
   * @return the preparation time
   */
  @Override
  @NotNull
  public Integer getPreparationTime() {
    return super.getPreparationTime();
  }

  /**
   * Returns the cooking time.
   *
   * @return the cooking time
   */
  @Override
  @NotNull
  public Integer getCookingTime() {
    return super.getCookingTime();
  }

  /**
   * Returns the difficulty level.
   *
   * @return the difficulty
   */
  @Override
  @NotNull
  public DifficultyLevel getDifficulty() {
    return super.getDifficulty();
  }

  /**
   * Returns the list of ingredients.
   *
   * @return the ingredients list
   */
  @Override
  @Valid
  @NotEmpty
  public List<@Valid @NotNull CreateRecipeIngredientRequest> getIngredients() {
    return super.getIngredients();
  }

  /**
   * Returns the list of steps.
   *
   * @return the steps list
   */
  @Override
  @Valid
  @NotEmpty
  public List<@Valid @NotNull CreateRecipeStepRequest> getSteps() {
    return super.getSteps();
  }

  /**
   * All-args constructor for CreateRecipeRequest.
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
  public CreateRecipeRequest(
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
   * Builder constructor for CreateRecipeRequest.
   *
   * @param b the builder
   */
  protected CreateRecipeRequest(final CreateRecipeRequestBuilder<?, ?> b) {
    super(b);
    this.setIngredients(safeList(this.getIngredients()));
    this.setSteps(safeList(this.getSteps()));
  }
}
