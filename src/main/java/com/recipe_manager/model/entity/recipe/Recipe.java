package com.recipe_manager.model.entity.recipe;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.recipe_manager.model.enums.DifficultyLevel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a recipe in the system. Maps to the recipes table in the database.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Entity
@Table(name = "recipes", schema = "recipe_manager")
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString(
    exclude = {
      "recipeIngredients",
      "recipeSteps",
      "recipeRevisions",
      "recipeFavorites",
      "recipeTags"
    })
@EqualsAndHashCode(
    exclude = {
      "recipeIngredients",
      "recipeSteps",
      "recipeRevisions",
      "recipeFavorites",
      "recipeTags"
    })
public class Recipe {

  /** Max name length as defined in DB schema. */
  private static final int MAX_TITLE_LENGTH = 255;

  /** Servings decimal precision as defined in DB schema. */
  private static final int SERVINGS_DECIMAL_PRECISION = 5;

  /** Servings decimal scale as defined in DB schema. */
  private static final int SERVINGS_DECIMAL_SCALE = 2;

  /** Max difficulty length as defined in DB schema. */
  private static final int MAX_DIFFICULTY_LENGTH = 5;

  /** The unique ID of the recipe. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "recipe_id")
  private Long recipeId;

  /** The user ID of the recipe owner. */
  @NotNull
  @Column(name = "user_id", nullable = false)
  private UUID userId;

  /** The title of the recipe. */
  @NotBlank
  @Size(max = MAX_TITLE_LENGTH)
  @Column(name = "title", nullable = false)
  private String title;

  /** The description of the recipe. */
  @Column(name = "description", columnDefinition = "text")
  private String description;

  /** The origin URL of the recipe. */
  @Column(name = "origin_url", columnDefinition = "text")
  private String originUrl;

  /** The number of servings. */
  @Column(name = "servings", precision = SERVINGS_DECIMAL_PRECISION, scale = SERVINGS_DECIMAL_SCALE)
  private BigDecimal servings;

  /** Preparation time in minutes. */
  @Column(name = "preparation_time")
  private Integer preparationTime;

  /** Cooking time in minutes. */
  @Column(name = "cooking_time")
  private Integer cookingTime;

  /** The difficulty level. */
  @Enumerated(EnumType.STRING)
  @Column(name = "difficulty", length = MAX_DIFFICULTY_LENGTH)
  private DifficultyLevel difficulty;

  /** The creation timestamp. */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /** The last update timestamp. */
  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /** The list of recipe ingredients. */
  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RecipeIngredient> recipeIngredients;

  /** The list of recipe steps. */
  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("stepNumber ASC")
  private List<RecipeStep> recipeSteps;

  /** The list of recipe revisions. */
  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RecipeRevision> recipeRevisions;

  /** The list of recipe favorites. */
  @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RecipeFavorite> recipeFavorites;

