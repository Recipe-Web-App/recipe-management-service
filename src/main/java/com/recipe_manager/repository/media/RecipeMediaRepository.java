package com.recipe_manager.repository.media;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.media.RecipeMedia;
import com.recipe_manager.model.entity.media.RecipeMediaId;

/**
 * Repository interface for RecipeMedia entity. Provides data access methods for recipe-media
 * association operations.
 */
@Repository
public interface RecipeMediaRepository extends JpaRepository<RecipeMedia, RecipeMediaId> {

  /**
   * Find all media associations for a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return list of recipe-media associations for the recipe
   */
  List<RecipeMedia> findByRecipeId(Long recipeId);

  /**
   * Find all recipe associations for a specific media item.
   *
   * @param mediaId the media ID
   * @return list of recipe-media associations for the media
   */
  List<RecipeMedia> findByMediaId(Long mediaId);

  /**
   * Delete all media associations for a specific recipe.
   *
   * @param recipeId the recipe ID
   */
  void deleteByRecipeId(Long recipeId);

  /**
   * Delete all recipe associations for a specific media item.
   *
   * @param mediaId the media ID
   */
  void deleteByMediaId(Long mediaId);

  /**
   * Count the number of media items associated with a recipe.
   *
   * @param recipeId the recipe ID
   * @return the count of media items associated with the recipe
   */
  long countByRecipeId(Long recipeId);

  /**
   * Count the number of recipes associated with a media item.
   *
   * @param mediaId the media ID
   * @return the count of recipes associated with the media
   */
  long countByMediaId(Long mediaId);

  /**
   * Check if a recipe has any associated media.
   *
   * @param recipeId the recipe ID
   * @return true if the recipe has associated media, false otherwise
   */
  boolean existsByRecipeId(Long recipeId);

  /**
   * Check if a media item is associated with any recipes.
   *
   * @param mediaId the media ID
   * @return true if the media is associated with recipes, false otherwise
   */
  boolean existsByMediaId(Long mediaId);
}
