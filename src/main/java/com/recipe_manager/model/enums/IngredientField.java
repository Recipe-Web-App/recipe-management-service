package com.recipe_manager.model.enums;

/**
 * Enum representing the fields of a recipe ingredient that can be modified in a revision. Used for
 * tracking specific field changes in ingredient update revisions.
 */
public enum IngredientField {
  /** The quantity of the ingredient. */
  QUANTITY,

  /** The unit of measurement for the ingredient. */
  UNIT,

  /** Whether the ingredient is optional in the recipe. */
  OPTIONAL_STATUS,

  /** The description of the ingredient. */
  DESCRIPTION
}
