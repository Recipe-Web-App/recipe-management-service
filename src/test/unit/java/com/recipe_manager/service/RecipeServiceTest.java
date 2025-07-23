package com.recipe_manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.recipe.RecipeDto;
import com.recipe_manager.model.dto.recipe.RecipeIngredientDto;
import com.recipe_manager.model.dto.request.CreateRecipeRequest;
import com.recipe_manager.model.dto.request.UpdateRecipeRequest;
import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.enums.DifficultyLevel;
import com.recipe_manager.model.enums.IngredientUnit;
import com.recipe_manager.model.mapper.RecipeMapper;
import com.recipe_manager.repository.ingredient.IngredientRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.util.SecurityUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

/**
 * Unit tests for RecipeService.
 *
 * <p>
 * Tests cover all methods:
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

  @Mock
  private RecipeRepository recipeRepository;

  @Mock
  private IngredientRepository ingredientRepository;

  @Mock
  private RecipeMapper recipeMapper;

  @InjectMocks
  private RecipeService recipeService;

  private UUID currentUserId;
  private Recipe testRecipe;
  private CreateRecipeRequest createRecipeRequest;
  private UpdateRecipeRequest updateRecipeRequest;

  @BeforeEach
  void setUp() {
    currentUserId = UUID.randomUUID();
    testRecipe = Recipe.builder()
        .recipeId(1L)
        .userId(currentUserId)
        .title("Test Recipe")
        .description("Test Description")
        .originUrl("http://test.com")
        .servings(BigDecimal.valueOf(4))
        .preparationTime(30)
        .cookingTime(45)
        .difficulty(DifficultyLevel.EASY)
        .recipeIngredients(new ArrayList<>())
        .build();

    createRecipeRequest = CreateRecipeRequest.builder()
        .title("Test Recipe")
        .description("Test Description")
        .originUrl("http://test.com")
        .servings(BigDecimal.valueOf(4))
        .preparationTime(30)
        .cookingTime(45)
        .difficulty(DifficultyLevel.EASY)
        .ingredients(new ArrayList<>())
        .steps(Arrays.asList()) // Empty but non-null for validation
        .build();

    updateRecipeRequest = UpdateRecipeRequest.builder()
        .title("Updated Recipe")
        .description("Updated Description")
        .build();
  }

  @Nested
  @DisplayName("createRecipe Tests")
  class CreateRecipeTests {

    @Test
    @Tag("standard-processing")
    @DisplayName("Should create recipe successfully with empty ingredients list")
    void shouldCreateRecipeWithEmptyIngredients() {
      // Given
      Recipe savedRecipe = Recipe.builder()
          .recipeId(1L)
          .userId(currentUserId)
          .title("Test Recipe")
          .build();

      RecipeDto recipeDto = RecipeDto.builder()
          .recipeId(1L)
          .userId(currentUserId)
          .title("Test Recipe")
          .build();

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);
        when(recipeMapper.toDto(savedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.createRecipe(createRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(recipeRepository).save(any(Recipe.class));
        verify(recipeMapper).toDto(savedRecipe);
        verify(ingredientRepository, never()).findById(any());
        verify(ingredientRepository, never()).findByNameIgnoreCase(anyString());
      }
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("Should create recipe with ingredients using existing ingredient ID")
    void shouldCreateRecipeWithExistingIngredientId() {
      // Given
      RecipeIngredientDto ingredientDto = RecipeIngredientDto.builder()
          .ingredientId(1L)
          .ingredientName("Flour")
          .quantity(BigDecimal.valueOf(500))
          .unit(IngredientUnit.G)
          .isOptional(false)
          .build();

      createRecipeRequest.setIngredients(Arrays.asList(ingredientDto));

      Ingredient existingIngredient = Ingredient.builder()
          .ingredientId(1L)
          .name("Flour")
          .build();

      Recipe savedRecipe = Recipe.builder()
          .recipeId(1L)
          .userId(currentUserId)
          .build();

      RecipeDto recipeDto = RecipeDto.builder()
          .recipeId(1L)
          .userId(currentUserId)
          .build();

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(existingIngredient));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);
        when(recipeMapper.toDto(savedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.createRecipe(createRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(ingredientRepository).findById(1L);
        verify(ingredientRepository, never()).findByNameIgnoreCase(anyString());
        verify(recipeRepository).save(any(Recipe.class));
        verify(recipeMapper).toDto(savedRecipe);
      }
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("Should create recipe with ingredients using existing ingredient name")
    void shouldCreateRecipeWithExistingIngredientName() {
      // Given
      RecipeIngredientDto ingredientDto = RecipeIngredientDto.builder()
          .ingredientName("Sugar")
          .quantity(BigDecimal.valueOf(200))
          .unit(IngredientUnit.G)
          .isOptional(true)
          .build();

      createRecipeRequest.setIngredients(Arrays.asList(ingredientDto));

      Ingredient existingIngredient = Ingredient.builder()
          .ingredientId(2L)
          .name("Sugar")
          .build();

      Recipe savedRecipe = Recipe.builder()
          .recipeId(1L)
          .userId(currentUserId)
          .build();

      RecipeDto recipeDto = RecipeDto.builder()
          .recipeId(1L)
          .userId(currentUserId)
          .build();

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
        when(ingredientRepository.findByNameIgnoreCase("Sugar")).thenReturn(Optional.of(existingIngredient));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);
        when(recipeMapper.toDto(savedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.createRecipe(createRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(ingredientRepository).findByNameIgnoreCase("Sugar");
        verify(recipeRepository).save(any(Recipe.class));
        verify(recipeMapper).toDto(savedRecipe);
      }
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("Should create recipe and new ingredient when name not found")
    void shouldCreateRecipeAndNewIngredient() {
      // Given
      RecipeIngredientDto ingredientDto = RecipeIngredientDto.builder()
          .ingredientName("New Spice")
          .quantity(BigDecimal.valueOf(10))
          .unit(IngredientUnit.G)
          .isOptional(false)
          .build();

      createRecipeRequest.setIngredients(Arrays.asList(ingredientDto));

      Ingredient newIngredient = Ingredient.builder()
          .ingredientId(3L)
          .name("New Spice")
          .build();

      Recipe savedRecipe = Recipe.builder()
          .recipeId(1L)
          .userId(currentUserId)
          .build();

      RecipeDto recipeDto = RecipeDto.builder()
          .recipeId(1L)
          .userId(currentUserId)
          .build();

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
        when(ingredientRepository.findByNameIgnoreCase("New Spice")).thenReturn(Optional.empty());
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(newIngredient);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);
        when(recipeMapper.toDto(savedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.createRecipe(createRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(ingredientRepository).findByNameIgnoreCase("New Spice");
        verify(ingredientRepository).save(any(Ingredient.class));
        verify(recipeRepository).save(any(Recipe.class));
        verify(recipeMapper).toDto(savedRecipe);
      }
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("Should create recipe with ingredient ID not found, falls back to name")
    void shouldCreateRecipeWithIdNotFoundFallbackToName() {
      // Given
      RecipeIngredientDto ingredientDto = RecipeIngredientDto.builder()
          .ingredientId(999L) // Non-existent ID
          .ingredientName("Backup Ingredient")
          .quantity(BigDecimal.valueOf(100))
          .unit(IngredientUnit.G)
          .build();

      createRecipeRequest.setIngredients(Arrays.asList(ingredientDto));

      Ingredient backupIngredient = Ingredient.builder()
          .ingredientId(4L)
          .name("Backup Ingredient")
          .build();

      Recipe savedRecipe = Recipe.builder()
          .recipeId(1L)
          .userId(currentUserId)
          .build();

      RecipeDto recipeDto = RecipeDto.builder()
          .recipeId(1L)
          .userId(currentUserId)
          .build();

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
        when(ingredientRepository.findById(999L)).thenReturn(Optional.empty());
        when(ingredientRepository.findByNameIgnoreCase("Backup Ingredient")).thenReturn(Optional.of(backupIngredient));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);
        when(recipeMapper.toDto(savedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.createRecipe(createRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(ingredientRepository).findById(999L);
        verify(ingredientRepository).findByNameIgnoreCase("Backup Ingredient");
        verify(recipeRepository).save(any(Recipe.class));
        verify(recipeMapper).toDto(savedRecipe);
      }
    }

    @Test
    @Tag("error-processing")
    @DisplayName("Should throw exception when ingredient has neither ID nor name")
    void shouldThrowExceptionWhenIngredientHasNoIdOrName() {
      // Given
      RecipeIngredientDto ingredientDto = RecipeIngredientDto.builder()
          .quantity(BigDecimal.valueOf(100))
          .unit(IngredientUnit.G)
          .build(); // No ingredientId or ingredientName

      createRecipeRequest.setIngredients(Arrays.asList(ingredientDto));

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);

        // When & Then
        assertThatThrownBy(() -> recipeService.createRecipe(createRecipeRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Ingredient must have either a valid ID or name");

        verify(recipeRepository, never()).save(any(Recipe.class));
      }
    }

    @Test
    @Tag("error-processing")
    @DisplayName("Should throw exception when ingredient ID not found and name is null")
    void shouldThrowExceptionWhenIdNotFoundAndNameNull() {
      // Given
      RecipeIngredientDto ingredientDto = RecipeIngredientDto.builder()
          .ingredientId(999L) // Non-existent ID
          .ingredientName(null) // Null name
          .quantity(BigDecimal.valueOf(100))
          .unit(IngredientUnit.G)
          .build();

      createRecipeRequest.setIngredients(Arrays.asList(ingredientDto));

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
        when(ingredientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> recipeService.createRecipe(createRecipeRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Ingredient must have either a valid ID or name");

        verify(ingredientRepository).findById(999L);
        verify(recipeRepository, never()).save(any(Recipe.class));
      }
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("Should handle isOptional null value correctly")
    void shouldHandleIsOptionalNull() {
      // Given
      RecipeIngredientDto ingredientDto = RecipeIngredientDto.builder()
          .ingredientName("Test Ingredient")
          .quantity(BigDecimal.valueOf(100))
          .unit(IngredientUnit.G)
          .isOptional(null) // Null value
          .build();

      createRecipeRequest.setIngredients(Arrays.asList(ingredientDto));

      Ingredient ingredient = Ingredient.builder()
          .ingredientId(5L)
          .name("Test Ingredient")
          .build();

      Recipe savedRecipe = Recipe.builder()
          .recipeId(1L)
          .userId(currentUserId)
          .build();

      RecipeDto recipeDto = RecipeDto.builder()
          .recipeId(1L)
          .userId(currentUserId)
          .build();

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
        when(ingredientRepository.findByNameIgnoreCase("Test Ingredient")).thenReturn(Optional.of(ingredient));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);
        when(recipeMapper.toDto(savedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.createRecipe(createRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(recipeRepository).save(any(Recipe.class));
        verify(recipeMapper).toDto(savedRecipe);
      }
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("Should handle isOptional true value correctly")
    void shouldHandleIsOptionalTrue() {
      // Given
      RecipeIngredientDto ingredientDto = RecipeIngredientDto.builder()
          .ingredientName("Optional Ingredient")
          .quantity(BigDecimal.valueOf(50))
          .unit(IngredientUnit.G)
          .isOptional(true)
          .build();

      createRecipeRequest.setIngredients(Arrays.asList(ingredientDto));

      Ingredient ingredient = Ingredient.builder()
          .ingredientId(6L)
          .name("Optional Ingredient")
          .build();

      Recipe savedRecipe = Recipe.builder()
          .recipeId(1L)
          .userId(currentUserId)
          .build();

      RecipeDto recipeDto = RecipeDto.builder()
          .recipeId(1L)
          .userId(currentUserId)
          .build();

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
        when(ingredientRepository.findByNameIgnoreCase("Optional Ingredient")).thenReturn(Optional.of(ingredient));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);
        when(recipeMapper.toDto(savedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.createRecipe(createRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(recipeRepository).save(any(Recipe.class));
        verify(recipeMapper).toDto(savedRecipe);
      }
    }
  }

  @Nested
  @DisplayName("updateRecipe Tests")
  class UpdateRecipeTests {

    @Test
    @Tag("standard-processing")
    @DisplayName("Should update recipe successfully with valid ID and user authorization")
    void shouldUpdateRecipeSuccessfully() {
      // Given
      String recipeId = "1";
      Recipe updatedRecipe = Recipe.builder()
          .recipeId(1L)
          .userId(currentUserId)
          .title("Updated Recipe")
          .build();

      RecipeDto recipeDto = RecipeDto.builder()
          .recipeId(1L)
          .userId(currentUserId)
          .title("Updated Recipe")
          .build();

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
        doNothing().when(recipeMapper).updateRecipeFromRequest(any(UpdateRecipeRequest.class), any(Recipe.class));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(updatedRecipe);
        when(recipeMapper.toDto(updatedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.updateRecipe(recipeId, updateRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(recipeRepository).findById(1L);
        verify(recipeMapper).updateRecipeFromRequest(updateRecipeRequest, testRecipe);
        verify(recipeRepository).save(testRecipe);
        verify(recipeMapper).toDto(updatedRecipe);
      }
    }

    @Test
    @Tag("error-processing")
    @DisplayName("Should throw ResourceNotFoundException for invalid recipe ID format")
    void shouldThrowExceptionForInvalidRecipeIdFormat() {
      // Given
      String invalidRecipeId = "invalid-id";

      // When & Then
      assertThatThrownBy(() -> recipeService.updateRecipe(invalidRecipeId, updateRecipeRequest))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Invalid recipe ID: invalid-id");

      verify(recipeRepository, never()).findById(any());
      verify(recipeMapper, never()).updateRecipeFromRequest(any(), any());
    }

    @Test
    @Tag("error-processing")
    @DisplayName("Should throw ResourceNotFoundException when recipe not found")
    void shouldThrowExceptionWhenRecipeNotFound() {
      // Given
      String recipeId = "999";

      when(recipeRepository.findById(999L)).thenReturn(Optional.empty());

      // When & Then
      assertThatThrownBy(() -> recipeService.updateRecipe(recipeId, updateRecipeRequest))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Recipe not found: 999");

      verify(recipeRepository).findById(999L);
      verify(recipeMapper, never()).updateRecipeFromRequest(any(), any());
    }

    @Test
    @Tag("error-processing")
    @DisplayName("Should throw AccessDeniedException when user does not own recipe")
    void shouldThrowExceptionWhenUserDoesNotOwnRecipe() {
      // Given
      String recipeId = "1";
      UUID differentUserId = UUID.randomUUID();
      Recipe otherUserRecipe = Recipe.builder()
          .recipeId(1L)
          .userId(differentUserId) // Different user
          .build();

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(otherUserRecipe));

        // When & Then
        assertThatThrownBy(() -> recipeService.updateRecipe(recipeId, updateRecipeRequest))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessage("User does not have permission to update this recipe");

        verify(recipeRepository).findById(1L);
        verify(recipeMapper, never()).updateRecipeFromRequest(any(), any());
        verify(recipeRepository, never()).save(any());
      }
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("Should update recipe with zero ID")
    void shouldUpdateRecipeWithZeroId() {
      // Given
      String recipeId = "0";
      Recipe zeroIdRecipe = Recipe.builder()
          .recipeId(0L)
          .userId(currentUserId)
          .title("Orginal Recipe Title")
          .build();

      Recipe updatedRecipe = Recipe.builder()
          .recipeId(0L)
          .userId(currentUserId)
          .title("Updated Recipe Title")
          .build();

      RecipeDto recipeDto = RecipeDto.builder()
          .recipeId(0L)
          .userId(currentUserId)
          .title("Updated Recipe Title")
          .build();

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
        when(recipeRepository.findById(0L)).thenReturn(Optional.of(zeroIdRecipe));
        doNothing().when(recipeMapper).updateRecipeFromRequest(any(UpdateRecipeRequest.class), any(Recipe.class));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(updatedRecipe);
        when(recipeMapper.toDto(updatedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.updateRecipe(recipeId, updateRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(recipeRepository).findById(0L);
        verify(recipeMapper).updateRecipeFromRequest(updateRecipeRequest, zeroIdRecipe);
        verify(recipeMapper).toDto(updatedRecipe);
      }
    }
  }

  @Nested
  @DisplayName("deleteRecipe Tests")
  class DeleteRecipeTests {

    @Test
    @Tag("standard-processing")
    @DisplayName("Should return placeholder response for delete recipe")
    void shouldReturnPlaceholderResponseForDeleteRecipe() {
      // Given
      String recipeId = "1";

      // When
      ResponseEntity<String> response = recipeService.deleteRecipe(recipeId);

      // Then
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEqualTo("Delete Recipe - placeholder");
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("Should handle null recipe ID for delete")
    void shouldHandleNullRecipeIdForDelete() {
      // Given
      String recipeId = null;

      // When
      ResponseEntity<String> response = recipeService.deleteRecipe(recipeId);

      // Then
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEqualTo("Delete Recipe - placeholder");
    }
  }

  @Nested
  @DisplayName("getRecipe Tests")
  class GetRecipeTests {

    @Test
    @Tag("standard-processing")
    @DisplayName("Should return placeholder response for get recipe")
    void shouldReturnPlaceholderResponseForGetRecipe() {
      // Given
      String recipeId = "1";

      // When
      ResponseEntity<String> response = recipeService.getRecipe(recipeId);

      // Then
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEqualTo("Get Full Recipe - placeholder");
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("Should handle null recipe ID for get")
    void shouldHandleNullRecipeIdForGet() {
      // Given
      String recipeId = null;

      // When
      ResponseEntity<String> response = recipeService.getRecipe(recipeId);

      // Then
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEqualTo("Get Full Recipe - placeholder");
    }
  }

  @Nested
  @DisplayName("searchRecipes Tests")
  class SearchRecipesTests {

    @Test
    @Tag("standard-processing")
    @DisplayName("Should return placeholder response for search recipes")
    void shouldReturnPlaceholderResponseForSearchRecipes() {
      // When
      ResponseEntity<String> response = recipeService.searchRecipes();

      // Then
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEqualTo("Search Recipes - placeholder");
    }
  }

  @Nested
  @DisplayName("Edge Case Tests")
  class EdgeCaseTests {

    @Test
    @Tag("standard-processing")
    @DisplayName("Should create recipe with multiple ingredients of different types")
    void shouldCreateRecipeWithMultipleIngredients() {
      // Given
      RecipeIngredientDto ingredient1 = RecipeIngredientDto.builder()
          .ingredientId(1L)
          .quantity(BigDecimal.valueOf(500))
          .unit(IngredientUnit.G)
          .build();

      RecipeIngredientDto ingredient2 = RecipeIngredientDto.builder()
          .ingredientName("New Ingredient")
          .quantity(BigDecimal.valueOf(200))
          .unit(IngredientUnit.ML)
          .isOptional(true)
          .build();

      createRecipeRequest.setIngredients(Arrays.asList(ingredient1, ingredient2));

      Ingredient existingIngredient = Ingredient.builder()
          .ingredientId(1L)
          .name("Existing")
          .build();

      Ingredient newIngredient = Ingredient.builder()
          .ingredientId(2L)
          .name("New Ingredient")
          .build();

      Recipe savedRecipe = Recipe.builder()
          .recipeId(1L)
          .userId(currentUserId)
          .build();

      RecipeDto recipeDto = RecipeDto.builder()
          .recipeId(1L)
          .userId(currentUserId)
          .build();

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(existingIngredient));
        when(ingredientRepository.findByNameIgnoreCase("New Ingredient")).thenReturn(Optional.empty());
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(newIngredient);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);
        when(recipeMapper.toDto(savedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.createRecipe(createRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(ingredientRepository).findById(1L);
        verify(ingredientRepository).findByNameIgnoreCase("New Ingredient");
        verify(ingredientRepository).save(any(Ingredient.class));
        verify(recipeRepository).save(any(Recipe.class));
        verify(recipeMapper).toDto(savedRecipe);
      }
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("Should handle large recipe ID numbers")
    void shouldHandleLargeRecipeIdNumbers() {
      // Given
      String largeRecipeId = String.valueOf(Long.MAX_VALUE);
      Recipe largeIdRecipe = Recipe.builder()
          .recipeId(Long.MAX_VALUE)
          .userId(currentUserId)
          .title("Original Recipe Title")
          .build();

      Recipe updatedRecipe = Recipe.builder()
          .recipeId(Long.MAX_VALUE)
          .userId(currentUserId)
          .title("Updated Recipe Title")
          .build();

      RecipeDto recipeDto = RecipeDto.builder()
          .recipeId(Long.MAX_VALUE)
          .userId(currentUserId)
          .title("Updated Recipe Title")
          .build();

      try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
        mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
        when(recipeRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.of(largeIdRecipe));
        doNothing().when(recipeMapper).updateRecipeFromRequest(any(UpdateRecipeRequest.class), any(Recipe.class));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(updatedRecipe);
        when(recipeMapper.toDto(updatedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.updateRecipe(largeRecipeId, updateRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(recipeRepository).findById(Long.MAX_VALUE);
        verify(recipeMapper).toDto(updatedRecipe);
      }
    }

    @Test
    @Tag("error-processing")
    @DisplayName("Should handle negative recipe ID")
    void shouldHandleNegativeRecipeId() {
      // Given
      String negativeRecipeId = "-1";

      when(recipeRepository.findById(-1L)).thenReturn(Optional.empty());

      // When & Then
      assertThatThrownBy(() -> recipeService.updateRecipe(negativeRecipeId, updateRecipeRequest))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Recipe not found: -1");

      verify(recipeRepository).findById(-1L);
    }
  }
}
