package com.recipe_manager.model.enums;

/**
 * Enum representing the collaboration mode of a recipe collection. Controls who can EDIT a
 * collection (add/remove recipes). Maps to the collaboration_mode_enum in the database.
 */
public enum CollaborationMode {
  /** Only the collection owner can edit. */
  OWNER_ONLY,
  /** Any authenticated user can edit the collection. */
  ALL_USERS,
  /** Only users in the collection_collaborators table can edit. */
  SPECIFIC_USERS
}
