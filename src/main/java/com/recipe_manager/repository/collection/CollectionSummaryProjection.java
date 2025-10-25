package com.recipe_manager.repository.collection;

import java.time.LocalDateTime;
import java.util.UUID;

import com.recipe_manager.model.enums.CollaborationMode;
import com.recipe_manager.model.enums.CollectionVisibility;

/**
 * Projection interface for collection summary data from database views. Used by Spring Data JPA to
 * map query results from vw_collection_summary view.
 */
public interface CollectionSummaryProjection {
  /**
   * Gets the collection ID.
   *
   * @return the collection ID
   */
  Long getCollectionId();

  /**
   * Gets the collection name.
   *
   * @return the collection name
   */
  String getName();

  /**
   * Gets the collection description.
   *
   * @return the collection description
   */
  String getDescription();

  /**
   * Gets the visibility level.
   *
   * @return the visibility level
   */
  CollectionVisibility getVisibility();

  /**
   * Gets the collaboration mode.
   *
   * @return the collaboration mode
   */
  CollaborationMode getCollaborationMode();

  /**
   * Gets the owner user ID.
   *
   * @return the owner user ID
   */
  UUID getOwnerId();

  /**
   * Gets the recipe count.
   *
   * @return the number of recipes in the collection
   */
  Integer getRecipeCount();

  /**
   * Gets the collaborator count.
   *
   * @return the number of collaborators
   */
  Integer getCollaboratorCount();

  /**
   * Gets the creation timestamp.
   *
   * @return the creation timestamp
   */
  LocalDateTime getCreatedAt();

  /**
   * Gets the last update timestamp.
   *
   * @return the last update timestamp
   */
  LocalDateTime getUpdatedAt();
}
