package com.recipe_manager.model.enums;

/** Logic operator for ingredient matching in recipe searches. */
public enum IngredientMatchMode {
  /** Recipes must contain ALL specified ingredients. */
  AND,
  /** Recipes must contain ANY of the specified ingredients. */
  OR
}
