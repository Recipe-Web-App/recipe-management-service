package com.recipe_manager.unit_tests.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.recipe.RecipeStepDto;
import com.recipe_manager.model.dto.recipe.StepCommentDto;
import com.recipe_manager.model.dto.request.AddStepCommentRequest;
import com.recipe_manager.model.dto.request.DeleteStepCommentRequest;
import com.recipe_manager.model.dto.request.EditStepCommentRequest;
import com.recipe_manager.model.dto.response.StepCommentResponse;
import com.recipe_manager.model.dto.response.StepResponse;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeStep;
import com.recipe_manager.model.entity.recipe.StepComment;
import com.recipe_manager.model.mapper.RecipeStepMapper;
import com.recipe_manager.model.mapper.StepCommentMapper;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.repository.recipe.RecipeStepRepository;
import com.recipe_manager.repository.recipe.StepCommentRepository;
import com.recipe_manager.service.StepService;
import com.recipe_manager.util.SecurityUtils;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class StepServiceTest {

  @Mock private RecipeRepository recipeRepository;
  @Mock private RecipeStepRepository recipeStepRepository;
  @Mock private StepCommentRepository stepCommentRepository;
  @Mock private RecipeStepMapper recipeStepMapper;
  @Mock private StepCommentMapper stepCommentMapper;

  private StepService stepService;
  private Recipe testRecipe;
  private RecipeStep testStep;
  private StepComment testComment;
  private UUID testUserId;

  @BeforeEach
  void setUp() {
    stepService =
        new StepService(
            recipeRepository,
            recipeStepRepository,
            stepCommentRepository,
            recipeStepMapper,
            stepCommentMapper);

    testUserId = UUID.randomUUID();

    testRecipe = Recipe.builder().recipeId(1L).title("Test Recipe").build();

    testStep =
        RecipeStep.builder()
            .stepId(1L)
            .recipe(testRecipe)
            .stepNumber(1)
            .instruction("Test instruction")
            .build();

    testComment =
        StepComment.builder()
            .commentId(1L)
            .recipeId(1L)
            .step(testStep)
            .userId(testUserId)
            .commentText("Test comment")
            .isPublic(true)
            .build();
  }

  @Test
  void testGetSteps_Success() {
    // Arrange
    List<RecipeStep> steps = Arrays.asList(testStep);
    List<RecipeStepDto> stepDtos =
        Arrays.asList(
            RecipeStepDto.builder()
                .stepId(1L)
                .recipeId(1L)
                .stepNumber(1)
                .instruction("Test instruction")
                .build());

    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeStepRepository.findByRecipeRecipeIdOrderByStepNumberAsc(1L)).thenReturn(steps);
    when(recipeStepMapper.toDtoList(steps)).thenReturn(stepDtos);

    // Act
    StepResponse result = stepService.getSteps(1L);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(1L);
    assertThat(result.getSteps()).hasSize(1);
    assertThat(result.getSteps().get(0).getStepId()).isEqualTo(1L);

    verify(recipeRepository).existsById(1L);
    verify(recipeStepRepository).findByRecipeRecipeIdOrderByStepNumberAsc(1L);
    verify(recipeStepMapper).toDtoList(steps);
  }

  @Test
  void testGetSteps_RecipeNotFound() {
    // Arrange
    when(recipeRepository.existsById(1L)).thenReturn(false);

    // Act & Assert
    assertThatThrownBy(() -> stepService.getSteps(1L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Recipe not found with ID: 1");

    verify(recipeRepository).existsById(1L);
    verify(recipeStepRepository, never()).findByRecipeRecipeIdOrderByStepNumberAsc(anyLong());
  }

  @Test
  void testGetSteps_EmptyStepsList() {
    // Arrange
    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeStepRepository.findByRecipeRecipeIdOrderByStepNumberAsc(1L))
        .thenReturn(Collections.emptyList());
    when(recipeStepMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

    // Act
    StepResponse result = stepService.getSteps(1L);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(1L);
    assertThat(result.getSteps()).isEmpty();
  }

  @Test
  void testAddComment_Success() {
    // Arrange
    AddStepCommentRequest request = AddStepCommentRequest.builder().comment("Test comment").build();
    StepCommentDto expectedDto =
        StepCommentDto.builder()
            .commentId(1L)
            .recipeId(1L)
            .stepId(1L)
            .userId(testUserId)
            .commentText("Test comment")
            .isPublic(true)
            .build();

    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 1L))
        .thenReturn(Optional.of(testStep));
    when(stepCommentRepository.save(any(StepComment.class))).thenReturn(testComment);
    when(stepCommentMapper.toDto(testComment)).thenReturn(expectedDto);

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      // Act
      StepCommentDto result = stepService.addComment(1L, 1L, request);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.getCommentText()).isEqualTo("Test comment");
      assertThat(result.getUserId()).isEqualTo(testUserId);

      verify(stepCommentRepository).save(any(StepComment.class));
      verify(stepCommentMapper).toDto(testComment);
    }
  }

  @Test
  void testAddComment_StepNotFound() {
    // Arrange
    AddStepCommentRequest request = AddStepCommentRequest.builder().comment("Test comment").build();

    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 1L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> stepService.addComment(1L, 1L, request))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Step not found with ID: 1 for recipe: 1");

    verify(stepCommentRepository, never()).save(any(StepComment.class));
  }

  @Test
  void testEditComment_Success() {
    // Arrange
    EditStepCommentRequest request =
        EditStepCommentRequest.builder().commentId(1L).comment("Updated comment").build();
    StepCommentDto expectedDto =
        StepCommentDto.builder()
            .commentId(1L)
            .recipeId(1L)
            .stepId(1L)
            .userId(testUserId)
            .commentText("Updated comment")
            .isPublic(true)
            .build();

    StepComment updatedComment = StepComment.builder()
        .commentId(testComment.getCommentId())
        .recipeId(testComment.getRecipeId())
        .step(testComment.getStep())
        .userId(testComment.getUserId())
        .commentText("Updated comment")
        .isPublic(testComment.getIsPublic())
        .createdAt(testComment.getCreatedAt())
        .updatedAt(testComment.getUpdatedAt())
        .build();

    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 1L))
        .thenReturn(Optional.of(testStep));
    when(stepCommentRepository.findByCommentIdAndStepStepId(1L, 1L))
        .thenReturn(Optional.of(testComment));
    when(stepCommentRepository.save(any(StepComment.class))).thenReturn(updatedComment);
    when(stepCommentMapper.toDto(updatedComment)).thenReturn(expectedDto);

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      // Act
      StepCommentDto result = stepService.editComment(1L, 1L, request);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.getCommentText()).isEqualTo("Updated comment");

      verify(stepCommentRepository).save(any(StepComment.class));
      verify(stepCommentMapper).toDto(updatedComment);
    }
  }

  @Test
  void testEditComment_CommentNotFound() {
    // Arrange
    EditStepCommentRequest request =
        EditStepCommentRequest.builder().commentId(1L).comment("Updated comment").build();

    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 1L))
        .thenReturn(Optional.of(testStep));
    when(stepCommentRepository.findByCommentIdAndStepStepId(1L, 1L)).thenReturn(Optional.empty());

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      // Act & Assert
      assertThatThrownBy(() -> stepService.editComment(1L, 1L, request))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Comment not found with ID: 1");

      verify(stepCommentRepository, never()).save(any(StepComment.class));
    }
  }

  @Test
  void testEditComment_AccessDenied() {
    // Arrange
    EditStepCommentRequest request =
        EditStepCommentRequest.builder().commentId(1L).comment("Updated comment").build();
    UUID differentUserId = UUID.randomUUID();

    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 1L))
        .thenReturn(Optional.of(testStep));
    when(stepCommentRepository.findByCommentIdAndStepStepId(1L, 1L))
        .thenReturn(Optional.of(testComment));

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(differentUserId);

      // Act & Assert
      assertThatThrownBy(() -> stepService.editComment(1L, 1L, request))
          .isInstanceOf(AccessDeniedException.class)
          .hasMessage("You can only edit your own comments");

      verify(stepCommentRepository, never()).save(any(StepComment.class));
    }
  }

  @Test
  void testDeleteComment_Success() {
    // Arrange
    DeleteStepCommentRequest request = DeleteStepCommentRequest.builder().commentId(1L).build();

    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 1L))
        .thenReturn(Optional.of(testStep));
    when(stepCommentRepository.findByCommentIdAndStepStepId(1L, 1L))
        .thenReturn(Optional.of(testComment));

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      // Act
      stepService.deleteComment(1L, 1L, request);

      // Assert
      verify(stepCommentRepository).delete(testComment);
    }
  }

  @Test
  void testDeleteComment_CommentNotFound() {
    // Arrange
    DeleteStepCommentRequest request = DeleteStepCommentRequest.builder().commentId(1L).build();

    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 1L))
        .thenReturn(Optional.of(testStep));
    when(stepCommentRepository.findByCommentIdAndStepStepId(1L, 1L)).thenReturn(Optional.empty());

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(testUserId);

      // Act & Assert
      assertThatThrownBy(() -> stepService.deleteComment(1L, 1L, request))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Comment not found with ID: 1");

      verify(stepCommentRepository, never()).delete(any(StepComment.class));
    }
  }

  @Test
  void testDeleteComment_AccessDenied() {
    // Arrange
    DeleteStepCommentRequest request = DeleteStepCommentRequest.builder().commentId(1L).build();
    UUID differentUserId = UUID.randomUUID();

    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 1L))
        .thenReturn(Optional.of(testStep));
    when(stepCommentRepository.findByCommentIdAndStepStepId(1L, 1L))
        .thenReturn(Optional.of(testComment));

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(differentUserId);

      // Act & Assert
      assertThatThrownBy(() -> stepService.deleteComment(1L, 1L, request))
          .isInstanceOf(AccessDeniedException.class)
          .hasMessage("You can only delete your own comments");

      verify(stepCommentRepository, never()).delete(any(StepComment.class));
    }
  }

  @Test
  void testGetStepComments_Success() {
    // Arrange
    List<StepComment> comments = Arrays.asList(testComment);
    List<StepCommentDto> commentDtos =
        Arrays.asList(
            StepCommentDto.builder()
                .commentId(1L)
                .recipeId(1L)
                .stepId(1L)
                .userId(testUserId)
                .commentText("Test comment")
                .isPublic(true)
                .build());

    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 1L))
        .thenReturn(Optional.of(testStep));
    when(stepCommentRepository.findByRecipeIdAndStepStepIdOrderByCreatedAtAsc(1L, 1L))
        .thenReturn(comments);
    when(stepCommentMapper.toDtoList(comments)).thenReturn(commentDtos);

    // Act
    StepCommentResponse result = stepService.getStepComments(1L, 1L);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(1L);
    assertThat(result.getStepId()).isEqualTo(1L);
    assertThat(result.getComments()).hasSize(1);
    assertThat(result.getComments().get(0).getCommentText()).isEqualTo("Test comment");

    verify(stepCommentRepository).findByRecipeIdAndStepStepIdOrderByCreatedAtAsc(1L, 1L);
    verify(stepCommentMapper).toDtoList(comments);
  }

  @Test
  void testGetStepComments_EmptyCommentsList() {
    // Arrange
    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 1L))
        .thenReturn(Optional.of(testStep));
    when(stepCommentRepository.findByRecipeIdAndStepStepIdOrderByCreatedAtAsc(1L, 1L))
        .thenReturn(Collections.emptyList());
    when(stepCommentMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

    // Act
    StepCommentResponse result = stepService.getStepComments(1L, 1L);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getRecipeId()).isEqualTo(1L);
    assertThat(result.getStepId()).isEqualTo(1L);
    assertThat(result.getComments()).isEmpty();
  }

  @Test
  void testGetStepComments_StepNotFound() {
    // Arrange
    when(recipeRepository.existsById(1L)).thenReturn(true);
    when(recipeStepRepository.findByStepIdAndRecipeRecipeId(1L, 1L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> stepService.getStepComments(1L, 1L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Step not found with ID: 1 for recipe: 1");

    verify(stepCommentRepository, never())
        .findByRecipeIdAndStepStepIdOrderByCreatedAtAsc(anyLong(), anyLong());
  }
}
