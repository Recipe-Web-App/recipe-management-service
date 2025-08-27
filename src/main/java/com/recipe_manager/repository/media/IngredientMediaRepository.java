package com.recipe_manager.repository.media;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.media.IngredientMedia;
import com.recipe_manager.model.entity.media.IngredientMediaId;

/**
 * Repository interface for IngredientMedia entity. Provides data access methods for
 * ingredient-media association operations.
 */
@Repository
public interface IngredientMediaRepository
    extends JpaRepository<IngredientMedia, IngredientMediaId> {

  /**
   * Find all ingredient media associations for a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return list of ingredient-media associations for the recipe
   */
  List<IngredientMedia> findByIdRecipeId(Long recipeId);

  /**
   * Find all media associations for a specific ingredient across all recipes.
   *
   * @param ingredientId the ingredient ID
   * @return list of ingredient-media associations for the ingredient
   */
  List<IngredientMedia> findByIdIngredientId(Long ingredientId);

  /**
   * Find all ingredient associations for a specific media item.
   *
   * @param mediaId the media ID
   * @return list of ingredient-media associations for the media
   */
  List<IngredientMedia> findByIdMediaId(Long mediaId);

  /**
   * Find all media associations for a specific ingredient in a specific recipe.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @return list of media associated with the specific recipe ingredient
   */
  List<IngredientMedia> findByIdRecipeIdAndIdIngredientId(Long recipeId, Long ingredientId);

  /**
   * Find all ingredient media associations for multiple recipes.
   *
   * @param recipeIds the list of recipe IDs
   * @return list of ingredient-media associations for the recipes
   */
  List<IngredientMedia> findByIdRecipeIdIn(List<Long> recipeIds);

  /**
   * Delete all ingredient media associations for a specific recipe.
   *
   * @param recipeId the recipe ID
   */
  void deleteByIdRecipeId(Long recipeId);

  /**
   * Delete all media associations for a specific ingredient across all recipes.
   *
   * @param ingredientId the ingredient ID
   */
  void deleteByIdIngredientId(Long ingredientId);

  /**
   * Delete all ingredient associations for a specific media item.
   *
   * @param mediaId the media ID
   */
  void deleteByIdMediaId(Long mediaId);

  /**
   * Delete all media associations for a specific ingredient in a specific recipe.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   */
  void deleteByIdRecipeIdAndIdIngredientId(Long recipeId, Long ingredientId);

  /**
   * Count the number of media items associated with ingredients in a recipe.
   *
   * @param recipeId the recipe ID
   * @return the count of ingredient media items in the recipe
   */
  long countByIdRecipeId(Long recipeId);

  /**
   * Count the number of media items associated with a specific ingredient.
   *
   * @param ingredientId the ingredient ID
   * @return the count of media items associated with the ingredient
   */
  long countByIdIngredientId(Long ingredientId);

  /**
   * Count the number of ingredient associations for a specific media item.
   *
   * @param mediaId the media ID
   * @return the count of ingredient associations for the media
   */
  long countByIdMediaId(Long mediaId);

  /**
   * Check if a recipe has any ingredient media associations.
   *
   * @param recipeId the recipe ID
   * @return true if the recipe has ingredient media, false otherwise
   */
  boolean existsByIdRecipeId(Long recipeId);

  /**
   * Check if an ingredient has any media associations.
   *
   * @param ingredientId the ingredient ID
   * @return true if the ingredient has associated media, false otherwise
   */
  boolean existsByIdIngredientId(Long ingredientId);

  /**
   * Check if a media item is associated with any ingredients.
   *
   * @param mediaId the media ID
   * @return true if the media is associated with ingredients, false otherwise
   */
  boolean existsByIdMediaId(Long mediaId);
}
