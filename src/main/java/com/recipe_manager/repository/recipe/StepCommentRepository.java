package com.recipe_manager.repository.recipe;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recipe_manager.model.entity.recipe.StepComment;

/** Repository interface for StepComment entity operations. */
@Repository
public interface StepCommentRepository extends JpaRepository<StepComment, Long> {

  /**
   * Find all comments for a specific step.
   *
   * @param stepId the step ID
   * @return list of comments for the step
   */
  List<StepComment> findByStepStepIdOrderByCreatedAtAsc(Long stepId);

  /**
   * Find all public comments for a specific step.
   *
   * @param stepId the step ID
   * @return list of public comments for the step
   */
  List<StepComment> findByStepStepIdAndIsPublicTrueOrderByCreatedAtAsc(Long stepId);

  /**
   * Find all comments for a specific step and user.
   *
   * @param stepId the step ID
   * @param userId the user ID
   * @return list of comments for the step and user
   */
  List<StepComment> findByStepStepIdAndUserIdOrderByCreatedAtAsc(Long stepId, UUID userId);

  /**
   * Find a specific comment by ID and step ID.
   *
   * @param commentId the comment ID
   * @param stepId the step ID
   * @return optional comment
   */
  Optional<StepComment> findByCommentIdAndStepStepId(Long commentId, Long stepId);

  /**
   * Find all comments for steps in a specific recipe.
   *
   * @param recipeId the recipe ID
   * @return list of comments for steps in the recipe
   */
  @Query(
      "SELECT sc FROM StepComment sc "
          + "JOIN sc.step s "
          + "WHERE s.recipe.recipeId = :recipeId "
          + "ORDER BY s.stepNumber ASC, sc.createdAt ASC")
  List<StepComment> findByRecipeId(@Param("recipeId") Long recipeId);

  /**
   * Find all comments for a specific recipe and step combination.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   * @return list of comments for the recipe and step
   */
  List<StepComment> findByRecipeIdAndStepStepIdOrderByCreatedAtAsc(Long recipeId, Long stepId);

  /**
   * Find all public comments for a specific recipe and step combination.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   * @return list of public comments for the recipe and step
   */
  List<StepComment> findByRecipeIdAndStepStepIdAndIsPublicTrueOrderByCreatedAtAsc(
      Long recipeId, Long stepId);

  /**
   * Count comments for a specific step.
   *
   * @param stepId the step ID
   * @return number of comments
   */
  long countByStepStepId(Long stepId);

  /**
   * Count public comments for a specific step.
   *
   * @param stepId the step ID
   * @return number of public comments
   */
  long countByStepStepIdAndIsPublicTrue(Long stepId);
}
