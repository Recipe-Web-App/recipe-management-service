package com.recipe_manager.model.dto.recipe;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.recipe_manager.model.enums.DifficultyLevel;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object for Recipe entity. Used for transferring recipe data between layers.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
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
  @Valid private List<RecipeIngredientDto> ingredients;

  /** The list of steps. */
  @Valid private List<RecipeStepDto> steps;

  /** The list of tags. */
  @Valid private List<RecipeTagDto> tags;

  /** The list of revisions. */
  @Valid private List<RecipeRevisionDto> revisions;

  /** The list of favorites. */
  @Valid private List<RecipeFavoriteDto> favorites;

  /**
   * All-args constructor with defensive copying for mutable fields.
   *
   * <p>WARNING: This constructor has many parameters. Prefer using the builder for clarity.
   *
   * @param recipeId the recipe ID
   * @param userId the user ID
   * @param title the title
   * @param description the description
   * @param originUrl the origin URL
   * @param servings the servings
   * @param preparationTime the preparation time
   * @param cookingTime the cooking time
   * @param difficulty the difficulty
   * @param createdAt the creation timestamp
   * @param updatedAt the update timestamp
   * @param ingredients the ingredients list
   * @param steps the steps list
   * @param tags the tags list
   * @param revisions the revisions list
   * @param favorites the favorites list
   */
  // CHECKSTYLE:OFF: ParameterNumber
  public RecipeDto(
      final Long recipeId,
      final UUID userId,
      final String title,
      final String description,
      final String originUrl,
      final BigDecimal servings,
      final Integer preparationTime,
      final Integer cookingTime,
      final DifficultyLevel difficulty,
      final LocalDateTime createdAt,
      final LocalDateTime updatedAt,
      final List<RecipeIngredientDto> ingredients,
      final List<RecipeStepDto> steps,
      final List<RecipeTagDto> tags,
      final List<RecipeRevisionDto> revisions,
      final List<RecipeFavoriteDto> favorites) {
    this.recipeId = recipeId;
    this.userId = userId;
    this.title = title;
    this.description = description;
    this.originUrl = originUrl;
    this.servings = servings;
    this.preparationTime = preparationTime;
    this.cookingTime = cookingTime;
    this.difficulty = difficulty;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.ingredients = ingredients != null ? new ArrayList<>(ingredients) : new ArrayList<>();
    this.steps = steps != null ? new ArrayList<>(steps) : new ArrayList<>();
    this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
    this.revisions = revisions != null ? new ArrayList<>(revisions) : new ArrayList<>();
    this.favorites = favorites != null ? new ArrayList<>(favorites) : new ArrayList<>();
  }

  // CHECKSTYLE:ON: ParameterNumber

  /** Builder for RecipeDto. Use to construct instances with clarity and safety. */
  public static final class RecipeDtoBuilder {
    /**
     * Sets the ingredients list.
     *
     * @param ingredients the list of ingredients
     * @return this builder
     */
    public RecipeDtoBuilder ingredients(final List<RecipeIngredientDto> ingredients) {
      this.ingredients = ingredients == null ? new ArrayList<>() : new ArrayList<>(ingredients);
      return this;
    }

    /**
     * Sets the steps list.
     *
     * @param steps the list of steps
     * @return this builder
     */
    public RecipeDtoBuilder steps(final List<RecipeStepDto> steps) {
      this.steps = steps == null ? new ArrayList<>() : new ArrayList<>(steps);
      return this;
    }

    /**
     * Sets the tags list.
     *
     * @param tags the list of tags
     * @return this builder
     */
    public RecipeDtoBuilder tags(final List<RecipeTagDto> tags) {
      this.tags = tags == null ? new ArrayList<>() : new ArrayList<>(tags);
      return this;
    }

    /**
     * Sets the revisions list.
     *
     * @param revisions the list of revisions
     * @return this builder
     */
    public RecipeDtoBuilder revisions(final List<RecipeRevisionDto> revisions) {
      this.revisions = revisions == null ? new ArrayList<>() : new ArrayList<>(revisions);
      return this;
    }

    /**
     * Sets the favorites list.
     *
     * @param favorites the list of favorites
     * @return this builder
     */
    public RecipeDtoBuilder favorites(final List<RecipeFavoriteDto> favorites) {
      this.favorites = favorites == null ? new ArrayList<>() : new ArrayList<>(favorites);
      return this;
    }
  }

  /**
   * Returns an unmodifiable list of ingredients.
   *
   * @return the ingredients list
   */
  public List<RecipeIngredientDto> getIngredients() {
    return Collections.unmodifiableList(ingredients);
  }

  /**
   * Sets the ingredients list.
   *
   * @param ingredients the list of ingredients
   */
  public void setIngredients(final List<RecipeIngredientDto> ingredients) {
    this.ingredients = ingredients != null ? new ArrayList<>(ingredients) : new ArrayList<>();
  }

  /**
   * Returns an unmodifiable list of steps.
   *
   * @return the steps list
   */
  public List<RecipeStepDto> getSteps() {
    return Collections.unmodifiableList(steps);
  }

  /**
   * Sets the steps list.
   *
   * @param steps the list of steps
   */
  public void setSteps(final List<RecipeStepDto> steps) {
    this.steps = steps != null ? new ArrayList<>(steps) : new ArrayList<>();
  }

  /**
   * Returns an unmodifiable list of tags.
   *
   * @return the tags list
   */
  public List<RecipeTagDto> getTags() {
    return Collections.unmodifiableList(tags);
  }

  /**
   * Sets the tags list.
   *
   * @param tags the list of tags
   */
  public void setTags(final List<RecipeTagDto> tags) {
    this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
  }

  /**
   * Returns an unmodifiable list of revisions.
   *
   * @return the revisions list
   */
  public List<RecipeRevisionDto> getRevisions() {
    return Collections.unmodifiableList(revisions);
  }

  /**
   * Sets the revisions list.
   *
   * @param revisions the list of revisions
   */
  public void setRevisions(final List<RecipeRevisionDto> revisions) {
    this.revisions = revisions != null ? new ArrayList<>(revisions) : new ArrayList<>();
  }

  /**
   * Returns an unmodifiable list of favorites.
   *
   * @return the favorites list
   */
  public List<RecipeFavoriteDto> getFavorites() {
    return Collections.unmodifiableList(favorites);
  }

  /**
   * Sets the favorites list.
   *
   * @param favorites the list of favorites
   */
  public void setFavorites(final List<RecipeFavoriteDto> favorites) {
    this.favorites = favorites != null ? new ArrayList<>(favorites) : new ArrayList<>();
  }

  /**
   * Checks equality based on all fields.
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
    final RecipeDto that = (RecipeDto) obj;
    return Objects.equals(recipeId, that.recipeId)
        && Objects.equals(userId, that.userId)
        && Objects.equals(title, that.title)
        && Objects.equals(description, that.description)
        && Objects.equals(originUrl, that.originUrl)
        && Objects.equals(servings, that.servings)
        && Objects.equals(preparationTime, that.preparationTime)
        && Objects.equals(cookingTime, that.cookingTime)
        && difficulty == that.difficulty
        && Objects.equals(createdAt, that.createdAt)
        && Objects.equals(updatedAt, that.updatedAt)
        && Objects.equals(ingredients, that.ingredients)
        && Objects.equals(steps, that.steps)
        && Objects.equals(tags, that.tags)
        && Objects.equals(revisions, that.revisions)
        && Objects.equals(favorites, that.favorites);
  }

  /**
   * Computes hash code based on all fields.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(
        recipeId,
        userId,
        title,
        description,
        originUrl,
        servings,
        preparationTime,
        cookingTime,
        difficulty,
        createdAt,
        updatedAt,
        ingredients,
        steps,
        tags,
        revisions,
        favorites);
  }
}
