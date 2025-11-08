package com.recipe_manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.recipe_manager.model.dto.recipe.RecipeIngredientDto;
import com.recipe_manager.model.dto.recipe.RecipeStepDto;
import com.recipe_manager.model.dto.request.UpdateRecipeRequest;
import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeIngredient;
import com.recipe_manager.model.entity.recipe.RecipeIngredientId;
import com.recipe_manager.model.entity.recipe.RecipeRevision;
import com.recipe_manager.model.entity.recipe.RecipeStep;
import com.recipe_manager.model.enums.IngredientUnit;
import com.recipe_manager.model.enums.RevisionCategory;
import com.recipe_manager.model.enums.RevisionType;
import com.recipe_manager.model.mapper.RecipeCommentMapper;
import com.recipe_manager.model.mapper.RecipeMapper;
import com.recipe_manager.model.mapper.RecipeRevisionMapper;
import com.recipe_manager.model.mapper.RecipeStepMapper;
import com.recipe_manager.repository.ingredient.IngredientRepository;
import com.recipe_manager.repository.recipe.RecipeCommentRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.repository.recipe.RecipeRevisionRepository;
import com.recipe_manager.repository.recipe.RecipeTagRepository;
import com.recipe_manager.service.external.notificationservice.NotificationService;
import com.recipe_manager.util.SecurityUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for RecipeService revision tracking functionality.
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class RecipeServiceRevisionTest {

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
  @Mock
  private RecipeCommentRepository recipeCommentRepository;
  @Mock
  private RecipeCommentMapper recipeCommentMapper;
  @Mock
  private NotificationService notificationService;

  private RecipeService recipeService;
  private UUID currentUserId;
  private Recipe existingRecipe;

  @BeforeEach
  void setUp() {
    recipeService = new RecipeService(
        recipeRepository,
        ingredientRepository,
        recipeTagRepository,
        recipeRevisionRepository,
        recipeMapper,
        recipeRevisionMapper,
        recipeStepMapper,
        recipeCommentRepository,
        recipeCommentMapper,
        notificationService);

    currentUserId = UUID.randomUUID();
    setupExistingRecipe();
  }

  private void setupExistingRecipe() {
    existingRecipe = new Recipe();
    existingRecipe.setRecipeId(1L);
    existingRecipe.setUserId(currentUserId);
    existingRecipe.setTitle("Test Recipe");

    // Setup existing ingredients
    Ingredient ingredient1 = Ingredient.builder()
        .ingredientId(1L)
        .name("Salt")
        .build();

    RecipeIngredient recipeIngredient1 = RecipeIngredient.builder()
        .id(RecipeIngredientId.builder().recipeId(1L).ingredientId(1L).build())
        .recipe(existingRecipe)
        .ingredient(ingredient1)
        .quantity(new BigDecimal("1.0"))
        .unit(IngredientUnit.TSP)
        .isOptional(false)
        .build();

    existingRecipe.setRecipeIngredients(new ArrayList<>(Arrays.asList(recipeIngredient1)));

    // Setup existing steps
    RecipeStep step1 = RecipeStep.builder()
        .stepId(1L)
        .recipe(existingRecipe)
        .stepNumber(1)
        .instruction("Mix ingredients")
        .build();

    existingRecipe.setRecipeSteps(new ArrayList<>(Arrays.asList(step1)));
  }

  @Test
  void updateRecipe_WithIngredientChanges_CreatesRevisions() {
    // Arrange
    UpdateRecipeRequest request = UpdateRecipeRequest.builder()
        .ingredients(Arrays.asList(
            RecipeIngredientDto.builder()
                .ingredientId(1L)
                .ingredientName("Salt")
                .quantity(new BigDecimal("2.0")) // Changed quantity
                .unit(IngredientUnit.TSP)
                .isOptional(false)
                .build(),
            RecipeIngredientDto.builder()
                .ingredientName("Pepper") // New ingredient
                .quantity(new BigDecimal("0.5"))
                .unit(IngredientUnit.TSP)
                .isOptional(true)
                .build()))
        .build();

    // Mock dependencies
    when(recipeRepository.findById(1L)).thenReturn(Optional.of(existingRecipe));
    when(ingredientRepository.findById(1L))
        .thenReturn(Optional.of(existingRecipe.getRecipeIngredients().get(0).getIngredient()));

    Ingredient newIngredient = Ingredient.builder()
        .ingredientId(2L)
        .name("Pepper")
        .build();
    when(ingredientRepository.findByNameIgnoreCase("Pepper")).thenReturn(Optional.empty());
    when(ingredientRepository.save(any(Ingredient.class))).thenReturn(newIngredient);
    when(recipeRepository.save(any(Recipe.class))).thenReturn(existingRecipe);

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      // Act
      ResponseEntity<?> response = recipeService.updateRecipe("1", request);

      // Assert
      assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());

      // Verify revisions were saved
      ArgumentCaptor<List<RecipeRevision>> revisionCaptor = ArgumentCaptor.captor();
      verify(recipeRevisionRepository).saveAll(revisionCaptor.capture());

      List<RecipeRevision> savedRevisions = revisionCaptor.getValue();
      assertEquals(2, savedRevisions.size());

      // Check that we have both UPDATE and ADD revisions for ingredients
      long updateRevisions = savedRevisions.stream()
          .filter(
              r -> r.getRevisionType() == RevisionType.UPDATE && r.getRevisionCategory() == RevisionCategory.INGREDIENT)
          .count();
      long addRevisions = savedRevisions.stream()
          .filter(
              r -> r.getRevisionType() == RevisionType.ADD && r.getRevisionCategory() == RevisionCategory.INGREDIENT)
          .count();

      assertEquals(1, updateRevisions);
      assertEquals(1, addRevisions);
    }
  }

  @Test
  void updateRecipe_WithStepChanges_CreatesRevisions() {
    // Arrange
    UpdateRecipeRequest request = UpdateRecipeRequest.builder()
        .steps(Arrays.asList(
            RecipeStepDto.builder()
                .stepNumber(1)
                .instruction("Mix ingredients thoroughly") // Changed instruction
                .build(),
            RecipeStepDto.builder()
                .stepNumber(2)
                .instruction("Cook for 10 minutes") // New step
                .build()))
        .build();

    when(recipeRepository.findById(1L)).thenReturn(Optional.of(existingRecipe));
    when(recipeRepository.save(any(Recipe.class))).thenReturn(existingRecipe);
    when(recipeStepMapper.toEntityList(anyList())).thenReturn(Arrays.asList(
        RecipeStep.builder()
            .stepNumber(1)
            .instruction("Mix ingredients thoroughly")
            .build(),
        RecipeStep.builder()
            .stepNumber(2)
            .instruction("Cook for 10 minutes")
            .build()));

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      // Act
      ResponseEntity<?> response = recipeService.updateRecipe("1", request);

      // Assert
      assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());

      // Verify revisions were saved
      ArgumentCaptor<List<RecipeRevision>> revisionsCaptor = ArgumentCaptor.captor();
      verify(recipeRevisionRepository).saveAll(revisionsCaptor.capture());

      List<RecipeRevision> savedRevisions = revisionsCaptor.getValue();
      assertEquals(2, savedRevisions.size());

      // Check that we have both UPDATE and ADD revisions for steps
      long updateRevisions = savedRevisions.stream()
          .filter(r -> r.getRevisionType() == RevisionType.UPDATE && r.getRevisionCategory() == RevisionCategory.STEP)
          .count();
      long addRevisions = savedRevisions.stream()
          .filter(r -> r.getRevisionType() == RevisionType.ADD && r.getRevisionCategory() == RevisionCategory.STEP)
          .count();

      assertEquals(1, updateRevisions);
      assertEquals(1, addRevisions);
    }
  }

  @Test
  void updateRecipe_WithDeletedIngredients_CreatesDeleteRevisions() {
    // Arrange - request with no ingredients (deletes existing ones)
    UpdateRecipeRequest request = UpdateRecipeRequest.builder()
        .ingredients(Arrays.asList()) // Empty list deletes all existing ingredients
        .build();

    when(recipeRepository.findById(1L)).thenReturn(Optional.of(existingRecipe));
    when(recipeRepository.save(any(Recipe.class))).thenReturn(existingRecipe);

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      // Act
      ResponseEntity<?> response = recipeService.updateRecipe("1", request);

      // Assert
      assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());

      // Verify revisions were saved
      ArgumentCaptor<List<RecipeRevision>> revisionsCaptor = ArgumentCaptor.captor();
      verify(recipeRevisionRepository).saveAll(revisionsCaptor.capture());

      List<RecipeRevision> savedRevisions = revisionsCaptor.getValue();
      assertEquals(1, savedRevisions.size());

      // Check that we have a DELETE revision for ingredient
      RecipeRevision deleteRevision = savedRevisions.get(0);
      assertEquals(RevisionType.DELETE, deleteRevision.getRevisionType());
      assertEquals(RevisionCategory.INGREDIENT, deleteRevision.getRevisionCategory());
    }
  }

  @Test
  void updateRecipe_WithDeletedSteps_CreatesDeleteRevisions() {
    // Arrange - request with no steps (deletes existing ones)
    UpdateRecipeRequest request = UpdateRecipeRequest.builder()
        .steps(Arrays.asList()) // Empty list deletes all existing steps
        .build();

    when(recipeRepository.findById(1L)).thenReturn(Optional.of(existingRecipe));
    when(recipeRepository.save(any(Recipe.class))).thenReturn(existingRecipe);
    when(recipeStepMapper.toEntityList(anyList())).thenReturn(Arrays.asList());

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      // Act
      ResponseEntity<?> response = recipeService.updateRecipe("1", request);

      // Assert
      assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());

      // Verify revisions were saved
      ArgumentCaptor<List<RecipeRevision>> revisionsCaptor = ArgumentCaptor.captor();
      verify(recipeRevisionRepository).saveAll(revisionsCaptor.capture());

      List<RecipeRevision> savedRevisions = revisionsCaptor.getValue();
      assertEquals(1, savedRevisions.size());

      // Check that we have a DELETE revision for step
      RecipeRevision deleteRevision = savedRevisions.get(0);
      assertEquals(RevisionType.DELETE, deleteRevision.getRevisionType());
      assertEquals(RevisionCategory.STEP, deleteRevision.getRevisionCategory());
    }
  }

  @Test
  void updateRecipe_WithNoChanges_CreatesNoRevisions() {
    // Arrange - request with same ingredients and steps
    UpdateRecipeRequest request = UpdateRecipeRequest.builder()
        .ingredients(Arrays.asList(
            RecipeIngredientDto.builder()
                .ingredientId(1L)
                .ingredientName("Salt")
                .quantity(new BigDecimal("1.0")) // Same quantity
                .unit(IngredientUnit.TSP)
                .isOptional(false)
                .build()))
        .steps(Arrays.asList(
            RecipeStepDto.builder()
                .stepNumber(1)
                .instruction("Mix ingredients") // Same instruction
                .build()))
        .build();

    when(recipeRepository.findById(1L)).thenReturn(Optional.of(existingRecipe));
    when(ingredientRepository.findById(1L))
        .thenReturn(Optional.of(existingRecipe.getRecipeIngredients().get(0).getIngredient()));
    when(recipeRepository.save(any(Recipe.class))).thenReturn(existingRecipe);
    when(recipeStepMapper.toEntityList(anyList())).thenReturn(Arrays.asList(
        RecipeStep.builder()
            .stepNumber(1)
            .instruction("Mix ingredients")
            .build()));

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      // Act
      ResponseEntity<?> response = recipeService.updateRecipe("1", request);

      // Assert
      assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());

      // Verify no revisions were saved since there were no changes
      verify(recipeRevisionRepository, never()).saveAll(anyList());
    }
  }

  @Test
  void updateRecipe_WithNullIngredientsAndSteps_CreatesNoRevisions() {
    // Arrange - request with null ingredients and steps (no changes)
    UpdateRecipeRequest request = UpdateRecipeRequest.builder()
        .title("Updated Title") // Only title change
        .build();

    when(recipeRepository.findById(1L)).thenReturn(Optional.of(existingRecipe));
    when(recipeRepository.save(any(Recipe.class))).thenReturn(existingRecipe);

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

      // Act
      ResponseEntity<?> response = recipeService.updateRecipe("1", request);

      // Assert
      assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());

      // Verify no revisions were saved since ingredients and steps weren't changed
      verify(recipeRevisionRepository, never()).saveAll(anyList());
    }
  }
}
