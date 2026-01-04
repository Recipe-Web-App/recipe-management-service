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
   * Searches collections with advanced filtering. Supports filtering by text query (searches name
   * and description), visibility, collaboration mode, owner, and recipe count ranges. All
   * parameters are optional (null or empty means no filter for that criterion).
   *
   * @param searchQuery text to search in collection name or description (null = no filter)
   * @param visibilityList list of visibility types to include (null or empty = no filter)
   * @param collaborationModeList list of collaboration modes to include (null or empty = no filter)
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
              + "WHERE (:searchQuery IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchQuery, '%')) "
              + "  OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchQuery, '%'))) "
              + "AND (:#{#visibilityList == null || #visibilityList.length == 0} = true OR c.visibility::text = ANY(CAST(:visibilityList AS"
              + " text[]))) "
              + "AND (:#{#collaborationModeList == null || #collaborationModeList.length == 0} = true OR c.collaboration_mode::text = ANY(CAST(:collaborationModeList AS"
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
              + "WHERE (:searchQuery IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchQuery, '%')) "
              + "  OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchQuery, '%'))) "
              + "AND (:#{#visibilityList == null || #visibilityList.length == 0} = true OR c.visibility::text = ANY(CAST(:visibilityList AS"
              + " text[]))) "
              + "AND (:#{#collaborationModeList == null || #collaborationModeList.length == 0} = true OR c.collaboration_mode::text = ANY(CAST(:collaborationModeList AS"
              + " text[]))) "
              + "AND (:ownerUserId IS NULL OR c.user_id = :ownerUserId) "
              + "GROUP BY c.collection_id "
              + "HAVING (:minRecipeCount IS NULL OR COUNT(rci.recipe_id) >= :minRecipeCount) "
              + "AND (:maxRecipeCount IS NULL OR COUNT(rci.recipe_id) <= :maxRecipeCount)",
      nativeQuery = true)
  Page<RecipeCollection> searchCollections(
      @Param("searchQuery") String searchQuery,
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

  /**
   * Finds collections owned by a specific user with pagination. Returns collection summary data
   * including recipe and collaborator counts from the vw_collection_summary view.
   *
   * @param userId the user ID
   * @param pageable pagination parameters
   * @return page of collection summaries owned by the user
   */
  @Query(
      value =
          "SELECT collection_id, name, description, visibility, collaboration_mode, "
              + "owner_id, recipe_count, collaborator_count, created_at, updated_at "
              + "FROM recipe_manager.vw_collection_summary "
              + "WHERE owner_id = :userId",
      countQuery =
          "SELECT COUNT(*) FROM recipe_manager.vw_collection_summary WHERE owner_id = :userId",
      nativeQuery = true)
  Page<CollectionSummaryProjection> findOwnedCollections(
      @Param("userId") UUID userId, Pageable pageable);

  /**
   * Finds collections owned by or where user is a collaborator with pagination. Returns collection
   * summary data including recipe and collaborator counts. Uses vw_collection_summary view joined
   * with collection_collaborators table.
   *
   * @param userId the user ID
   * @param pageable pagination parameters
   * @return page of collection summaries (owned + collaborating)
   */
  @Query(
      value =
          "SELECT DISTINCT cs.collection_id, cs.name, cs.description, cs.visibility, "
              + "cs.collaboration_mode, cs.owner_id, cs.recipe_count, cs.collaborator_count, "
              + "cs.created_at, cs.updated_at "
              + "FROM recipe_manager.vw_collection_summary cs "
              + "LEFT JOIN recipe_manager.collection_collaborators cc "
              + "ON cs.collection_id = cc.collection_id "
              + "WHERE cs.owner_id = :userId OR cc.user_id = :userId",
      countQuery =
          "SELECT COUNT(DISTINCT cs.collection_id) "
              + "FROM recipe_manager.vw_collection_summary cs "
              + "LEFT JOIN recipe_manager.collection_collaborators cc "
              + "ON cs.collection_id = cc.collection_id "
              + "WHERE cs.owner_id = :userId OR cc.user_id = :userId",
      nativeQuery = true)
  Page<CollectionSummaryProjection> findOwnedAndCollaboratingCollections(
      @Param("userId") UUID userId, Pageable pageable);

  /**
   * Find trending collections accessible to the user based on a time-decayed scoring algorithm.
   * Returns at most 100 trending collections, which can then be paginated by the client. Score =
   * Sum(Weight * exp(-DecayRate * Age)) Weights: Favorites=3.0, RecipeAdds=4.0 DecayRate: 0.23
   * (half-life of ~3 days)
   *
   * @param userId the user requesting trending collections
   * @param pageable pagination information (within the top 100)
   * @return page of trending collections the user can access (max 100 total)
   */
  @Query(
      value =
          "WITH top_trending AS ("
              + "  SELECT c.collection_id, c.user_id, c.name, c.description, c.visibility, "
              + "    c.collaboration_mode, c.created_at, c.updated_at, "
              + "    (COALESCE(fav.fav_score, 0) + COALESCE(adds.add_score, 0)) as trending_score "
              + "  FROM recipe_manager.recipe_collections c "
              + "  INNER JOIN recipe_manager.vw_user_collection_access vca "
              + "    ON c.collection_id = vca.collection_id AND vca.accessor_user_id = :userId "
              + "  LEFT JOIN ("
              + "    SELECT cf.collection_id, "
              + "      SUM(3.0 * EXP(-0.23 * EXTRACT(EPOCH FROM (NOW() - cf.favorited_at)) / 86400))"
              + "        as fav_score "
              + "    FROM recipe_manager.collection_favorites cf "
              + "    WHERE cf.favorited_at > NOW() - INTERVAL '30 days' "
              + "    GROUP BY cf.collection_id"
              + "  ) fav ON c.collection_id = fav.collection_id "
              + "  LEFT JOIN ("
              + "    SELECT rci.collection_id, "
              + "      SUM(4.0 * EXP(-0.23 * EXTRACT(EPOCH FROM (NOW() - rci.added_at)) / 86400))"
              + "        as add_score "
              + "    FROM recipe_manager.recipe_collection_items rci "
              + "    WHERE rci.added_at > NOW() - INTERVAL '30 days' "
              + "    GROUP BY rci.collection_id"
              + "  ) adds ON c.collection_id = adds.collection_id "
              + "  ORDER BY trending_score DESC, c.created_at DESC "
              + "  LIMIT 100"
              + ") "
              + "SELECT collection_id, user_id, name, description, visibility, "
              + "  collaboration_mode, created_at, updated_at "
              + "FROM top_trending "
              + "ORDER BY trending_score DESC, created_at DESC",
      countQuery =
          "SELECT LEAST(COUNT(*), 100) "
              + "FROM recipe_manager.recipe_collections c "
              + "INNER JOIN recipe_manager.vw_user_collection_access vca "
              + "  ON c.collection_id = vca.collection_id AND vca.accessor_user_id = :userId",
      nativeQuery = true)
  Page<RecipeCollection> findTrendingCollections(@Param("userId") UUID userId, Pageable pageable);
}
