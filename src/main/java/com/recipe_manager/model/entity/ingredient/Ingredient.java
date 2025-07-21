package com.recipe_manager.model.entity.ingredient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.recipe_manager.model.entity.recipe.RecipeIngredient;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing an ingredient in the system. Maps to the ingredients table in the database.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Entity
@Table(name = "ingredients", schema = "recipe_manager")
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString(exclude = "recipeIngredients")
@EqualsAndHashCode(exclude = "recipeIngredients")
public class Ingredient {
  /** Max length allowed by database schema. */
  private static final int MAX_INGREDIENT_NAME_LENGTH = 100;

  /** The unique ID of the ingredient. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ingredient_id")
  private Long ingredientId;

  /** The name of the ingredient. */
  @NotBlank
  @Size(max = MAX_INGREDIENT_NAME_LENGTH)
  @Column(name = "name", nullable = false)
  private String name;

  /** The description of the ingredient. */
  @Column(name = "description", columnDefinition = "text")
  private String description;

  /** The category of the ingredient. */
  @Column(name = "category")
  private String category;

  /** The creation timestamp. */
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /** The list of recipe ingredients that use this ingredient. */
  @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RecipeIngredient> recipeIngredients;

  /**
   * Returns an unmodifiable list of recipe ingredients.
   *
   * <p>Override this method with care if you subclass Ingredient. Document extension safety.
   *
   * @return the list of recipe ingredients
   */
  public List<RecipeIngredient> getRecipeIngredients() {
    return Collections.unmodifiableList(recipeIngredients);
  }

  /**
   * Sets the list of recipe ingredients.
   *
   * <p>Override this method with care if you subclass Ingredient. Document extension safety.
   *
   * @param recipeIngredients the list of recipe ingredients
   */
  public void setRecipeIngredients(final List<RecipeIngredient> recipeIngredients) {
    this.recipeIngredients =
        recipeIngredients != null ? new ArrayList<>(recipeIngredients) : new ArrayList<>();
  }

  /**
   * All-args constructor for Ingredient.
   *
   * <p>WARNING: This constructor has many parameters. Prefer using the builder for clarity.
   *
   * @param ingredientId the ingredient ID
   * @param name the ingredient name
   * @param description the description
   * @param category the category
   * @param createdAt the creation timestamp
   * @param recipeIngredients the recipe ingredients list
   */
  public Ingredient(
      final Long ingredientId,
      final String name,
      final String description,
      final String category,
      final LocalDateTime createdAt,
      final List<RecipeIngredient> recipeIngredients) {
    this.ingredientId = ingredientId;
    this.name = name;
    this.description = description;
    this.category = category;
    this.createdAt = createdAt;
    this.recipeIngredients =
        recipeIngredients != null ? new ArrayList<>(recipeIngredients) : new ArrayList<>();
  }

  /**
   * Copy constructor for Ingredient.
   *
   * @param other the Ingredient to copy
   */
  public Ingredient(final Ingredient other) {
    this.ingredientId = other.ingredientId;
    this.name = other.name;
    this.description = other.description;
    this.category = other.category;
    this.createdAt = other.createdAt;
    this.recipeIngredients =
        other.recipeIngredients != null
            ? new ArrayList<>(other.recipeIngredients)
            : new ArrayList<>();
  }

  /** Builder for Ingredient. Use to construct instances with clarity and safety. */
  public static class IngredientBuilder {
    /**
     * Sets the recipe ingredients list.
     *
     * @param recipeIngredients the list of recipe ingredients
     * @return this builder
     */
    public IngredientBuilder recipeIngredients(final List<RecipeIngredient> recipeIngredients) {
      this.recipeIngredients =
          recipeIngredients == null ? new ArrayList<>() : new ArrayList<>(recipeIngredients);
      return this;
    }
  }
}
