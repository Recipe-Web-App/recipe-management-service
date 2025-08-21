package com.recipe_manager.repository.recipe;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.recipe.RecipeRevision;

/** Repository for managing recipe revision data access. */
@Repository
public interface RecipeRevisionRepository extends JpaRepository<RecipeRevision, Long> {

  /**
   * Find all revisions for a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return list of revisions for the recipe
   */
  @Query(
      "SELECT r FROM RecipeRevision r WHERE r.recipe.recipeId = :recipeId ORDER BY r.createdAt DESC")
  List<RecipeRevision> findByRecipeId(@Param("recipeId") Long recipeId);

  /**
   * Find all revisions made by a specific user.
   *
   * @param userId the user ID
   * @return list of revisions made by the user
   */
  @Query("SELECT r FROM RecipeRevision r WHERE r.userId = :userId ORDER BY r.createdAt DESC")
  List<RecipeRevision> findByUserId(@Param("userId") UUID userId);

  /**
   * Find all revisions for a specific recipe made by a specific user.
   *
   * @param recipeId the recipe ID
   * @param userId the user ID
   * @return list of revisions for the recipe made by the user
   */
  @Query(
      "SELECT r FROM RecipeRevision r WHERE r.recipe.recipeId = :recipeId AND r.userId = :userId ORDER BY r.createdAt DESC")
  List<RecipeRevision> findByRecipeIdAndUserId(
      @Param("recipeId") Long recipeId, @Param("userId") UUID userId);

  /**
   * Find all step revisions for a specific recipe and step.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   * @return list of step revisions for the recipe and step
   */
  @Query(
      value =
          "SELECT * FROM recipe_manager.recipe_revisions r WHERE r.recipe_id = :recipeId "
              + "AND r.revision_category = 'STEP' "
              + "AND (CAST(r.previous_data->>'stepId' AS bigint) = :stepId "
              + "OR CAST(r.new_data->>'stepId' AS bigint) = :stepId) "
              + "ORDER BY r.created_at DESC",
      nativeQuery = true)
  List<RecipeRevision> findStepRevisionsByRecipeIdAndStepId(
      @Param("recipeId") Long recipeId, @Param("stepId") Long stepId);

  /**
   * Find all ingredient revisions for a specific recipe and ingredient.
   *
   * @param recipeId the recipe ID
   * @param ingredientId the ingredient ID
   * @return list of ingredient revisions for the recipe and ingredient
   */
  @Query(
      value =
          "SELECT * FROM recipe_manager.recipe_revisions r WHERE r.recipe_id = :recipeId "
              + "AND r.revision_category = 'INGREDIENT' "
              + "AND (CAST(r.previous_data->>'ingredientId' AS bigint) = :ingredientId "
              + "OR CAST(r.new_data->>'ingredientId' AS bigint) = :ingredientId) "
              + "ORDER BY r.created_at DESC",
      nativeQuery = true)
  List<RecipeRevision> findIngredientRevisionsByRecipeIdAndIngredientId(
      @Param("recipeId") Long recipeId, @Param("ingredientId") Long ingredientId);
}
