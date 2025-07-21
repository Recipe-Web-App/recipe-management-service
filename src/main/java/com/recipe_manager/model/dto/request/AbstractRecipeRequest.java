package com.recipe_manager.model.dto.request;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.recipe_manager.model.enums.DifficultyLevel;

import jakarta.validation.Valid;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Abstract base class for recipe request DTOs (create/update). Contains all shared fields,
 * constructors, and logic.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
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
  @Valid @Default private List<CreateRecipeIngredientRequest> ingredients = new ArrayList<>();

  /** The list of steps. */
  @Valid @Default private List<CreateRecipeStepRequest> steps = new ArrayList();

  /**
   * All-args constructor for AbstractRecipeRequest.
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
  protected AbstractRecipeRequest(
      final String title,
      final String description,
      final String originUrl,
      final BigDecimal servings,
      final Integer preparationTime,
      final Integer cookingTime,
      final DifficultyLevel difficulty,
      final List<CreateRecipeIngredientRequest> ingredients,
      final List<CreateRecipeStepRequest> steps) {
    this.title = title;
    this.description = description;
    this.originUrl = originUrl;
    this.servings = servings;
    this.preparationTime = preparationTime;
    this.cookingTime = cookingTime;
    this.difficulty = difficulty;
    this.ingredients = safeList(ingredients);
    this.steps = safeList(steps);
  }

  // CHECKSTYLE:ON: ParameterNumber

  /**
   * Utility method to safely copy a list or return an empty list if null.
   *
   * @param input the input list
   * @param <T> the type of list elements
   * @return a new list or empty list if input is null
   */
  protected static <T> List<T> safeList(final List<T> input) {
    return input != null ? new ArrayList<>(input) : new ArrayList<>();
  }

  /**
   * Returns an unmodifiable list of ingredients.
   *
   * @return the ingredients list
   */
  public List<CreateRecipeIngredientRequest> getIngredients() {
    return Collections.unmodifiableList(ingredients);
  }

  /**
   * Sets the ingredients list.
   *
   * @param ingredients the list of ingredients
   */
  public void setIngredients(final List<CreateRecipeIngredientRequest> ingredients) {
    this.ingredients = ingredients != null ? new ArrayList<>(ingredients) : new ArrayList<>();
  }

  /**
   * Returns an unmodifiable list of steps.
   *
   * @return the steps list
   */
  public List<CreateRecipeStepRequest> getSteps() {
    return Collections.unmodifiableList(steps);
  }

  /**
   * Sets the steps list.
   *
   * @param steps the list of steps
   */
  public void setSteps(final List<CreateRecipeStepRequest> steps) {
    this.steps = steps != null ? new ArrayList<>(steps) : new ArrayList<>();
  }

  /**
   * Returns the recipe title.
   *
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the recipe title.
   *
   * @param title the title
   */
  public void setTitle(final String title) {
    this.title = title;
  }

  /**
   * Returns the recipe description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the recipe description.
   *
   * @param description the description
   */
  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * Returns the origin URL.
   *
   * @return the originUrl
   */
  public String getOriginUrl() {
    return originUrl;
  }

  /**
   * Sets the origin URL.
   *
   * @param originUrl the origin URL
   */
  public void setOriginUrl(final String originUrl) {
    this.originUrl = originUrl;
  }

  /**
   * Returns the servings.
   *
   * @return the servings
   */
  public BigDecimal getServings() {
    return servings;
  }

  /**
   * Sets the servings.
   *
   * @param servings the servings
   */
  public void setServings(final BigDecimal servings) {
    this.servings = servings;
  }

  /**
   * Returns the preparation time.
   *
   * @return the preparation time
   */
  public Integer getPreparationTime() {
    return preparationTime;
  }

  /**
   * Sets the preparation time.
   *
   * @param preparationTime the preparation time
   */
  public void setPreparationTime(final Integer preparationTime) {
    this.preparationTime = preparationTime;
  }

  /**
   * Returns the cooking time.
   *
   * @return the cooking time
   */
  public Integer getCookingTime() {
    return cookingTime;
  }

  /**
   * Sets the cooking time.
   *
   * @param cookingTime the cooking time
   */
  public void setCookingTime(final Integer cookingTime) {
    this.cookingTime = cookingTime;
  }

  /**
   * Returns the difficulty level.
   *
   * @return the difficulty
   */
  public DifficultyLevel getDifficulty() {
    return difficulty;
  }

  /**
   * Sets the difficulty level.
   *
   * @param difficulty the difficulty
   */
  public void setDifficulty(final DifficultyLevel difficulty) {
    this.difficulty = difficulty;
  }

  /**
   * Checks equality based on all fields.
   *
   * <p>If you override this method in a subclass, ensure you include all relevant fields and
   * maintain the contract with hashCode. Document any changes to equality logic.
   *
   * @param obj the object to compare
   * @return true if equal, false otherwise
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final AbstractRecipeRequest that = (AbstractRecipeRequest) obj;
    return Objects.equals(title, that.title)
        && Objects.equals(description, that.description)
        && Objects.equals(originUrl, that.originUrl)
        && Objects.equals(servings, that.servings)
        && Objects.equals(preparationTime, that.preparationTime)
        && Objects.equals(cookingTime, that.cookingTime)
        && difficulty == that.difficulty
        && Objects.equals(ingredients, that.ingredients)
        && Objects.equals(steps, that.steps);
  }

  /**
   * Computes hash code based on all fields.
   *
   * <p>If you override this method in a subclass, ensure you include all relevant fields and
   * maintain the contract with equals. Document any changes to hash code logic.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(
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

  /**
   * Builder for AbstractRecipeRequest. Use to construct instances with clarity and safety.
   *
   * @param <C> the concrete type
   * @param <B> the builder type
   */
  public abstract static class AbstractRecipeRequestBuilder<
      C extends AbstractRecipeRequest, B extends AbstractRecipeRequestBuilder<C, B>> {
    /**
     * Sets the ingredients list.
     *
     * @param ingredients the list of ingredients
     * @return this builder
     */
    public B ingredients(final List<CreateRecipeIngredientRequest> ingredients) {
      this.ingredients$value = safeList(ingredients);
      this.ingredients$set = true;
      return self();
    }

    /**
     * Sets the steps list.
     *
     * @param steps the list of steps
     * @return this builder
     */
    public B steps(final List<CreateRecipeStepRequest> steps) {
      this.steps$value = safeList(steps);
      this.steps$set = true;
      return self();
    }
  }
}
