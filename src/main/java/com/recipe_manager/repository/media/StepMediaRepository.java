package com.recipe_manager.repository.media;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.media.StepMedia;
import com.recipe_manager.model.entity.media.StepMediaId;

/**
 * Repository interface for StepMedia entity. Provides data access methods for step-media
 * association operations.
 */
@Repository
public interface StepMediaRepository extends JpaRepository<StepMedia, StepMediaId> {

  /**
   * Find all media associations for a specific step.
   *
   * @param stepId the step ID
   * @return list of step-media associations for the step
   */
  List<StepMedia> findByIdStepId(Long stepId);

  /**
   * Find all step associations for a specific media item.
   *
   * @param mediaId the media ID
   * @return list of step-media associations for the media
   */
  List<StepMedia> findByIdMediaId(Long mediaId);

  /**
   * Find all step media associations for a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return list of step-media associations for all steps in the recipe
   */
  List<StepMedia> findByRecipeRecipeId(Long recipeId);

  /**
   * Find all step media associations for multiple steps.
   *
   * @param stepIds the list of step IDs
   * @return list of step-media associations for the steps
   */
  List<StepMedia> findByIdStepIdIn(List<Long> stepIds);

  /**
   * Find all step media associations for multiple recipes.
   *
   * @param recipeIds the list of recipe IDs
   * @return list of step-media associations for all steps in the recipes
   */
  List<StepMedia> findByRecipeRecipeIdIn(List<Long> recipeIds);

  /**
   * Delete all media associations for a specific step.
   *
   * @param stepId the step ID
   */
  void deleteByIdStepId(Long stepId);

  /**
   * Delete all step associations for a specific media item.
   *
   * @param mediaId the media ID
   */
  void deleteByIdMediaId(Long mediaId);

  /**
   * Delete all step media associations for a specific recipe.
   *
   * @param recipeId the recipe ID
   */
  void deleteByRecipeRecipeId(Long recipeId);

  /**
   * Count the number of media items associated with a step.
   *
   * @param stepId the step ID
   * @return the count of media items associated with the step
   */
  long countByIdStepId(Long stepId);

  /**
   * Count the number of step associations for a specific media item.
   *
   * @param mediaId the media ID
   * @return the count of step associations for the media
   */
  long countByIdMediaId(Long mediaId);

  /**
   * Count the number of step media associations in a recipe.
   *
   * @param recipeId the recipe ID
   * @return the count of step media associations for all steps in the recipe
   */
  long countByRecipeRecipeId(Long recipeId);

  /**
   * Check if a step has any associated media.
   *
   * @param stepId the step ID
   * @return true if the step has associated media, false otherwise
   */
  boolean existsByIdStepId(Long stepId);

  /**
   * Check if a media item is associated with any steps.
   *
   * @param mediaId the media ID
   * @return true if the media is associated with steps, false otherwise
   */
  boolean existsByIdMediaId(Long mediaId);

  /**
   * Check if a recipe has any step media associations.
   *
   * @param recipeId the recipe ID
   * @return true if the recipe has step media associations, false otherwise
   */
  boolean existsByRecipeRecipeId(Long recipeId);
}
