package com.recipe_manager.repository.collection;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.collection.CollectionFavorite;
import com.recipe_manager.model.entity.collection.CollectionFavoriteId;

/**
 * Repository interface for CollectionFavorite entity operations.
 *
 * <p>Provides database access methods for managing collection favorites, including querying by
 * user, checking favorite status, and managing favorite relationships. Uses composite key pattern
 * with CollectionFavoriteId (userId + collectionId).
 *
 * <p>Key query patterns:
 *
 * <ul>
 *   <li>Access composite key fields using {@code id.userId} and {@code id.collectionId} convention
 *   <li>Use {@code JOIN FETCH} in custom queries to avoid N+1 query problems
 *   <li>Support pagination for list operations
 * </ul>
 */
@Repository
public interface CollectionFavoriteRepository
    extends JpaRepository<CollectionFavorite, CollectionFavoriteId> {

  /**
   * Find all favorites for a user with pagination support.
   *
   * @param userId the user ID to query favorites for
   * @param pageable pagination parameters (page, size, sort)
   * @return page of favorites for the specified user
   */
  Page<CollectionFavorite> findByIdUserId(UUID userId, Pageable pageable);

  /**
   * Find all favorites for a user with collection details eagerly loaded.
   *
   * <p>Uses JOIN FETCH to load collection data in a single query, avoiding N+1 query issues.
   * Results are ordered by favorited date descending (most recent first).
   *
   * @param userId the user ID to query favorites for
   * @param pageable pagination parameters (page, size, sort)
   * @return page of favorites with collection details for the specified user
   */
  @Query(
      "SELECT cf FROM CollectionFavorite cf "
          + "JOIN FETCH cf.collection c "
          + "WHERE cf.id.userId = :userId "
          + "ORDER BY cf.favoritedAt DESC")
  Page<CollectionFavorite> findByUserIdWithCollection(
      @Param("userId") UUID userId, Pageable pageable);

  /**
   * Check if a user has favorited a specific collection.
   *
   * @param userId the user ID to check
   * @param collectionId the collection ID to check
   * @return true if the user has favorited the collection, false otherwise
   */
  boolean existsByIdUserIdAndIdCollectionId(UUID userId, Long collectionId);

  /**
   * Find a specific favorite by user ID and collection ID.
   *
   * @param userId the user ID
   * @param collectionId the collection ID
   * @return Optional containing the favorite if found, empty otherwise
   */
  Optional<CollectionFavorite> findByIdUserIdAndIdCollectionId(UUID userId, Long collectionId);

  /**
   * Delete a favorite by user ID and collection ID.
   *
   * <p>This is a modifying query that requires a transaction context.
   *
   * @param userId the user ID
   * @param collectionId the collection ID
   */
  void deleteByIdUserIdAndIdCollectionId(UUID userId, Long collectionId);

  /**
   * Count the total number of favorites for a user.
   *
   * @param userId the user ID
   * @return the count of favorites for the user
   */
  long countByIdUserId(UUID userId);

  /**
   * Count the total number of users who favorited a specific collection.
   *
   * @param collectionId the collection ID
   * @return the count of users who favorited the collection
   */
  long countByIdCollectionId(Long collectionId);
}
