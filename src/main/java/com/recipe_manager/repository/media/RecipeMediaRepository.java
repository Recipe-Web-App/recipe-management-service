package com.recipe_manager.repository.media;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.media.RecipeMedia;
import com.recipe_manager.model.entity.media.RecipeMediaId;

/**
 * Repository interface for RecipeMedia entity. Provides data access methods for recipe-media
 * relationship operations.
 */
@Repository
public interface RecipeMediaRepository extends JpaRepository<RecipeMedia, RecipeMediaId> {

  /**
   * Find all media for a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return list of recipe media relationships
   */
  List<RecipeMedia> findByRecipeRecipeId(Long recipeId);

  /**
   * Find all recipes that use a specific media.
   *
   * @param mediaId the media ID
   * @return list of recipe media relationships
   */
  List<RecipeMedia> findByMediaMediaId(Long mediaId);

  /**
   * Find a specific recipe-media relationship.
   *
   * @param recipeId the recipe ID
   * @param mediaId the media ID
   * @return optional recipe media relationship
   */
  Optional<RecipeMedia> findByRecipeRecipeIdAndMediaMediaId(Long recipeId, Long mediaId);

  /**
   * Check if a specific recipe-media relationship exists.
   *
   * @param recipeId the recipe ID
   * @param mediaId the media ID
   * @return true if relationship exists, false otherwise
   */
  boolean existsByRecipeRecipeIdAndMediaMediaId(Long recipeId, Long mediaId);

  /**
   * Count media items for a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return number of media items for the recipe
   */
  long countByRecipeRecipeId(Long recipeId);

  /**
   * Count recipes using a specific media.
   *
   * @param mediaId the media ID
   * @return number of recipes using the media
   */
  long countByMediaMediaId(Long mediaId);

  /**
   * Find all media for recipes owned by a specific user.
   *
   * @param userId the user ID
   * @return list of recipe media relationships for the user's recipes
   */
  @Query("SELECT rm FROM RecipeMedia rm WHERE rm.recipe.userId = :userId")
  List<RecipeMedia> findByRecipeUserId(@Param("userId") java.util.UUID userId);

  /**
   * Delete all media relationships for a specific recipe.
   *
   * @param recipeId the recipe ID
   */
  void deleteByRecipeRecipeId(Long recipeId);

  /**
   * Delete all recipe relationships for a specific media.
   *
   * @param mediaId the media ID
   */
  void deleteByMediaMediaId(Long mediaId);
}
