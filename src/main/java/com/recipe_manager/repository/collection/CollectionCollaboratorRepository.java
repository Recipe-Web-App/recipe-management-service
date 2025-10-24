package com.recipe_manager.repository.collection;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.collection.CollectionCollaborator;
import com.recipe_manager.model.entity.collection.CollectionCollaboratorId;

/**
 * Repository interface for CollectionCollaborator entity operations. Handles user-to-collection
 * collaborator associations.
 */
@Repository
public interface CollectionCollaboratorRepository
    extends JpaRepository<CollectionCollaborator, CollectionCollaboratorId> {

  /**
   * Finds all collaborators for a specific collection.
   *
   * @param collectionId the collection ID
   * @return list of collaborators for the collection
   */
  List<CollectionCollaborator> findByIdCollectionId(Long collectionId);

  /**
   * Finds all collections where a user is a collaborator.
   *
   * @param userId the user ID
   * @return list of collaborator records for the user
   */
  List<CollectionCollaborator> findByIdUserId(UUID userId);

  /**
   * Checks if a user is a collaborator on a collection.
   *
   * @param collectionId the collection ID
   * @param userId the user ID
   * @return true if user is a collaborator on the collection
   */
  boolean existsByIdCollectionIdAndIdUserId(Long collectionId, UUID userId);

  /**
   * Removes a collaborator from a collection by composite key components.
   *
   * @param collectionId the collection ID
   * @param userId the user ID
   */
  void deleteByIdCollectionIdAndIdUserId(Long collectionId, UUID userId);

  /**
   * Counts the number of collaborators for a collection.
   *
   * @param collectionId the collection ID
   * @return count of collaborators for the collection
   */
  long countByIdCollectionId(Long collectionId);

  /**
   * Deletes all collaborators from a collection.
   *
   * @param collectionId the collection ID
   */
  void deleteByIdCollectionId(Long collectionId);

  /**
   * Finds collaborators in multiple collections (batch operation).
   *
   * @param collectionIds list of collection IDs
   * @return list of collaborators
   */
  List<CollectionCollaborator> findByIdCollectionIdIn(List<Long> collectionIds);
}
