package com.recipe_manager.repository.collection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.collection.RecipeCollection;
import com.recipe_manager.model.enums.CollectionVisibility;

/** Repository interface for RecipeCollection entity operations. */
@Repository
public interface RecipeCollectionRepository extends JpaRepository<RecipeCollection, Long> {

  /**
   * Finds all collections owned by a specific user.
   *
   * @param userId the user ID
   * @return list of collections owned by the user
   */
  List<RecipeCollection> findByUserId(UUID userId);

  /**
   * Finds all collections owned by a specific user, ordered by creation date descending.
   *
   * @param userId the user ID
   * @return list of collections owned by the user, newest first
   */
  List<RecipeCollection> findByUserIdOrderByCreatedAtDesc(UUID userId);

  /**
   * Checks if a collection exists and is owned by a specific user.
   *
   * @param collectionId the collection ID
   * @param userId the user ID
   * @return true if collection exists and is owned by user
   */
  boolean existsByCollectionIdAndUserId(Long collectionId, UUID userId);

  /**
   * Finds a collection by ID and user ID.
   *
   * @param collectionId the collection ID
   * @param userId the user ID
   * @return optional containing the collection if found and owned by user
   */
  Optional<RecipeCollection> findByCollectionIdAndUserId(Long collectionId, UUID userId);

  /**
   * Finds all collections with a specific visibility.
   *
   * @param visibility the visibility type
   * @return list of collections with the specified visibility
   */
  List<RecipeCollection> findByVisibility(CollectionVisibility visibility);

  /**
   * Finds all collections with a specific visibility, paginated.
   *
   * @param visibility the visibility type
   * @param pageable pagination information
   * @return page of collections with the specified visibility
   */
  Page<RecipeCollection> findByVisibility(CollectionVisibility visibility, Pageable pageable);

  /**
   * Searches collections with advanced filtering. Supports filtering by visibility, collaboration
   * mode, owner, and recipe count ranges. All parameters are optional (null means no filter for
   * that criterion).
   *
   * @param visibilityList list of visibility types to include (null = no filter)
   * @param collaborationModeList list of collaboration modes to include (null = no filter)
   * @param ownerUserId filter by owner user ID (null = no filter)
   * @param minRecipeCount minimum recipe count (null = no filter)
   * @param maxRecipeCount maximum recipe count (null = no filter)
   * @param pageable pagination information
   * @return page of matching collections
   */
  @Query(
      value =
          "SELECT DISTINCT c.* FROM recipe_manager.recipe_collections c "
              + "LEFT JOIN recipe_manager.recipe_collection_items rci ON c.collection_id ="
              + " rci.collection_id "
              + "WHERE (:#{#visibilityList == null} = true OR c.visibility::text = ANY(CAST(:visibilityList AS"
              + " text[]))) "
              + "AND (:#{#collaborationModeList == null} = true OR c.collaboration_mode::text = ANY(CAST(:collaborationModeList AS"
              + " text[]))) "
              + "AND (:ownerUserId IS NULL OR c.user_id = :ownerUserId) "
              + "GROUP BY c.collection_id "
              + "HAVING (:minRecipeCount IS NULL OR COUNT(rci.recipe_id) >= :minRecipeCount) "
              + "AND (:maxRecipeCount IS NULL OR COUNT(rci.recipe_id) <= :maxRecipeCount) "
              + "ORDER BY c.created_at DESC",
      countQuery =
          "SELECT COUNT(DISTINCT c.collection_id) FROM recipe_manager.recipe_collections c "
              + "LEFT JOIN recipe_manager.recipe_collection_items rci ON c.collection_id ="
              + " rci.collection_id "
              + "WHERE (:#{#visibilityList == null} = true OR c.visibility::text = ANY(CAST(:visibilityList AS"
              + " text[]))) "
              + "AND (:#{#collaborationModeList == null} = true OR c.collaboration_mode::text = ANY(CAST(:collaborationModeList AS"
              + " text[]))) "
              + "AND (:ownerUserId IS NULL OR c.user_id = :ownerUserId) "
              + "GROUP BY c.collection_id "
              + "HAVING (:minRecipeCount IS NULL OR COUNT(rci.recipe_id) >= :minRecipeCount) "
              + "AND (:maxRecipeCount IS NULL OR COUNT(rci.recipe_id) <= :maxRecipeCount)",
      nativeQuery = true)
  Page<RecipeCollection> searchCollections(
      @Param("visibilityList") String[] visibilityList,
      @Param("collaborationModeList") String[] collaborationModeList,
      @Param("ownerUserId") UUID ownerUserId,
      @Param("minRecipeCount") Integer minRecipeCount,
      @Param("maxRecipeCount") Integer maxRecipeCount,
      Pageable pageable);

  /**
   * Finds all collections accessible by a specific user using database views. This includes
   * collections the user owns, collaborates on, and public collections based on the
   * vw_user_collection_access view.
   *
   * @param userId the user ID to check access for
   * @param pageable pagination information
   * @return page of collection summaries accessible by the user
   */
  @Query(
      value =
          "SELECT collection_id, name, description, visibility, collaboration_mode, "
              + "owner_id, recipe_count, collaborator_count, created_at, updated_at "
              + "FROM recipe_manager.vw_collection_summary "
              + "WHERE collection_id IN ( "
              + "  SELECT collection_id FROM recipe_manager.vw_user_collection_access "
              + "  WHERE accessor_user_id = :userId "
              + ")",
      nativeQuery = true)
  Page<CollectionSummaryProjection> findAccessibleCollections(
      @Param("userId") UUID userId, Pageable pageable);

  /**
   * Checks if a user has view access to a specific collection. Uses the vw_user_collection_access
   * view which handles all permission logic (owner, collaborator, public, friends).
   *
   * @param collectionId the collection ID to check access for
   * @param userId the user ID to check access for
   * @return true if user has view access to the collection, false otherwise
   */
  @Query(
      value =
          "SELECT COUNT(*) > 0 FROM recipe_manager.vw_user_collection_access "
              + "WHERE collection_id = :collectionId AND accessor_user_id = :userId",
      nativeQuery = true)
  boolean hasViewAccess(@Param("collectionId") Long collectionId, @Param("userId") UUID userId);

  /**
   * Finds a collection by ID with all collection items eagerly loaded. This method fetches the
   * collection with its recipes in a single query using JOIN FETCH to avoid N+1 queries. Collection
   * items are ordered by display_order ascending.
   *
   * @param collectionId the collection ID
   * @return optional containing the collection with eagerly loaded items, or empty if not found
   */
  @Query(
      "SELECT DISTINCT c FROM RecipeCollection c "
          + "LEFT JOIN FETCH c.collectionItems ci "
          + "LEFT JOIN FETCH ci.recipe "
          + "WHERE c.collectionId = :collectionId "
          + "ORDER BY ci.displayOrder ASC")
  Optional<RecipeCollection> findByIdWithItems(@Param("collectionId") Long collectionId);
}
