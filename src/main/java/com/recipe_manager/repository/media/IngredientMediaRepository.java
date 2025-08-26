package com.recipe_manager.repository.media;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.media.IngredientMedia;
import com.recipe_manager.model.entity.media.IngredientMediaId;

/**
 * Repository interface for IngredientMedia entity. Provides data access methods for
 * ingredient-media relationship operations.
 */
@Repository
public interface IngredientMediaRepository
    extends JpaRepository<IngredientMedia, IngredientMediaId> {

  /**
   * Find all media for ingredients in a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return list of ingredient media relationships
   */
  List<IngredientMedia> findByRecipeRecipeId(Long recipeId);

  /**
   * Find all media for a specific ingredient in a recipe.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @return list of ingredient media relationships
   */
  List<IngredientMedia> findByRecipeRecipeIdAndIngredientIngredientId(
      Long recipeId, Long ingredientId);

  /**
   * Find all ingredients that use a specific media.
   *
   * @param mediaId the media ID
   * @return list of ingredient media relationships
   */
  List<IngredientMedia> findByMediaMediaId(Long mediaId);

  /**
   * Find a specific ingredient-media relationship.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @param mediaId the media ID
   * @return optional ingredient media relationship
   */
  Optional<IngredientMedia> findByRecipeRecipeIdAndIngredientIngredientIdAndMediaMediaId(
      Long recipeId, Long ingredientId, Long mediaId);

  /**
   * Check if a specific ingredient-media relationship exists.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @param mediaId the media ID
   * @return true if relationship exists, false otherwise
   */
  boolean existsByRecipeRecipeIdAndIngredientIngredientIdAndMediaMediaId(
      Long recipeId, Long ingredientId, Long mediaId);

  /**
   * Count media items for a specific ingredient in a recipe.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @return number of media items for the ingredient
   */
  long countByRecipeRecipeIdAndIngredientIngredientId(Long recipeId, Long ingredientId);

  /**
   * Count ingredients using a specific media.
   *
   * @param mediaId the media ID
   * @return number of ingredients using the media
   */
  long countByMediaMediaId(Long mediaId);

  /**
   * Find all media for ingredients in recipes owned by a specific user.
   *
   * @param userId the user ID
   * @return list of ingredient media relationships for the user's recipes
   */
  @Query("SELECT im FROM IngredientMedia im WHERE im.recipe.userId = :userId")
  List<IngredientMedia> findByRecipeUserId(@Param("userId") java.util.UUID userId);

  /**
   * Delete all media relationships for ingredients in a specific recipe.
   *
   * @param recipeId the recipe ID
   */
  void deleteByRecipeRecipeId(Long recipeId);

  /**
   * Delete all ingredient relationships for a specific media.
   *
   * @param mediaId the media ID
   */
  void deleteByMediaMediaId(Long mediaId);

  /**
   * Delete all media relationships for a specific ingredient in a recipe.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   */
  void deleteByRecipeRecipeIdAndIngredientIngredientId(Long recipeId, Long ingredientId);
}
