package com.recipe_manager.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.dto.request.CreateRecipeIngredientRequest;
import com.recipe_manager.model.dto.request.CreateRecipeRequest;
import com.recipe_manager.model.dto.request.CreateRecipeStepRequest;
import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.enums.DifficultyLevel;
import com.recipe_manager.repository.ingredient.IngredientRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.util.SecurityUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for RecipeService.
 *
 * <p>
 * Tests cover all placeholder methods:
 * <ul>
 * <li>createRecipe</li>
 * <li>updateRecipe</li>
 * <li>deleteRecipe</li>
 * <li>getRecipe</li>
 * <li>searchRecipes</li>
 * </ul>
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

  @InjectMocks
  private RecipeService recipeService;

  @Mock
  private RecipeRepository recipeRepository;
  @Mock
  private IngredientRepository ingredientRepository;

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create recipe successfully")
  void shouldCreateRecipeSuccessfully() {
    UUID fakeUserId = UUID.randomUUID();
    try (var mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserId).thenReturn(fakeUserId);
      Ingredient flour = Ingredient.builder().ingredientId(1L).name("Flour").build();
      when(ingredientRepository.findByNameIgnoreCase("Flour")).thenReturn(java.util.Optional.of(flour));
      CreateRecipeIngredientRequest ingredient = CreateRecipeIngredientRequest.builder()
          .ingredientName("Flour")
          .quantity(BigDecimal.valueOf(1.0))
          .unit(null)
          .isOptional(false)
          .build();
      CreateRecipeStepRequest step = CreateRecipeStepRequest.builder()
          .stepNumber(1)
          .instruction("Mix ingredients")
          .build();
      CreateRecipeRequest request = CreateRecipeRequest.builder()
          .title("Test Recipe")
          .description("A test recipe")
          .originUrl("http://example.com")
          .servings(BigDecimal.valueOf(2))
          .preparationTime(10)
          .cookingTime(20)
          .difficulty(DifficultyLevel.BEGINNER)
          .ingredients(List.of(ingredient))
          .steps(List.of(step))
          .build();
      when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> {
        Recipe r = invocation.getArgument(0);
        r.setRecipeId(42L);
        return r;
      });
      RecipeService service = new RecipeService(recipeRepository, ingredientRepository);
      ResponseEntity<Long> response = service.createRecipe(request);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
    }
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should update recipe successfully")
  void shouldUpdateRecipeSuccessfully() {
    // Given
    String recipeId = "recipe-123";

    // When
    ResponseEntity<String> response = recipeService.updateRecipe(recipeId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Update Recipe - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should delete recipe successfully")
  void shouldDeleteRecipeSuccessfully() {
    // Given
    String recipeId = "recipe-123";

    // When
    ResponseEntity<String> response = recipeService.deleteRecipe(recipeId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Delete Recipe - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should get recipe by ID successfully")
  void shouldGetRecipeByIdSuccessfully() {
    // Given
    String recipeId = "recipe-123";

    // When
    ResponseEntity<String> response = recipeService.getRecipe(recipeId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Get Full Recipe - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should search recipes successfully")
  void shouldSearchRecipesSuccessfully() {
    // When
    ResponseEntity<String> response = recipeService.searchRecipes();

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Search Recipes - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle null recipe ID gracefully")
  void shouldHandleNullRecipeIdGracefully() {
    // When
    ResponseEntity<String> response = recipeService.getRecipe(null);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Get Full Recipe - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle empty recipe ID gracefully")
  void shouldHandleEmptyRecipeIdGracefully() {
    // When
    ResponseEntity<String> response = recipeService.getRecipe("");

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Get Full Recipe - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create recipe with user ID from security context")
  void shouldCreateRecipeWithUserIdFromSecurityContext() {
    UUID fakeUserId = UUID.randomUUID();
    try (var mocked = mockStatic(SecurityUtils.class)) {
      mocked.when(SecurityUtils::getCurrentUserId).thenReturn(fakeUserId);
      Ingredient flour = Ingredient.builder().ingredientId(1L).name("Flour").build();
      when(ingredientRepository.findByNameIgnoreCase("Flour")).thenReturn(java.util.Optional.of(flour));
      CreateRecipeIngredientRequest ingredient = CreateRecipeIngredientRequest.builder()
          .ingredientName("Flour")
          .quantity(BigDecimal.valueOf(1.0))
          .unit(null)
          .isOptional(false)
          .build();
      CreateRecipeStepRequest step = CreateRecipeStepRequest.builder()
          .stepNumber(1)
          .instruction("Mix ingredients")
          .build();
      CreateRecipeRequest request = CreateRecipeRequest.builder()
          .title("Test Recipe")
          .description("A test recipe")
          .originUrl("http://example.com")
          .servings(BigDecimal.valueOf(2))
          .preparationTime(10)
          .cookingTime(20)
          .difficulty(DifficultyLevel.BEGINNER)
          .ingredients(List.of(ingredient))
          .steps(List.of(step))
          .build();
      ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);
      when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> {
        Recipe r = invocation.getArgument(0);
        r.setRecipeId(42L);
        return r;
      });
      RecipeService service = new RecipeService(recipeRepository, ingredientRepository);
      ResponseEntity<Long> response = service.createRecipe(request);
      verify(recipeRepository).save(recipeCaptor.capture());
      Recipe savedRecipe = recipeCaptor.getValue();
      assertThat(savedRecipe.getUserId()).isEqualTo(fakeUserId);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
  }
}
