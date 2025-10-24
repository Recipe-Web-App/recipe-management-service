package com.recipe_manager.model.enums;

/**
 * Enum representing the visibility level of a recipe collection. Controls who can VIEW a
 * collection. Maps to the collection_visibility_enum in the database.
 */
public enum CollectionVisibility {
  /** Anyone can view the collection. */
  PUBLIC,
  /** Only owner and collaborators can view the collection. */
  PRIVATE,
  /** Only owner, collaborators, and friends can view the collection. */
  FRIENDS_ONLY
}
