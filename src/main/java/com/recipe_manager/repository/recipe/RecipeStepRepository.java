package com.recipe_manager.repository.recipe;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.recipe.RecipeStep;

/** Repository interface for RecipeStep entity operations. */
@Repository
public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {

  /**
   * Find all steps for a specific recipe, ordered by step number.
   *
   * @param recipeId the recipe ID
   * @return list of recipe steps ordered by step number
   */
  List<RecipeStep> findByRecipeRecipeIdOrderByStepNumberAsc(Long recipeId);

  /**
   * Find a specific step by step ID and recipe ID.
   *
   * @param stepId the step ID
   * @param recipeId the recipe ID
   * @return optional recipe step
   */
  Optional<RecipeStep> findByStepIdAndRecipeRecipeId(Long stepId, Long recipeId);

  /**
   * Check if a step exists for a specific recipe and step ID.
   *
   * @param stepId the step ID
   * @param recipeId the recipe ID
   * @return true if step exists, false otherwise
   */
  boolean existsByStepIdAndRecipeRecipeId(Long stepId, Long recipeId);

  /**
   * Count the number of steps in a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return number of steps in the recipe
   */
  long countByRecipeRecipeId(Long recipeId);

  /**
   * Find the maximum step number for a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return optional maximum step number
   */
  Optional<Integer> findMaxStepNumberByRecipeRecipeId(Long recipeId);
}
