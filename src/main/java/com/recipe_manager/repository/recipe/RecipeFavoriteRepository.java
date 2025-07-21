package com.recipe_manager.repository.recipe;

import java.util.List;
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
 * Repository interface for RecipeFavorite entity. Provides data access methods for recipe favorite
 * operations.
 */
@Repository
public interface RecipeFavoriteRepository extends JpaRepository<RecipeFavorite, RecipeFavoriteId> {

  /**
   * Find all favorite recipes for a user.
   *
   * @param userId the user ID
   * @param pageable pagination information
   * @return page of favorite recipes for the user
   */
  Page<RecipeFavorite> findByUserId(UUID userId, Pageable pageable);

  /**
   * Find a specific favorite recipe for a user.
   *
   * @param userId the user ID
   * @param recipeId the recipe ID
   * @return optional containing the favorite if found
   */
  Optional<RecipeFavorite> findByUserIdAndRecipeId(UUID userId, Long recipeId);

  /**
   * Check if a recipe is favorited by a user.
   *
   * @param userId the user ID
   * @param recipeId the recipe ID
   * @return true if the recipe is favorited by the user, false otherwise
   */
  boolean existsByUserIdAndRecipeId(UUID userId, Long recipeId);

  /**
   * Count favorites for a recipe.
   *
   * @param recipeId the recipe ID
   * @return the number of favorites for the recipe
   */
  long countByRecipeId(Long recipeId);

  /**
   * Count favorites for a user.
   *
   * @param userId the user ID
   * @return the number of favorites for the user
   */
  long countByUserId(UUID userId);

  /**
   * Find all recipe IDs favorited by a user.
   *
   * @param userId the user ID
   * @return list of recipe IDs favorited by the user
   */
  @Query("SELECT rf.recipeId FROM RecipeFavorite rf WHERE rf.userId = :userId")
  List<Long> findRecipeIdsByUserId(@Param("userId") UUID userId);

  /**
   * Find all user IDs who favorited a recipe.
   *
   * @param recipeId the recipe ID
   * @return list of user IDs who favorited the recipe
   */
  @Query("SELECT rf.userId FROM RecipeFavorite rf WHERE rf.recipeId = :recipeId")
  List<UUID> findUserIdsByRecipeId(@Param("recipeId") Long recipeId);

  /**
   * Find favorite recipes with recipe details for a user.
   *
   * @param userId the user ID
   * @param pageable pagination information
   * @return page of favorite recipes with details for the user
   */
  @Query(
      "SELECT rf FROM RecipeFavorite rf " + "JOIN FETCH rf.recipe " + "WHERE rf.userId = :userId")
  Page<RecipeFavorite> findByUserIdWithRecipeDetails(
      @Param("userId") UUID userId, Pageable pageable);
}
