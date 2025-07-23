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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing the relationship between recipes and ingredients. Maps to the
 * recipe_ingredients table in the database.
 */
@Entity
@Table(name = "recipe_ingredients", schema = "recipe_manager")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
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
  @Default
  private Boolean isOptional = false;
}
