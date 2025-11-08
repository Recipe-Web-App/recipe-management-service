package com.recipe_manager.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.recipe.RecipeStepDto;
import com.recipe_manager.model.dto.recipe.StepCommentDto;
import com.recipe_manager.model.dto.request.AddStepCommentRequest;
import com.recipe_manager.model.dto.request.DeleteStepCommentRequest;
import com.recipe_manager.model.dto.request.EditStepCommentRequest;
import com.recipe_manager.model.dto.response.StepCommentResponse;
import com.recipe_manager.model.dto.response.StepResponse;
import com.recipe_manager.model.dto.response.StepRevisionsResponse;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeRevision;
import com.recipe_manager.model.entity.recipe.RecipeStep;
import com.recipe_manager.model.entity.recipe.StepComment;
import com.recipe_manager.model.mapper.RecipeRevisionMapper;
import com.recipe_manager.model.mapper.RecipeStepMapper;
import com.recipe_manager.model.mapper.StepCommentMapper;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.repository.recipe.RecipeRevisionRepository;
import com.recipe_manager.repository.recipe.RecipeStepRepository;
import com.recipe_manager.repository.recipe.StepCommentRepository;
import com.recipe_manager.service.external.notificationservice.NotificationService;
import com.recipe_manager.util.SecurityUtils;

/** Service for step-related operations. */
@Service
public class StepService {

  /** Repository for managing recipe data. */
  private final RecipeRepository recipeRepository;

  /** Repository for managing recipe step data. */
  private final RecipeStepRepository recipeStepRepository;

  /** Repository for managing step comment data. */
  private final StepCommentRepository stepCommentRepository;

  /** Repository for managing recipe revision data. */
  private final RecipeRevisionRepository recipeRevisionRepository;

  /** Mapper for converting between RecipeStep entities and DTOs. */
  private final RecipeStepMapper recipeStepMapper;

  /** Mapper for converting between StepComment entities and DTOs. */
  private final StepCommentMapper stepCommentMapper;

  /** Mapper for converting between RecipeRevision entities and DTOs. */
  private final RecipeRevisionMapper recipeRevisionMapper;

  /** Service for sending notifications about recipe events. */
  private final NotificationService notificationService;

  public StepService(
      final RecipeRepository recipeRepository,
      final RecipeStepRepository recipeStepRepository,
      final StepCommentRepository stepCommentRepository,
      final RecipeRevisionRepository recipeRevisionRepository,
      final RecipeStepMapper recipeStepMapper,
      final StepCommentMapper stepCommentMapper,
      final RecipeRevisionMapper recipeRevisionMapper,
      final NotificationService notificationService) {
    this.recipeRepository = recipeRepository;
    this.recipeStepRepository = recipeStepRepository;
    this.stepCommentRepository = stepCommentRepository;
    this.recipeRevisionRepository = recipeRevisionRepository;
    this.recipeStepMapper = recipeStepMapper;
    this.stepCommentMapper = stepCommentMapper;
    this.recipeRevisionMapper = recipeRevisionMapper;
    this.notificationService = notificationService;
  }

  /**
   * Get all steps for a recipe.
   *
   * @param recipeId the recipe ID
   * @return response containing all steps for the recipe
   * @throws ResourceNotFoundException if recipe not found
   */
  public StepResponse getSteps(final Long recipeId) {
    validateRecipeExists(recipeId);

    List<RecipeStep> steps =
        recipeStepRepository.findByRecipeRecipeIdOrderByStepNumberAsc(recipeId);
    List<RecipeStepDto> stepDtos = recipeStepMapper.toDtoList(steps);

    return StepResponse.builder().recipeId(recipeId).steps(stepDtos).build();
  }

  /**
   * Add a comment to a recipe step.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   * @param request the comment request
   * @return the created comment
   * @throws ResourceNotFoundException if recipe or step not found
   */
  @Transactional
  public StepCommentDto addComment(
      final Long recipeId, final Long stepId, final AddStepCommentRequest request) {
    RecipeStep step = validateStepExists(recipeId, stepId);
    UUID currentUserId = SecurityUtils.getCurrentUserId();

    StepComment comment =
        StepComment.builder()
            .recipeId(recipeId)
            .step(step)
            .userId(currentUserId)
            .commentText(request.getComment())
            .isPublic(request.getIsPublic())
            .build();

    StepComment savedComment = stepCommentRepository.save(comment);
    StepCommentDto commentDto = stepCommentMapper.toDto(savedComment);

    // Trigger async notification for step commented (with self-notification filtering)
    UUID recipeAuthorId = step.getRecipe().getUserId();
    notificationService.notifyRecipeCommentedAsync(
        recipeAuthorId, savedComment.getCommentId(), currentUserId);

    return commentDto;
  }

