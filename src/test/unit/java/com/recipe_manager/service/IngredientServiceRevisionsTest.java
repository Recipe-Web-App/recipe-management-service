package com.recipe_manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.recipe.RecipeRevisionDto;
import com.recipe_manager.model.dto.response.IngredientRevisionsResponse;
import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeIngredient;
import com.recipe_manager.model.entity.recipe.RecipeRevision;
import com.recipe_manager.model.enums.IngredientUnit;
import com.recipe_manager.model.mapper.IngredientCommentMapper;
import com.recipe_manager.model.mapper.RecipeIngredientMapper;
import com.recipe_manager.model.mapper.RecipeRevisionMapper;
import com.recipe_manager.model.mapper.ShoppingListMapper;
import com.recipe_manager.repository.ingredient.IngredientCommentRepository;
import com.recipe_manager.repository.ingredient.IngredientRepository;
import com.recipe_manager.repository.recipe.RecipeIngredientRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.repository.recipe.RecipeRevisionRepository;
import com.recipe_manager.service.external.RecipeScraperService;
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
 * Unit tests for IngredientService revision-related methods.
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class IngredientServiceRevisionsTest {

  @Mock
  private RecipeIngredientRepository recipeIngredientRepository;

  @Mock
  private IngredientRepository ingredientRepository;

  @Mock
  private IngredientCommentRepository ingredientCommentRepository;

  @Mock
  private RecipeRepository recipeRepository;

  @Mock
  private RecipeRevisionRepository recipeRevisionRepository;

  @Mock
  private RecipeIngredientMapper recipeIngredientMapper;

  @Mock
  private IngredientCommentMapper ingredientCommentMapper;

  @Mock
  private RecipeRevisionMapper recipeRevisionMapper;

  @Mock
  private ShoppingListMapper shoppingListMapper;

  @Mock
  private RecipeScraperService recipeScraperService;

  private IngredientService ingredientService;

  private Recipe testRecipe;
  private Ingredient testIngredient;
  private RecipeIngredient testRecipeIngredient;
  private UUID currentUserId;
  private List<RecipeRevision> testRevisions;
  private List<RecipeRevisionDto> testRevisionDtos;

  @BeforeEach
  void setUp() {
    ingredientService = new IngredientService(
        recipeIngredientRepository,
        ingredientRepository,
        ingredientCommentRepository,
        recipeRepository,
        recipeRevisionRepository,
        recipeIngredientMapper,
        ingredientCommentMapper,
        recipeRevisionMapper,
        shoppingListMapper,
        recipeScraperService);

    currentUserId = UUID.randomUUID();
    testRecipe = Recipe.builder()
        .recipeId(1L)
        .userId(currentUserId)
        .title("Test Recipe")
        .build();

    testIngredient = Ingredient.builder()
        .ingredientId(1L)
        .name("Test Ingredient")
        .build();

    testRecipeIngredient = RecipeIngredient.builder()
        .recipe(testRecipe)
        .ingredient(testIngredient)
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.CUP)
        .isOptional(false)
        .build();

    testRevisions = Arrays.asList(
        RecipeRevision.builder().revisionId(1L).recipe(testRecipe).build(),
        RecipeRevision.builder().revisionId(2L).recipe(testRecipe).build());

    testRevisionDtos = Arrays.asList(
        RecipeRevisionDto.builder().revisionId(1L).recipeId(1L).build(),
        RecipeRevisionDto.builder().revisionId(2L).recipeId(1L).build());
  }

  @Test
  void getIngredientRevisions_WhenRecipeAndIngredientExistAndUserOwnsIt_ShouldReturnRevisions() {
    // Arrange
    Long recipeId = 1L;
    Long ingredientId = 1L;

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(testRecipe));
      when(recipeIngredientRepository.findByRecipeRecipeIdAndIngredientIngredientId(recipeId, ingredientId))
          .thenReturn(Optional.of(testRecipeIngredient));
      when(recipeRevisionRepository.findIngredientRevisionsByRecipeIdAndIngredientId(recipeId, ingredientId))
          .thenReturn(testRevisions);
      when(recipeRevisionMapper.toDtoList(testRevisions)).thenReturn(testRevisionDtos);

      // Act
      IngredientRevisionsResponse response = ingredientService.getIngredientRevisions(recipeId, ingredientId);

      // Assert
      assertEquals(recipeId, response.getRecipeId());
      assertEquals(ingredientId, response.getIngredientId());
      assertEquals(2, response.getTotalCount());
      assertEquals(testRevisionDtos, response.getRevisions());
    }
  }

  @Test
  void getIngredientRevisions_WhenRecipeNotFound_ShouldThrowResourceNotFoundException() {
    // Arrange
    Long recipeId = 999L;
    Long ingredientId = 1L;

    when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> ingredientService.getIngredientRevisions(recipeId, ingredientId));

    assertEquals("Recipe not found with id: " + recipeId, exception.getMessage());
  }

  @Test
  void getIngredientRevisions_WhenUserDoesNotOwnRecipe_ShouldThrowAccessDeniedException() {
    // Arrange
    Long recipeId = 1L;
    Long ingredientId = 1L;
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
          () -> ingredientService.getIngredientRevisions(recipeId, ingredientId));

      assertEquals("You don't have permission to view revisions for this recipe", exception.getMessage());
    }
  }

  @Test
  void getIngredientRevisions_WhenIngredientNotFound_ShouldThrowResourceNotFoundException() {
    // Arrange
    Long recipeId = 1L;
    Long ingredientId = 999L;

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(testRecipe));
      when(recipeIngredientRepository.findByRecipeRecipeIdAndIngredientIngredientId(recipeId, ingredientId))
          .thenReturn(Optional.empty());

      // Act & Assert
      ResourceNotFoundException exception = assertThrows(
          ResourceNotFoundException.class,
          () -> ingredientService.getIngredientRevisions(recipeId, ingredientId));

      assertEquals("Recipe ingredient not found for recipe " + recipeId + " and ingredient " + ingredientId,
          exception.getMessage());
    }
  }

  @Test
  void getIngredientRevisions_WhenNoRevisionsExist_ShouldReturnEmptyList() {
    // Arrange
    Long recipeId = 1L;
    Long ingredientId = 1L;
    List<RecipeRevision> emptyRevisions = Arrays.asList();
    List<RecipeRevisionDto> emptyRevisionDtos = Arrays.asList();

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = Mockito.mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(testRecipe));
      when(recipeIngredientRepository.findByRecipeRecipeIdAndIngredientIngredientId(recipeId, ingredientId))
          .thenReturn(Optional.of(testRecipeIngredient));
      when(recipeRevisionRepository.findIngredientRevisionsByRecipeIdAndIngredientId(recipeId, ingredientId))
          .thenReturn(emptyRevisions);
      when(recipeRevisionMapper.toDtoList(emptyRevisions)).thenReturn(emptyRevisionDtos);

      // Act
      IngredientRevisionsResponse response = ingredientService.getIngredientRevisions(recipeId, ingredientId);

      // Assert
      assertEquals(recipeId, response.getRecipeId());
      assertEquals(ingredientId, response.getIngredientId());
      assertEquals(0, response.getTotalCount());
      assertEquals(emptyRevisionDtos, response.getRevisions());
    }
  }
}
