package com.recipe_manager.model.enums;

/**
 * Enum representing the category of a recipe revision. Maps to the revision_category_enum in the
 * database.
 */
public enum RevisionCategory {
  /** Revision affects ingredients. */
  INGREDIENT,
  /** Revision affects steps. */
  STEP
}
