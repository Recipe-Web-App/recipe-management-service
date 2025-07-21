package com.recipe_manager.model.entity.recipe;

import java.math.BigDecimal;

import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.enums.IngredientUnit;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing the relationship between recipes and ingredients. Maps to the
 * recipe_ingredients table in the database.
 *
 * <p>WARNING: The all-args constructor has many parameters. Consider using the builder for clarity.
 */
@Entity
@Table(name = "recipe_ingredients", schema = "recipe_manager")
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class RecipeIngredient {

  /** Schema constraint as defined in db for decimal precision. */
  private static final int QUANTITY_DECIMAL_PRECISION = 8;

  /** Schema constraint as defined in db for decimal scale. */
  private static final int QUANTITY_DECIMAL_SCALE = 3;

  /** The composite ID for this relationship. */
  @EmbeddedId private RecipeIngredientId id;

  /** The recipe entity. */
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("recipeId")
  @JoinColumn(name = "recipe_id", nullable = false)
  private Recipe recipe;

  /** The ingredient entity. */
  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("ingredientId")
  @JoinColumn(name = "ingredient_id", nullable = false)
  private Ingredient ingredient;

  /** The quantity of the ingredient. */
  @Column(name = "quantity", precision = QUANTITY_DECIMAL_PRECISION, scale = QUANTITY_DECIMAL_SCALE)
  private BigDecimal quantity;

  /** The unit of measurement. */
  @Enumerated(EnumType.STRING)
  @Column(name = "unit")
  private IngredientUnit unit;

  /** Whether the ingredient is optional. */
  @Column(name = "is_optional")
  @Builder.Default
  private Boolean isOptional = false;

  /**
   * Returns the composite ID.
   *
   * <p>Override this method with care if you subclass RecipeIngredient. Document extension safety.
   *
   * @return the composite ID
   */
  public RecipeIngredientId getId() {
    return id == null ? null : new RecipeIngredientId(id);
  }

  /**
   * Sets the composite ID.
   *
   * <p>Override this method with care if you subclass RecipeIngredient. Document extension safety.
   *
   * @param id the composite ID
   */
  public void setId(final RecipeIngredientId id) {
    this.id = id == null ? null : new RecipeIngredientId(id);
  }

  /**
   * Returns the recipe entity.
   *
   * <p>Override this method with care if you subclass RecipeIngredient. Document extension safety.
   *
   * @return the recipe
   */
  public Recipe getRecipe() {
    return recipe == null ? null : new Recipe(recipe);
  }

  /**
   * Sets the recipe entity.
   *
   * <p>Override this method with care if you subclass RecipeIngredient. Document extension safety.
   *
   * @param recipe the recipe
   */
  public void setRecipe(final Recipe recipe) {
    this.recipe = recipe == null ? null : new Recipe(recipe);
  }

  /**
   * Returns the ingredient entity.
   *
   * <p>Override this method with care if you subclass RecipeIngredient. Document extension safety.
   *
   * @return the ingredient
   */
  public Ingredient getIngredient() {
    return ingredient == null ? null : new Ingredient(ingredient);
  }

  /**
   * Sets the ingredient entity.
   *
   * <p>Override this method with care if you subclass RecipeIngredient. Document extension safety.
   *
   * @param ingredient the ingredient
   */
  public void setIngredient(final Ingredient ingredient) {
    this.ingredient = ingredient == null ? null : new Ingredient(ingredient);
  }

  /**
   * All-args constructor for RecipeIngredient.
   *
   * <p>WARNING: This constructor has many parameters. Prefer using the builder for clarity.
   *
   * @param id the composite ID
   * @param recipe the recipe
   * @param ingredient the ingredient
   * @param quantity the quantity
   * @param unit the unit
   * @param isOptional whether the ingredient is optional
   */
  public RecipeIngredient(
      final RecipeIngredientId id,
      final Recipe recipe,
      final Ingredient ingredient,
      final BigDecimal quantity,
      final IngredientUnit unit,
      final Boolean isOptional) {
    this.id = id == null ? null : new RecipeIngredientId(id);
    this.recipe = recipe == null ? null : new Recipe(recipe);
    this.ingredient = ingredient == null ? null : new Ingredient(ingredient);
    this.quantity = quantity;
    this.unit = unit;
    this.isOptional = isOptional;
  }

  /** Builder for RecipeIngredient. Use to construct instances with clarity and safety. */
  public static class RecipeIngredientBuilder {
    /**
     * Sets the composite ID.
     *
     * @param id the composite ID
     * @return this builder
     */
    public RecipeIngredientBuilder id(final RecipeIngredientId id) {
      this.id = id == null ? null : new RecipeIngredientId(id);
      return this;
    }

    /**
     * Sets the recipe.
     *
     * @param recipe the recipe
     * @return this builder
     */
    public RecipeIngredientBuilder recipe(final Recipe recipe) {
      this.recipe = recipe == null ? null : new Recipe(recipe);
      return this;
    }

    /**
     * Sets the ingredient.
     *
     * @param ingredient the ingredient
     * @return this builder
     */
    public RecipeIngredientBuilder ingredient(final Ingredient ingredient) {
      this.ingredient = ingredient == null ? null : new Ingredient(ingredient);
      return this;
    }
  }
}
