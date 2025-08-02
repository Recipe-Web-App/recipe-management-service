package com.recipe_manager.model.enums;

/**
 * Enum representing the type of a recipe revision. Maps to the revision_type_enum in the database.
 */
public enum RevisionType {
  /** Revision adds a new entity. */
  ADD,
  /** Revision updates an existing entity. */
  UPDATE,
  /** Revision deletes an entity. */
  DELETE
}
