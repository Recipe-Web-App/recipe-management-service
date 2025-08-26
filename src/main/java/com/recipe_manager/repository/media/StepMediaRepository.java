package com.recipe_manager.repository.media;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.media.StepMedia;
import com.recipe_manager.model.entity.media.StepMediaId;

/**
 * Repository interface for StepMedia entity. Provides data access methods for step-media
 * relationship operations.
 */
@Repository
public interface StepMediaRepository extends JpaRepository<StepMedia, StepMediaId> {

  /**
   * Find all media for steps in a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return list of step media relationships
   */
  List<StepMedia> findByRecipeRecipeId(Long recipeId);

  /**
   * Find all media for a specific step.
   *
   * @param stepId the step ID
   * @return list of step media relationships
   */
  List<StepMedia> findByStepStepId(Long stepId);

  /**
   * Find all media for a specific step in a recipe.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   * @return list of step media relationships
   */
  List<StepMedia> findByRecipeRecipeIdAndStepStepId(Long recipeId, Long stepId);

  /**
   * Find all steps that use a specific media.
   *
   * @param mediaId the media ID
   * @return list of step media relationships
   */
  List<StepMedia> findByMediaMediaId(Long mediaId);

  /**
   * Find a specific step-media relationship.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   * @param mediaId the media ID
   * @return optional step media relationship
   */
  Optional<StepMedia> findByRecipeRecipeIdAndStepStepIdAndMediaMediaId(
      Long recipeId, Long stepId, Long mediaId);

  /**
   * Check if a specific step-media relationship exists.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   * @param mediaId the media ID
   * @return true if relationship exists, false otherwise
   */
  boolean existsByRecipeRecipeIdAndStepStepIdAndMediaMediaId(
      Long recipeId, Long stepId, Long mediaId);

  /**
   * Count media items for a specific step.
   *
   * @param stepId the step ID
   * @return number of media items for the step
   */
  long countByStepStepId(Long stepId);

  /**
   * Count steps using a specific media.
   *
   * @param mediaId the media ID
   * @return number of steps using the media
   */
  long countByMediaMediaId(Long mediaId);

  /**
   * Find all media for steps in recipes owned by a specific user.
   *
   * @param userId the user ID
   * @return list of step media relationships for the user's recipes
   */
  @Query("SELECT sm FROM StepMedia sm WHERE sm.recipe.userId = :userId")
  List<StepMedia> findByRecipeUserId(@Param("userId") java.util.UUID userId);

  /**
   * Find all media for steps ordered by step number for a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return list of step media relationships ordered by step number
   */
  @Query(
      "SELECT sm FROM StepMedia sm WHERE sm.recipe.recipeId = :recipeId ORDER BY sm.step.stepNumber ASC")
  List<StepMedia> findByRecipeRecipeIdOrderByStepNumber(@Param("recipeId") Long recipeId);

  /**
   * Delete all media relationships for steps in a specific recipe.
   *
   * @param recipeId the recipe ID
   */
  void deleteByRecipeRecipeId(Long recipeId);

  /**
   * Delete all step relationships for a specific media.
   *
   * @param mediaId the media ID
   */
  void deleteByMediaMediaId(Long mediaId);

  /**
   * Delete all media relationships for a specific step.
   *
   * @param stepId the step ID
   */
  void deleteByStepStepId(Long stepId);

  /**
   * Delete all media relationships for a specific step in a recipe.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   */
  void deleteByRecipeRecipeIdAndStepStepId(Long recipeId, Long stepId);
}