  /** The list of recipe tags. */
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "recipe_tag_junction",
      schema = "recipe_manager",
      joinColumns = @JoinColumn(name = "recipe_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private List<RecipeTag> recipeTags;

  /**
   * All-args constructor for Recipe.
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
   * @param recipeIngredients the recipe ingredients list
   * @param recipeSteps the recipe steps list
   * @param recipeRevisions the recipe revisions list
   * @param recipeFavorites the recipe favorites list
   * @param recipeTags the recipe tags list
   */
  // CHECKSTYLE:OFF: ParameterNumber
  public Recipe(
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
      final List<RecipeIngredient> recipeIngredients,
      final List<RecipeStep> recipeSteps,
      final List<RecipeRevision> recipeRevisions,
      final List<RecipeFavorite> recipeFavorites,
      final List<RecipeTag> recipeTags) {
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
    this.recipeIngredients =
        recipeIngredients != null ? new ArrayList<>(recipeIngredients) : new ArrayList<>();
    this.recipeSteps = recipeSteps != null ? new ArrayList<>(recipeSteps) : new ArrayList<>();
    this.recipeRevisions =
        recipeRevisions != null ? new ArrayList<>(recipeRevisions) : new ArrayList<>();
    this.recipeFavorites =
        recipeFavorites != null ? new ArrayList<>(recipeFavorites) : new ArrayList<>();
    this.recipeTags = recipeTags != null ? new ArrayList<>(recipeTags) : new ArrayList<>();
  }

  // CHECKSTYLE:ON: ParameterNumber

  /**
   * Copy constructor for Recipe.
   *
   * @param other the Recipe to copy
   */
  public Recipe(final Recipe other) {
    this.recipeId = other.recipeId;
    this.userId = other.userId;
    this.title = other.title;
    this.description = other.description;
    this.originUrl = other.originUrl;
    this.servings = other.servings;
    this.preparationTime = other.preparationTime;
    this.cookingTime = other.cookingTime;
    this.difficulty = other.difficulty;
    this.createdAt = other.createdAt;
    this.updatedAt = other.updatedAt;
    this.recipeIngredients =
        other.recipeIngredients != null
            ? new ArrayList<>(other.recipeIngredients)
            : new ArrayList<>();
    this.recipeSteps =
        other.recipeSteps != null ? new ArrayList<>(other.recipeSteps) : new ArrayList<>();
    this.recipeRevisions =
        other.recipeRevisions != null ? new ArrayList<>(other.recipeRevisions) : new ArrayList<>();
    this.recipeFavorites =
        other.recipeFavorites != null ? new ArrayList<>(other.recipeFavorites) : new ArrayList<>();
    this.recipeTags =
        other.recipeTags != null ? new ArrayList<>(other.recipeTags) : new ArrayList<>();
  }

  /** Builder for Recipe. Use to construct instances with clarity and safety. */
  public static class RecipeBuilder {
    /**
     * Sets the recipe ingredients list.
     *
     * @param recipeIngredients the list of recipe ingredients
     * @return this builder
     */
    public RecipeBuilder recipeIngredients(final List<RecipeIngredient> recipeIngredients) {
      this.recipeIngredients =
          recipeIngredients == null ? new ArrayList<>() : new ArrayList<>(recipeIngredients);
      return this;
    }

    /**
     * Sets the recipe steps list.
     *
     * @param recipeSteps the list of recipe steps
     * @return this builder
     */
    public RecipeBuilder recipeSteps(final List<RecipeStep> recipeSteps) {
      this.recipeSteps = recipeSteps == null ? new ArrayList<>() : new ArrayList<>(recipeSteps);
      return this;
    }

    /**
     * Sets the recipe revisions list.
     *
     * @param recipeRevisions the list of recipe revisions
     * @return this builder
     */
    public RecipeBuilder recipeRevisions(final List<RecipeRevision> recipeRevisions) {
      this.recipeRevisions =
          recipeRevisions == null ? new ArrayList<>() : new ArrayList<>(recipeRevisions);
      return this;
    }

    /**
     * Sets the recipe favorites list.
     *
     * @param recipeFavorites the list of recipe favorites
     * @return this builder
     */
    public RecipeBuilder recipeFavorites(final List<RecipeFavorite> recipeFavorites) {
      this.recipeFavorites =
          recipeFavorites == null ? new ArrayList<>() : new ArrayList<>(recipeFavorites);
      return this;
    }

    /**
     * Sets the recipe tags list.
     *
     * @param recipeTags the list of recipe tags
     * @return this builder
     */
    public RecipeBuilder recipeTags(final List<RecipeTag> recipeTags) {
      this.recipeTags = recipeTags == null ? new ArrayList<>() : new ArrayList<>(recipeTags);
      return this;
    }
  }

  // Helper methods
  /**
   * Adds a recipe ingredient to the recipe.
   *
   * @param recipeIngredient the recipe ingredient to add
   */
  public void addRecipeIngredient(final RecipeIngredient recipeIngredient) {
    recipeIngredients.add(recipeIngredient);
    recipeIngredient.setRecipe(this);
  }

  /**
   * Removes a recipe ingredient from the recipe.
   *
   * @param recipeIngredient the recipe ingredient to remove
   */
  public void removeRecipeIngredient(final RecipeIngredient recipeIngredient) {
    recipeIngredients.remove(recipeIngredient);
    recipeIngredient.setRecipe(null);
  }

  /**
   * Adds a recipe step to the recipe.
   *
   * @param recipeStep the recipe step to add
   */
  public void addRecipeStep(final RecipeStep recipeStep) {
    recipeSteps.add(recipeStep);
    recipeStep.setRecipe(this);
  }

  /**
   * Removes a recipe step from the recipe.
   *
   * @param recipeStep the recipe step to remove
   */
  public void removeRecipeStep(final RecipeStep recipeStep) {
    recipeSteps.remove(recipeStep);
    recipeStep.setRecipe(null);
  }

  /**
   * Adds a recipe tag to the recipe.
   *
   * @param recipeTag the recipe tag to add
   */
  public void addRecipeTag(final RecipeTag recipeTag) {
    recipeTags.add(recipeTag);
  }

  /**
   * Removes a recipe tag from the recipe.
   *
   * @param recipeTag the recipe tag to remove
   */
  public void removeRecipeTag(final RecipeTag recipeTag) {
    recipeTags.remove(recipeTag);
  }

  /**
   * Gets the list of recipe ingredients.
   *
   * @return the list of recipe ingredients
   */
  public List<RecipeIngredient> getRecipeIngredients() {
    return Collections.unmodifiableList(recipeIngredients);
  }

  /**
   * Sets the list of recipe ingredients.
   *
   * @param recipeIngredients the list of recipe ingredients
   */
  public void setRecipeIngredients(final List<RecipeIngredient> recipeIngredients) {
    this.recipeIngredients =
        recipeIngredients != null ? new ArrayList<>(recipeIngredients) : new ArrayList<>();
  }

  /**
   * Gets the list of recipe steps.
   *
   * @return the list of recipe steps
   */
  public List<RecipeStep> getRecipeSteps() {
    return Collections.unmodifiableList(recipeSteps);
  }

  /**
   * Sets the list of recipe steps.
   *
   * @param recipeSteps the list of recipe steps
   */
  public void setRecipeSteps(final List<RecipeStep> recipeSteps) {
    this.recipeSteps = recipeSteps != null ? new ArrayList<>(recipeSteps) : new ArrayList<>();
  }

  /**
   * Gets the list of recipe revisions.
   *
   * @return the list of recipe revisions
   */
  public List<RecipeRevision> getRecipeRevisions() {
    return Collections.unmodifiableList(recipeRevisions);
  }

  /**
   * Sets the list of recipe revisions.
   *
   * @param recipeRevisions the list of recipe revisions
   */
  public void setRecipeRevisions(final List<RecipeRevision> recipeRevisions) {
    this.recipeRevisions =
        recipeRevisions != null ? new ArrayList<>(recipeRevisions) : new ArrayList<>();
  }

  /**
   * Gets the list of recipe favorites.
   *
   * @return the list of recipe favorites
   */
  public List<RecipeFavorite> getRecipeFavorites() {
    return Collections.unmodifiableList(recipeFavorites);
  }

  /**
   * Sets the list of recipe favorites.
   *
   * @param recipeFavorites the list of recipe favorites
   */
  public void setRecipeFavorites(final List<RecipeFavorite> recipeFavorites) {
    this.recipeFavorites =
        recipeFavorites != null ? new ArrayList<>(recipeFavorites) : new ArrayList<>();
  }

  /**
   * Gets the list of recipe tags.
   *
   * @return the list of recipe tags
   */
  public List<RecipeTag> getRecipeTags() {
    return Collections.unmodifiableList(recipeTags);
  }

  /**
   * Sets the list of recipe tags.
   *
   * @param recipeTags the list of recipe tags
   */
  public void setRecipeTags(final List<RecipeTag> recipeTags) {
    this.recipeTags = recipeTags != null ? new ArrayList<>(recipeTags) : new ArrayList<>();
  }
}
