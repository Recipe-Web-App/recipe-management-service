package com.recipe_manager.repository.collection;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

  /**
   * Finds all collaborators for a collection ordered by granted date (newest first). This method
   * uses a native query to join with the users table to fetch usernames.
   *
   * @param collectionId the collection ID
   * @return list of collaborators ordered by granted_at DESC
   */
  @Query(
      value =
          "SELECT cc.collection_id, cc.user_id, u1.username, cc.granted_by, "
              + "u2.username AS granted_by_username, cc.granted_at "
              + "FROM recipe_manager.collection_collaborators cc "
              + "JOIN recipe_manager.users u1 ON cc.user_id = u1.user_id "
              + "JOIN recipe_manager.users u2 ON cc.granted_by = u2.user_id "
              + "WHERE cc.collection_id = :collectionId "
              + "ORDER BY cc.granted_at DESC",
      nativeQuery = true)
  List<Object[]> findCollaboratorsWithUsernamesByCollectionId(
      @Param("collectionId") Long collectionId);
}
