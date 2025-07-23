package com.recipe_manager.model.dto.request;

import java.util.List;

import com.recipe_manager.model.dto.recipe.RecipeIngredientDto;
import com.recipe_manager.model.dto.recipe.RecipeStepDto;
import com.recipe_manager.model.enums.DifficultyLevel;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/** Request DTO for creating a new recipe. All fields are required. */
@Data
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
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
  public List<@Valid @NotNull RecipeIngredientDto> getIngredients() {
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
  public List<@Valid @NotNull RecipeStepDto> getSteps() {
    return super.getSteps();
  }
}
