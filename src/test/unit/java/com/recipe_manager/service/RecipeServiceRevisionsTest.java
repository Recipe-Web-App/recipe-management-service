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
import com.recipe_manager.model.dto.response.RecipeRevisionsResponse;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeRevision;
import com.recipe_manager.model.mapper.RecipeMapper;
import com.recipe_manager.model.mapper.RecipeRevisionMapper;
import com.recipe_manager.model.mapper.RecipeStepMapper;
import com.recipe_manager.repository.ingredient.IngredientRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.repository.recipe.RecipeRevisionRepository;
import com.recipe_manager.repository.recipe.RecipeTagRepository;
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
 * Unit tests for RecipeService revision-related methods.
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class RecipeServiceRevisionsTest {

  @Mock
  private RecipeRepository recipeRepository;

  @Mock
  private IngredientRepository ingredientRepository;

  @Mock
  private RecipeTagRepository recipeTagRepository;

  @Mock
  private RecipeRevisionRepository recipeRevisionRepository;

  @Mock
  private RecipeMapper recipeMapper;

  @Mock
  private RecipeRevisionMapper recipeRevisionMapper;

  @Mock
  private RecipeStepMapper recipeStepMapper;

  private RecipeService recipeService;

  private Recipe testRecipe;
  private UUID currentUserId;
  private List<RecipeRevision> testRevisions;
  private List<RecipeRevisionDto> testRevisionDtos;

  @BeforeEach
  void setUp() {
    recipeService = new RecipeService(
        recipeRepository,
        ingredientRepository,
        recipeTagRepository,
        recipeRevisionRepository,
        recipeMapper,
        recipeRevisionMapper,
        recipeStepMapper);

    currentUserId = UUID.randomUUID();
    testRecipe = Recipe.builder()
        .recipeId(1L)
        .userId(currentUserId)
        .title("Test Recipe")
        .build();

    testRevisions = Arrays.asList(
        RecipeRevision.builder().revisionId(1L).build(),
        RecipeRevision.builder().revisionId(2L).build());

    testRevisionDtos = Arrays.asList(
        RecipeRevisionDto.builder().revisionId(1L).recipeId(1L).build(),
        RecipeRevisionDto.builder().revisionId(2L).recipeId(1L).build());
  }

  @Test
  void getRevisions_WhenRecipeExistsAndUserOwnsIt_ShouldReturnRevisions() {
    // Arrange
    Long recipeId = 1L;

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(testRecipe));
      when(recipeRevisionRepository.findByRecipeId(recipeId)).thenReturn(testRevisions);
      when(recipeRevisionMapper.toDtoList(testRevisions)).thenReturn(testRevisionDtos);

      // Act
      RecipeRevisionsResponse response = recipeService.getRevisions(recipeId);

      // Assert
      assertEquals(recipeId, response.getRecipeId());
      assertEquals(2, response.getTotalCount());
      assertEquals(testRevisionDtos, response.getRevisions());
    }
  }

  @Test
  void getRevisions_WhenRecipeNotFound_ShouldThrowResourceNotFoundException() {
    // Arrange
    Long recipeId = 999L;

    when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> recipeService.getRevisions(recipeId));

    assertEquals("Recipe not found with id: " + recipeId, exception.getMessage());
  }

  @Test
  void getRevisions_WhenUserDoesNotOwnRecipe_ShouldThrowAccessDeniedException() {
    // Arrange
    Long recipeId = 1L;
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
          () -> recipeService.getRevisions(recipeId));

      assertEquals("You don't have permission to view revisions for this recipe", exception.getMessage());
    }
  }

  @Test
  void getRevisions_WhenNoRevisionsExist_ShouldReturnEmptyList() {
    // Arrange
    Long recipeId = 1L;
    List<RecipeRevision> emptyRevisions = Arrays.asList();
    List<RecipeRevisionDto> emptyRevisionDtos = Arrays.asList();

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(testRecipe));
      when(recipeRevisionRepository.findByRecipeId(recipeId)).thenReturn(emptyRevisions);
      when(recipeRevisionMapper.toDtoList(emptyRevisions)).thenReturn(emptyRevisionDtos);

      // Act
      RecipeRevisionsResponse response = recipeService.getRevisions(recipeId);

      // Assert
      assertEquals(recipeId, response.getRecipeId());
      assertEquals(0, response.getTotalCount());
      assertEquals(emptyRevisionDtos, response.getRevisions());
    }
  }
}
