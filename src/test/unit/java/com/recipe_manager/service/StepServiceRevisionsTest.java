package com.recipe_manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.recipe.RecipeRevisionDto;
import com.recipe_manager.model.dto.response.StepRevisionsResponse;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeRevision;
import com.recipe_manager.model.entity.recipe.RecipeStep;
import com.recipe_manager.model.mapper.RecipeRevisionMapper;
import com.recipe_manager.model.mapper.RecipeStepMapper;
import com.recipe_manager.model.mapper.StepCommentMapper;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.repository.recipe.RecipeRevisionRepository;
import com.recipe_manager.repository.recipe.RecipeStepRepository;
import com.recipe_manager.repository.recipe.StepCommentRepository;
import com.recipe_manager.service.external.notificationservice.NotificationService;
import com.recipe_manager.util.SecurityUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

/**
 * Unit tests for StepService revision-related methods.
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class StepServiceRevisionsTest {

  @Mock
  private RecipeRepository recipeRepository;

  @Mock
  private RecipeStepRepository recipeStepRepository;

  @Mock
  private StepCommentRepository stepCommentRepository;

  @Mock
  private RecipeRevisionRepository recipeRevisionRepository;

  @Mock
  private RecipeStepMapper recipeStepMapper;

  @Mock
  private StepCommentMapper stepCommentMapper;

  @Mock
  private RecipeRevisionMapper recipeRevisionMapper;

  @Mock
  private NotificationService notificationService;

  private StepService stepService;

  private Recipe testRecipe;
  private RecipeStep testStep;
  private UUID currentUserId;
  private List<RecipeRevision> testRevisions;
  private List<RecipeRevisionDto> testRevisionDtos;

  @BeforeEach
  void setUp() {
    stepService = new StepService(
        recipeRepository,
        recipeStepRepository,
        stepCommentRepository,
        recipeRevisionRepository,
        recipeStepMapper,
        stepCommentMapper,
        recipeRevisionMapper,
        notificationService);

    currentUserId = UUID.randomUUID();
    testRecipe = Recipe.builder()
        .recipeId(1L)
        .userId(currentUserId)
        .title("Test Recipe")
        .build();

    testStep = RecipeStep.builder()
        .stepId(1L)
        .recipe(testRecipe)
        .stepNumber(1)
        .instruction("Test step")
        .build();

    testRevisions = Arrays.asList(
        RecipeRevision.builder().revisionId(1L).recipe(testRecipe).build(),
        RecipeRevision.builder().revisionId(2L).recipe(testRecipe).build());

    testRevisionDtos = Arrays.asList(
        RecipeRevisionDto.builder().revisionId(1L).recipeId(1L).build(),
        RecipeRevisionDto.builder().revisionId(2L).recipeId(1L).build());
  }

  @Test
  void getStepRevisions_WhenRecipeAndStepExistAndUserOwnsIt_ShouldReturnRevisions() {
    // Arrange
    Long recipeId = 1L;
    Long stepId = 1L;

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(testRecipe));
      when(recipeRepository.existsById(recipeId)).thenReturn(true);
      when(recipeStepRepository.findByStepIdAndRecipeRecipeId(stepId, recipeId))
          .thenReturn(Optional.of(testStep));
      when(recipeRevisionRepository.findStepRevisionsByRecipeIdAndStepId(recipeId, stepId))
          .thenReturn(testRevisions);
      when(recipeRevisionMapper.toDtoList(testRevisions)).thenReturn(testRevisionDtos);

      // Act
      StepRevisionsResponse response = stepService.getStepRevisions(recipeId, stepId);

      // Assert
      assertEquals(recipeId, response.getRecipeId());
      assertEquals(stepId, response.getStepId());
      assertEquals(2, response.getTotalCount());
      assertEquals(testRevisionDtos, response.getRevisions());
    }
  }

  @Test
  void getStepRevisions_WhenRecipeNotFound_ShouldThrowResourceNotFoundException() {
    // Arrange
    Long recipeId = 999L;
    Long stepId = 1L;

    when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> stepService.getStepRevisions(recipeId, stepId));

    assertEquals("Recipe not found with id: " + recipeId, exception.getMessage());
  }

  @Test
  void getStepRevisions_WhenUserDoesNotOwnRecipe_ShouldThrowAccessDeniedException() {
    // Arrange
    Long recipeId = 1L;
    Long stepId = 1L;
    UUID differentUserId = UUID.randomUUID();
    Recipe recipeOwnedByOtherUser = Recipe.builder()
        .recipeId(recipeId)
        .userId(differentUserId)
        .title("Other User's Recipe")
        .build();

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipeOwnedByOtherUser));

      // Act & Assert
      AccessDeniedException exception = assertThrows(
          AccessDeniedException.class,
          () -> stepService.getStepRevisions(recipeId, stepId));

      assertEquals("You don't have permission to view revisions for this recipe", exception.getMessage());
    }
  }

  @Test
  void getStepRevisions_WhenStepNotFound_ShouldThrowResourceNotFoundException() {
    // Arrange
    Long recipeId = 1L;
    Long stepId = 999L;

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(testRecipe));
      when(recipeRepository.existsById(recipeId)).thenReturn(true);
      when(recipeStepRepository.findByStepIdAndRecipeRecipeId(stepId, recipeId))
          .thenReturn(Optional.empty());

      // Act & Assert
      ResourceNotFoundException exception = assertThrows(
          ResourceNotFoundException.class,
          () -> stepService.getStepRevisions(recipeId, stepId));

      assertEquals("Step not found with ID: " + stepId + " for recipe: " + recipeId, exception.getMessage());
    }
  }

  @Test
  void getStepRevisions_WhenNoRevisionsExist_ShouldReturnEmptyList() {
    // Arrange
    Long recipeId = 1L;
    Long stepId = 1L;
    List<RecipeRevision> emptyRevisions = Arrays.asList();
    List<RecipeRevisionDto> emptyRevisionDtos = Arrays.asList();

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(testRecipe));
      when(recipeRepository.existsById(recipeId)).thenReturn(true);
      when(recipeStepRepository.findByStepIdAndRecipeRecipeId(stepId, recipeId))
          .thenReturn(Optional.of(testStep));
      when(recipeRevisionRepository.findStepRevisionsByRecipeIdAndStepId(recipeId, stepId))
          .thenReturn(emptyRevisions);
      when(recipeRevisionMapper.toDtoList(emptyRevisions)).thenReturn(emptyRevisionDtos);

      // Act
      StepRevisionsResponse response = stepService.getStepRevisions(recipeId, stepId);

      // Assert
      assertEquals(recipeId, response.getRecipeId());
      assertEquals(stepId, response.getStepId());
      assertEquals(0, response.getTotalCount());
      assertEquals(emptyRevisionDtos, response.getRevisions());
    }
  }
}