  /**
   * Edit a comment on a recipe step.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   * @param request the edit comment request
   * @return the updated comment
   * @throws ResourceNotFoundException if recipe, step, or comment not found
   * @throws AccessDeniedException if user doesn't own the comment
   */
  @Transactional
  public StepCommentDto editComment(
      final Long recipeId, final Long stepId, final EditStepCommentRequest request) {
    validateStepExists(recipeId, stepId);
    UUID currentUserId = SecurityUtils.getCurrentUserId();

    StepComment comment =
        stepCommentRepository
            .findByCommentIdAndStepStepId(request.getCommentId(), stepId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Comment not found with ID: " + request.getCommentId()));

    if (!comment.getUserId().equals(currentUserId)) {
      throw new AccessDeniedException("You can only edit your own comments");
    }

    comment.setCommentText(request.getComment());
    StepComment savedComment = stepCommentRepository.save(comment);
    return stepCommentMapper.toDto(savedComment);
  }

  /**
   * Delete a comment from a recipe step.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   * @param request the delete comment request
   * @throws ResourceNotFoundException if recipe, step, or comment not found
   * @throws AccessDeniedException if user doesn't own the comment
   */
  @Transactional
  public void deleteComment(
      final Long recipeId, final Long stepId, final DeleteStepCommentRequest request) {
    validateStepExists(recipeId, stepId);
    UUID currentUserId = SecurityUtils.getCurrentUserId();

    StepComment comment =
        stepCommentRepository
            .findByCommentIdAndStepStepId(request.getCommentId(), stepId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Comment not found with ID: " + request.getCommentId()));

    if (!comment.getUserId().equals(currentUserId)) {
      throw new AccessDeniedException("You can only delete your own comments");
    }

    stepCommentRepository.delete(comment);
  }

  /**
   * Get all comments for a recipe step.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   * @return response containing all comments for the step
   * @throws ResourceNotFoundException if recipe or step not found
   */
  public StepCommentResponse getStepComments(final Long recipeId, final Long stepId) {
    validateStepExists(recipeId, stepId);

    List<StepComment> comments =
        stepCommentRepository.findByRecipeIdAndStepStepIdOrderByCreatedAtAsc(recipeId, stepId);
    List<StepCommentDto> commentDtos = stepCommentMapper.toDtoList(comments);

    return StepCommentResponse.builder()
        .recipeId(recipeId)
        .stepId(stepId)
        .comments(commentDtos)
        .build();
  }

  /**
   * Validates that a recipe exists.
   *
   * @param recipeId the recipe ID
   * @throws ResourceNotFoundException if recipe not found
   */
  private void validateRecipeExists(final Long recipeId) {
    if (!recipeRepository.existsById(recipeId)) {
      throw new ResourceNotFoundException("Recipe not found with ID: " + recipeId);
    }
  }

  /**
   * Validates that a step exists for a given recipe.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   * @return the recipe step if found
   * @throws ResourceNotFoundException if recipe or step not found
   */
  private RecipeStep validateStepExists(final Long recipeId, final Long stepId) {
    validateRecipeExists(recipeId);

    return recipeStepRepository
        .findByStepIdAndRecipeRecipeId(stepId, recipeId)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "Step not found with ID: " + stepId + " for recipe: " + recipeId));
  }

  /**
   * Get all revisions for a specific recipe step.
   *
   * @param recipeId the recipe ID
   * @param stepId the step ID
   * @return StepRevisionsResponse containing all revisions for the step
   * @throws ResourceNotFoundException if the recipe or step is not found
   * @throws AccessDeniedException if the user doesn't have permission to view the recipe
   */
  public StepRevisionsResponse getStepRevisions(final Long recipeId, final Long stepId) {
    // Check user has access to the recipe
    Recipe recipe =
        recipeRepository
            .findById(recipeId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));

    if (!recipe.getUserId().equals(SecurityUtils.getCurrentUserId())) {
      throw new AccessDeniedException(
          "You don't have permission to view revisions for this recipe");
    }

    // Verify step exists and user has access to recipe
    RecipeStep step = validateStepExists(recipeId, stepId);
    if (step == null) {
      throw new ResourceNotFoundException("Step not found with ID: " + stepId);
    }

    // Get all step revisions for the recipe and step
    List<RecipeRevision> revisions =
        recipeRevisionRepository.findStepRevisionsByRecipeIdAndStepId(recipeId, stepId);

    // Convert to DTOs
    var revisionDtos = recipeRevisionMapper.toDtoList(revisions);

    return StepRevisionsResponse.builder()
        .recipeId(recipeId)
        .stepId(stepId)
        .revisions(revisionDtos)
        .totalCount(revisionDtos.size())
        .build();
  }
}
