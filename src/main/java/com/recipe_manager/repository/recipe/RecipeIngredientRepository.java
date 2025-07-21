package com.recipe_manager.repository.recipe;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.recipe.RecipeIngredient;
import com.recipe_manager.model.entity.recipe.RecipeIngredientId;

/**
 * Repository interface for RecipeIngredient entity. Provides data access methods for recipe
 * ingredient operations.
 */
@Repository
public interface RecipeIngredientRepository
    extends JpaRepository<RecipeIngredient, RecipeIngredientId> {

  /**
   * Find all recipe ingredients by recipe ID.
   *
   * @param recipeId the recipe ID
   * @return list of recipe ingredients for the recipe
   */
  @Query(
      "SELECT ri FROM RecipeIngredient ri "
          + "JOIN FETCH ri.ingredient "
          + "WHERE ri.recipe.recipeId = :recipeId "
          + "ORDER BY ri.ingredient.name")
  List<RecipeIngredient> findByRecipeId(@Param("recipeId") Long recipeId);

  /**
   * Find all recipe ingredients by ingredient ID.
   *
   * @param ingredientId the ingredient ID
   * @return list of recipe ingredients for the ingredient
   */
  @Query(
      "SELECT ri FROM RecipeIngredient ri "
          + "JOIN FETCH ri.recipe "
          + "WHERE ri.ingredient.ingredientId = :ingredientId")
  List<RecipeIngredient> findByIngredientId(@Param("ingredientId") Long ingredientId);

  /**
   * Delete all recipe ingredients by recipe ID.
   *
   * @param recipeId the recipe ID
   */
  void deleteByRecipeId(Long recipeId);

  /**
   * Count recipe ingredients by recipe ID.
   *
   * @param recipeId the recipe ID
   * @return the number of ingredients for the recipe
   */
  long countByRecipeId(Long recipeId);

  /**
   * Find recipe ingredients by recipe ID and optional flag.
   *
   * @param recipeId the recipe ID
   * @param isOptional the optional flag
   * @return list of recipe ingredients matching the criteria
   */
  List<RecipeIngredient> findByRecipeIdAndIsOptional(Long recipeId, Boolean isOptional);

  /**
   * Find recipe ingredients by ingredient name containing the given text (case-insensitive).
   *
   * @param ingredientName the ingredient name text to search for
   * @return list of recipe ingredients matching the ingredient name
   */
  @Query(
      "SELECT ri FROM RecipeIngredient ri "
          + "JOIN FETCH ri.ingredient i "
          + "WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :ingredientName, '%'))")
  List<RecipeIngredient> findByIngredientNameContainingIgnoreCase(
      @Param("ingredientName") String ingredientName);
}
