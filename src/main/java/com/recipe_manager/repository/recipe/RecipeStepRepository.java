package com.recipe_manager.repository.recipe;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.recipe.RecipeStep;

/**
 * Repository interface for RecipeStep entity. Provides data access methods for recipe step
 * operations.
 */
@Repository
public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {

  /**
   * Find all recipe steps by recipe ID, ordered by step number.
   *
   * @param recipeId the recipe ID
   * @return list of recipe steps for the recipe, ordered by step number
   */
  List<RecipeStep> findByRecipeIdOrderByStepNumber(Long recipeId);

  /**
   * Find recipe steps by recipe ID and optional flag.
   *
   * @param recipeId the recipe ID
   * @param optional the optional flag
   * @return list of recipe steps matching the criteria
   */
  List<RecipeStep> findByRecipeIdAndOptionalOrderByStepNumber(Long recipeId, Boolean optional);

  /**
   * Delete all recipe steps by recipe ID.
   *
   * @param recipeId the recipe ID
   */
  void deleteByRecipeId(Long recipeId);

  /**
   * Count recipe steps by recipe ID.
   *
   * @param recipeId the recipe ID
   * @return the number of steps for the recipe
   */
  long countByRecipeId(Long recipeId);

  /**
   * Find the maximum step number for a recipe.
   *
   * @param recipeId the recipe ID
   * @return the maximum step number, or null if no steps exist
   */
  @Query("SELECT MAX(rs.stepNumber) FROM RecipeStep rs WHERE rs.recipe.recipeId = :recipeId")
  Integer findMaxStepNumberByRecipeId(@Param("recipeId") Long recipeId);

  /**
   * Find recipe steps by instruction containing the given text (case-insensitive).
   *
   * @param instruction the instruction text to search for
   * @return list of recipe steps matching the instruction
   */
  List<RecipeStep> findByInstructionContainingIgnoreCase(String instruction);

  /**
   * Find recipe steps with timer by recipe ID.
   *
   * @param recipeId the recipe ID
   * @return list of recipe steps with timer for the recipe
   */
  List<RecipeStep> findByRecipeIdAndTimerSecondsIsNotNullOrderByStepNumber(Long recipeId);
}
