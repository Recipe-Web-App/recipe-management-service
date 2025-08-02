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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class RecipeIngredientId implements Serializable {
  /** Serial version UID for ensuring compatibility during serialization. */
  private static final long serialVersionUID = 1L;

  /** The recipe ID. */
  private Long recipeId;

  /** The ingredient ID. */
  private Long ingredientId;
}
