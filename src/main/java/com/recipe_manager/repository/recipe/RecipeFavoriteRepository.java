package com.recipe_manager.repository.recipe;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.recipe.RecipeFavorite;
import com.recipe_manager.model.entity.recipe.RecipeFavoriteId;

/**
 * Repository interface for RecipeFavorite entity operations.
 *
 * <p>Provides database access methods for managing recipe favorites, including querying by user,
 * checking favorite status, and managing favorite relationships. Uses composite key pattern with
 * RecipeFavoriteId (userId + recipeId).
 *
 * <p>Key query patterns:
 *
 * <ul>
 *   <li>Access composite key fields using {@code id.userId} and {@code id.recipeId} convention
 *   <li>Use {@code JOIN FETCH} in custom queries to avoid N+1 query problems
 *   <li>Support pagination for list operations
 * </ul>
 */
@Repository
public interface RecipeFavoriteRepository extends JpaRepository<RecipeFavorite, RecipeFavoriteId> {

  /**
   * Find all favorites for a user with pagination support.
   *
   * @param userId the user ID to query favorites for
   * @param pageable pagination parameters (page, size, sort)
   * @return page of favorites for the specified user
   */
  Page<RecipeFavorite> findByIdUserId(UUID userId, Pageable pageable);

  /**
   * Find all favorites for a user with recipe details eagerly loaded.
   *
   * <p>Uses JOIN FETCH to load recipe data in a single query, avoiding N+1 query issues. Results
   * are ordered by favorited date descending (most recent first).
   *
   * @param userId the user ID to query favorites for
   * @param pageable pagination parameters (page, size, sort)
   * @return page of favorites with recipe details for the specified user
   */
  @Query(
      "SELECT rf FROM RecipeFavorite rf "
          + "JOIN FETCH rf.recipe r "
          + "WHERE rf.id.userId = :userId "
          + "ORDER BY rf.favoritedAt DESC")
  Page<RecipeFavorite> findByUserIdWithRecipe(@Param("userId") UUID userId, Pageable pageable);

  /**
   * Check if a user has favorited a specific recipe.
   *
   * @param userId the user ID to check
   * @param recipeId the recipe ID to check
   * @return true if the user has favorited the recipe, false otherwise
   */
  boolean existsByIdUserIdAndIdRecipeId(UUID userId, Long recipeId);

  /**
   * Find a specific favorite by user ID and recipe ID.
   *
   * @param userId the user ID
   * @param recipeId the recipe ID
   * @return Optional containing the favorite if found, empty otherwise
   */
  Optional<RecipeFavorite> findByIdUserIdAndIdRecipeId(UUID userId, Long recipeId);

  /**
   * Delete a favorite by user ID and recipe ID.
   *
   * <p>This is a modifying query that requires a transaction context.
   *
   * @param userId the user ID
   * @param recipeId the recipe ID
   */
  void deleteByIdUserIdAndIdRecipeId(UUID userId, Long recipeId);

  /**
   * Count the total number of favorites for a user.
   *
   * @param userId the user ID
   * @return the count of favorites for the user
   */
  long countByIdUserId(UUID userId);

  /**
   * Count the total number of users who favorited a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return the count of users who favorited the recipe
   */
  long countByIdRecipeId(Long recipeId);
}
