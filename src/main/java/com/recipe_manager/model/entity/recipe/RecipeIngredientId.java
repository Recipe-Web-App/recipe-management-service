package com.recipe_manager.model.entity.recipe;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Composite primary key for RecipeIngredient entity. */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class RecipeIngredientId implements Serializable {

  private static final long serialVersionUID = 1L;

  /** The recipe ID. */
  private Long recipeId;

  /** The ingredient ID. */
  private Long ingredientId;

  /**
   * Copy constructor for RecipeIngredientId.
   *
   * <p>WARNING: This constructor is for defensive copying only.
   *
   * @param other the RecipeIngredientId to copy
   */
  public RecipeIngredientId(final RecipeIngredientId other) {
    this.recipeId = other == null ? null : other.recipeId;
    this.ingredientId = other == null ? null : other.ingredientId;
  }
}
