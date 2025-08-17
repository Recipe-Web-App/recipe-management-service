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

import com.recipe_manager.exception.BusinessException;
import com.recipe_manager.exception.ResourceNotFoundException;
import com.recipe_manager.model.dto.recipe.RecipeDto;
import com.recipe_manager.model.dto.recipe.RecipeIngredientDto;
import com.recipe_manager.model.dto.recipe.RecipeStepDto;
import com.recipe_manager.model.dto.recipe.RecipeTagDto;
import com.recipe_manager.model.dto.request.CreateRecipeRequest;
import com.recipe_manager.model.dto.request.SearchRecipesRequest;
import com.recipe_manager.model.dto.request.UpdateRecipeRequest;
import com.recipe_manager.model.dto.response.SearchRecipesResponse;
import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeStep;
import com.recipe_manager.model.entity.recipe.RecipeTag;
import com.recipe_manager.model.enums.DifficultyLevel;
import com.recipe_manager.model.enums.IngredientMatchMode;
import com.recipe_manager.model.enums.IngredientUnit;
import com.recipe_manager.model.mapper.RecipeMapper;
import com.recipe_manager.model.mapper.RecipeStepMapper;
import com.recipe_manager.repository.ingredient.IngredientRepository;
import com.recipe_manager.repository.recipe.RecipeRepository;
import com.recipe_manager.repository.recipe.RecipeTagRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
  private RecipeTagRepository recipeTagRepository;

  @Mock
  private RecipeMapper recipeMapper;

  @Mock
  private RecipeStepMapper recipeStepMapper;

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

    @Test
    @Tag("standard-processing")
    @DisplayName("Should create recipe with steps")
    void shouldCreateRecipeWithSteps() {
      // Given
      RecipeStepDto stepDto = RecipeStepDto.builder()
          .stepNumber(1)
          .instruction("Mix ingredients")
          .optional(false)
          .timerSeconds(300)
          .build();

      createRecipeRequest.setSteps(Arrays.asList(stepDto));

      RecipeStep step = RecipeStep.builder()
          .stepId(1L)
          .stepNumber(1)
          .instruction("Mix ingredients")
          .optional(false)
          .timerSeconds(300)
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
        when(recipeStepMapper.toEntityList(Arrays.asList(stepDto))).thenReturn(Arrays.asList(step));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);
        when(recipeMapper.toDto(savedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.createRecipe(createRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(recipeStepMapper).toEntityList(Arrays.asList(stepDto));
        verify(recipeRepository).save(any(Recipe.class));
        verify(recipeMapper).toDto(savedRecipe);
      }
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("Should create recipe with empty steps")
    void shouldCreateRecipeWithEmptySteps() {
      // Given
      createRecipeRequest.setSteps(new ArrayList<>());

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
        when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);
        when(recipeMapper.toDto(savedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.createRecipe(createRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(recipeStepMapper, never()).toEntityList(any());
        verify(recipeRepository).save(any(Recipe.class));
        verify(recipeMapper).toDto(savedRecipe);
      }
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("Should create recipe with null steps")
    void shouldCreateRecipeWithNullSteps() {
      // Given
      createRecipeRequest.setSteps(null);

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
        when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);
        when(recipeMapper.toDto(savedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.createRecipe(createRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(recipeStepMapper, never()).toEntityList(any());
        verify(recipeRepository).save(any(Recipe.class));
        verify(recipeMapper).toDto(savedRecipe);
      }
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("Should create recipe with existing tags")
    void shouldCreateRecipeWithExistingTags() {
      // Given
      RecipeTagDto tagDto = RecipeTagDto.builder()
          .name("Italian")
          .build();

      createRecipeRequest.setTags(Arrays.asList(tagDto));

      RecipeTag existingTag = RecipeTag.builder()
          .tagId(1L)
          .name("Italian")
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
        when(recipeTagRepository.findByNameIgnoreCase("Italian")).thenReturn(Optional.of(existingTag));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);
        when(recipeMapper.toDto(savedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.createRecipe(createRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(recipeTagRepository).findByNameIgnoreCase("Italian");
        verify(recipeTagRepository, never()).save(any(RecipeTag.class));
        verify(recipeRepository).save(any(Recipe.class));
        verify(recipeMapper).toDto(savedRecipe);
      }
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("Should create recipe with new tags")
    void shouldCreateRecipeWithNewTags() {
      // Given
      RecipeTagDto tagDto = RecipeTagDto.builder()
          .name("Mexican")
          .build();

      createRecipeRequest.setTags(Arrays.asList(tagDto));

      RecipeTag newTag = RecipeTag.builder()
          .tagId(2L)
          .name("Mexican")
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
        when(recipeTagRepository.findByNameIgnoreCase("Mexican")).thenReturn(Optional.empty());
        when(recipeTagRepository.save(any(RecipeTag.class))).thenReturn(newTag);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);
        when(recipeMapper.toDto(savedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.createRecipe(createRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(recipeTagRepository).findByNameIgnoreCase("Mexican");
        verify(recipeTagRepository).save(any(RecipeTag.class));
        verify(recipeRepository).save(any(Recipe.class));
        verify(recipeMapper).toDto(savedRecipe);
      }
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("Should create recipe with empty tags")
    void shouldCreateRecipeWithEmptyTags() {
      // Given
      createRecipeRequest.setTags(new ArrayList<>());

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
        when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);
        when(recipeMapper.toDto(savedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.createRecipe(createRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(recipeTagRepository, never()).findByNameIgnoreCase(anyString());
        verify(recipeTagRepository, never()).save(any(RecipeTag.class));
        verify(recipeRepository).save(any(Recipe.class));
        verify(recipeMapper).toDto(savedRecipe);
      }
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("Should create recipe with null tags")
    void shouldCreateRecipeWithNullTags() {
      // Given
      createRecipeRequest.setTags(null);

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
        when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);
        when(recipeMapper.toDto(savedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.createRecipe(createRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(recipeTagRepository, never()).findByNameIgnoreCase(anyString());
        verify(recipeTagRepository, never()).save(any(RecipeTag.class));
        verify(recipeRepository).save(any(Recipe.class));
        verify(recipeMapper).toDto(savedRecipe);
      }
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("Should create recipe with mixed existing and new tags")
    void shouldCreateRecipeWithMixedTags() {
      // Given
      RecipeTagDto existingTagDto = RecipeTagDto.builder()
          .name("Italian")
          .build();
      RecipeTagDto newTagDto = RecipeTagDto.builder()
          .name("Vegetarian")
          .build();

      createRecipeRequest.setTags(Arrays.asList(existingTagDto, newTagDto));

      RecipeTag existingTag = RecipeTag.builder()
          .tagId(1L)
          .name("Italian")
          .build();
      RecipeTag newTag = RecipeTag.builder()
          .tagId(2L)
          .name("Vegetarian")
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
        when(recipeTagRepository.findByNameIgnoreCase("Italian")).thenReturn(Optional.of(existingTag));
        when(recipeTagRepository.findByNameIgnoreCase("Vegetarian")).thenReturn(Optional.empty());
        when(recipeTagRepository.save(any(RecipeTag.class))).thenReturn(newTag);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);
        when(recipeMapper.toDto(savedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.createRecipe(createRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(recipeTagRepository).findByNameIgnoreCase("Italian");
        verify(recipeTagRepository).findByNameIgnoreCase("Vegetarian");
        verify(recipeTagRepository).save(any(RecipeTag.class));
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
        when(recipeRepository.save(any(Recipe.class))).thenReturn(updatedRecipe);
        when(recipeMapper.toDto(updatedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.updateRecipe(recipeId, updateRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(recipeRepository).findById(1L);
        verify(recipeRepository).save(any(Recipe.class));
        verify(recipeMapper).toDto(updatedRecipe);
      }
    }

    @Test
    @Tag("error-processing")
    @DisplayName("Should throw BusinessException for invalid recipe ID format")
    void shouldThrowExceptionForInvalidRecipeIdFormat() {
      // Given
      String invalidRecipeId = "invalid-id";

      // When & Then
      assertThatThrownBy(() -> recipeService.updateRecipe(invalidRecipeId, updateRecipeRequest))
          .isInstanceOf(BusinessException.class)
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
          .title("Original Recipe Title")
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
        when(recipeRepository.save(any(Recipe.class))).thenReturn(updatedRecipe);
        when(recipeMapper.toDto(updatedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.updateRecipe(recipeId, updateRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(recipeRepository).findById(0L);
        verify(recipeRepository).save(any(Recipe.class));
        verify(recipeMapper).toDto(updatedRecipe);
      }
    }
  }

  @Nested
  @DisplayName("deleteRecipe Tests")
  class DeleteRecipeTests {

    @Test
    @Tag("standard-processing")
    @DisplayName("Should successfully delete recipe for valid ID and owner")
    void shouldDeleteRecipeForValidIdAndOwner() {
      // Given
      String recipeId = "1";
      Long id = 1L;
      UUID currentUserId = UUID.randomUUID();

      Recipe recipe = Recipe.builder()
          .recipeId(id)
          .userId(currentUserId)
          .title("Test Recipe")
          .description("A test recipe")
          .build();

      try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
        securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
        when(recipeRepository.findById(id)).thenReturn(Optional.of(recipe));
        doNothing().when(recipeRepository).delete(recipe);

        // When
        ResponseEntity<Void> response = recipeService.deleteRecipe(recipeId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(recipeRepository).findById(id);
        verify(recipeRepository).delete(recipe);
      }
    }

    @Test
    @Tag("error-processing")
    @DisplayName("Should throw ResourceNotFoundException for non-existent recipe ID")
    void shouldThrowExceptionForNonExistentRecipeId() {
      // Given
      String recipeId = "999";
      Long id = 999L;

      when(recipeRepository.findById(id)).thenReturn(Optional.empty());

      // When & Then
      assertThatThrownBy(() -> recipeService.deleteRecipe(recipeId))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Recipe not found: " + recipeId);

      verify(recipeRepository).findById(id);
      verify(recipeRepository, never()).delete(any());
    }

    @Test
    @Tag("error-processing")
    @DisplayName("Should throw BusinessException for invalid recipe ID format")
    void shouldThrowExceptionForInvalidRecipeIdFormat() {
      // Given
      String recipeId = "invalid";

      // When & Then
      assertThatThrownBy(() -> recipeService.deleteRecipe(recipeId))
          .isInstanceOf(BusinessException.class)
          .hasMessage("Invalid recipe ID: " + recipeId);

      verify(recipeRepository, never()).findById(any());
      verify(recipeRepository, never()).delete(any());
    }

    @Test
    @Tag("error-processing")
    @DisplayName("Should throw AccessDeniedException when user does not own recipe")
    void shouldThrowExceptionWhenUserDoesNotOwnRecipe() {
      // Given
      String recipeId = "1";
      Long id = 1L;
      UUID currentUserId = UUID.randomUUID();
      UUID differentUserId = UUID.randomUUID();

      Recipe recipe = Recipe.builder()
          .recipeId(id)
          .userId(differentUserId) // Different user owns this recipe
          .title("Test Recipe")
          .description("A test recipe")
          .build();

      try (MockedStatic<SecurityUtils> securityUtilsMock = Mockito.mockStatic(SecurityUtils.class)) {
        securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(currentUserId);
        when(recipeRepository.findById(id)).thenReturn(Optional.of(recipe));

        // When & Then
        assertThatThrownBy(() -> recipeService.deleteRecipe(recipeId))
            .isInstanceOf(AccessDeniedException.class)
            .hasMessage("User does not have permission to delete this recipe");

        verify(recipeRepository).findById(id);
        verify(recipeRepository, never()).delete(any());
      }
    }

    @Test
    @Tag("error-processing")
    @DisplayName("Should throw BusinessException for null recipe ID")
    void shouldThrowExceptionForNullRecipeId() {
      // Given
      String recipeId = null;

      // When & Then
      assertThatThrownBy(() -> recipeService.deleteRecipe(recipeId))
          .isInstanceOf(BusinessException.class)
          .hasMessage("Invalid recipe ID: null");

      verify(recipeRepository, never()).findById(any());
      verify(recipeRepository, never()).delete(any());
    }
  }

  @Nested
  @DisplayName("getRecipe Tests")
  class GetRecipeTests {

    @Test
    @Tag("standard-processing")
    @DisplayName("Should return recipe for valid recipe ID")
    void shouldReturnRecipeForValidId() {
      // Given
      String recipeId = "1";
      Long id = 1L;
      UUID currentUserId = UUID.randomUUID();

      Recipe recipe = Recipe.builder()
          .recipeId(id)
          .userId(currentUserId)
          .title("Test Recipe")
          .description("A test recipe")
          .build();

      RecipeDto recipeDto = RecipeDto.builder()
          .recipeId(id)
          .userId(currentUserId)
          .title("Test Recipe")
          .description("A test recipe")
          .build();

      when(recipeRepository.findById(id)).thenReturn(Optional.of(recipe));
      when(recipeMapper.toDto(recipe)).thenReturn(recipeDto);

      // When
      ResponseEntity<RecipeDto> response = recipeService.getRecipe(recipeId);

      // Then
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEqualTo(recipeDto);
      verify(recipeRepository).findById(id);
      verify(recipeMapper).toDto(recipe);
    }

    @Test
    @Tag("error-processing")
    @DisplayName("Should throw ResourceNotFoundException for non-existent recipe ID")
    void shouldThrowExceptionForNonExistentRecipeId() {
      // Given
      String recipeId = "999";
      Long id = 999L;

      when(recipeRepository.findById(id)).thenReturn(Optional.empty());

      // When & Then
      assertThatThrownBy(() -> recipeService.getRecipe(recipeId))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessage("Recipe not found: " + recipeId);

      verify(recipeRepository).findById(id);
      verify(recipeMapper, never()).toDto(any());
    }

    @Test
    @Tag("error-processing")
    @DisplayName("Should throw BusinessException for invalid recipe ID format")
    void shouldThrowExceptionForInvalidRecipeIdFormat() {
      // Given
      String recipeId = "invalid";

      // When & Then
      assertThatThrownBy(() -> recipeService.getRecipe(recipeId))
          .isInstanceOf(BusinessException.class)
          .hasMessage("Invalid recipe ID: " + recipeId);

      verify(recipeRepository, never()).findById(any());
      verify(recipeMapper, never()).toDto(any());
    }

    @Test
    @Tag("error-processing")
    @DisplayName("Should throw BusinessException for null recipe ID")
    void shouldThrowExceptionForNullRecipeId() {
      // Given
      String recipeId = null;

      // When & Then
      assertThatThrownBy(() -> recipeService.getRecipe(recipeId))
          .isInstanceOf(BusinessException.class)
          .hasMessage("Invalid recipe ID: null");

      verify(recipeRepository, never()).findById(any());
      verify(recipeMapper, never()).toDto(any());
    }
  }

  @Nested
  @DisplayName("getAllRecipes Tests")
  class GetAllRecipesTests {

    private Pageable pageable;
    private Recipe recipe1;
    private Recipe recipe2;
    private RecipeDto recipeDto1;
    private RecipeDto recipeDto2;
    private Page<Recipe> recipePage;

    @BeforeEach
    void setUp() {
      pageable = PageRequest.of(0, 20);

      recipe1 = new Recipe();
      recipe1.setRecipeId(1L);
      recipe1.setTitle("Test Recipe 1");
      recipe1.setDescription("Test Description 1");

      recipe2 = new Recipe();
      recipe2.setRecipeId(2L);
      recipe2.setTitle("Test Recipe 2");
      recipe2.setDescription("Test Description 2");

      recipeDto1 = RecipeDto.builder()
          .recipeId(1L)
          .title("Test Recipe 1")
          .description("Test Description 1")
          .build();

      recipeDto2 = RecipeDto.builder()
          .recipeId(2L)
          .title("Test Recipe 2")
          .description("Test Description 2")
          .build();

      recipePage = new PageImpl<>(Arrays.asList(recipe1, recipe2), pageable, 2);
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("should return paginated recipes successfully")
    void shouldReturnPaginatedRecipesSuccessfully() {
      // Given
      when(recipeRepository.findAll(pageable)).thenReturn(recipePage);
      when(recipeMapper.toDto(recipe1)).thenReturn(recipeDto1);
      when(recipeMapper.toDto(recipe2)).thenReturn(recipeDto2);

      // When
      var response = recipeService.getAllRecipes(pageable);

      // Then
      assertThat(response).isNotNull();
      assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

      SearchRecipesResponse responseBody = response.getBody();
      assertThat(responseBody).isNotNull();
      assertThat(responseBody.getRecipes()).hasSize(2);
      assertThat(responseBody.getRecipes().get(0)).isEqualTo(recipeDto1);
      assertThat(responseBody.getRecipes().get(1)).isEqualTo(recipeDto2);
      assertThat(responseBody.getPage()).isEqualTo(0);
      assertThat(responseBody.getSize()).isEqualTo(20);
      assertThat(responseBody.getTotalElements()).isEqualTo(2L);
      assertThat(responseBody.getTotalPages()).isEqualTo(1);
      assertThat(responseBody.isFirst()).isTrue();
      assertThat(responseBody.isLast()).isTrue();
      assertThat(responseBody.getNumberOfElements()).isEqualTo(2);
      assertThat(responseBody.isEmpty()).isFalse();

      verify(recipeRepository).findAll(pageable);
      verify(recipeMapper).toDto(recipe1);
      verify(recipeMapper).toDto(recipe2);
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("should return empty page when no recipes exist")
    void shouldReturnEmptyPageWhenNoRecipesExist() {
      // Given
      Page<Recipe> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
      when(recipeRepository.findAll(pageable)).thenReturn(emptyPage);

      // When
      var response = recipeService.getAllRecipes(pageable);

      // Then
      assertThat(response).isNotNull();
      assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

      SearchRecipesResponse responseBody = response.getBody();
      assertThat(responseBody).isNotNull();
      assertThat(responseBody.getRecipes()).isEmpty();
      assertThat(responseBody.getPage()).isEqualTo(0);
      assertThat(responseBody.getSize()).isEqualTo(20);
      assertThat(responseBody.getTotalElements()).isEqualTo(0L);
      assertThat(responseBody.getTotalPages()).isEqualTo(0);
      assertThat(responseBody.isFirst()).isTrue();
      assertThat(responseBody.isLast()).isTrue();
      assertThat(responseBody.getNumberOfElements()).isEqualTo(0);
      assertThat(responseBody.isEmpty()).isTrue();

      verify(recipeRepository).findAll(pageable);
      verify(recipeMapper, never()).toDto(any());
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("should handle pagination correctly for multiple pages")
    void shouldHandlePaginationCorrectlyForMultiplePages() {
      // Given
      Pageable secondPage = PageRequest.of(1, 1);
      Page<Recipe> pagedRecipes = new PageImpl<>(Arrays.asList(recipe2), secondPage, 2);
      when(recipeRepository.findAll(secondPage)).thenReturn(pagedRecipes);
      when(recipeMapper.toDto(recipe2)).thenReturn(recipeDto2);

      // When
      var response = recipeService.getAllRecipes(secondPage);

      // Then
      assertThat(response).isNotNull();
      assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

      SearchRecipesResponse responseBody = response.getBody();
      assertThat(responseBody).isNotNull();
      assertThat(responseBody.getRecipes()).hasSize(1);
      assertThat(responseBody.getRecipes().get(0)).isEqualTo(recipeDto2);
      assertThat(responseBody.getPage()).isEqualTo(1);
      assertThat(responseBody.getSize()).isEqualTo(1);
      assertThat(responseBody.getTotalElements()).isEqualTo(2L);
      assertThat(responseBody.getTotalPages()).isEqualTo(2);
      assertThat(responseBody.isFirst()).isFalse();
      assertThat(responseBody.isLast()).isTrue();
      assertThat(responseBody.getNumberOfElements()).isEqualTo(1);
      assertThat(responseBody.isEmpty()).isFalse();

      verify(recipeRepository).findAll(secondPage);
      verify(recipeMapper).toDto(recipe2);
    }
  }

  @Nested
  @DisplayName("searchRecipes Tests")
  class SearchRecipesTests {

    private SearchRecipesRequest searchRequest;
    private Pageable pageable;
    private Recipe recipe1;
    private Recipe recipe2;
    private RecipeDto recipeDto1;
    private RecipeDto recipeDto2;
    private Page<Recipe> recipePage;

    @BeforeEach
    void setUpSearchTests() {
      searchRequest = new SearchRecipesRequest();
      pageable = PageRequest.of(0, 10);

      recipe1 = Recipe.builder()
          .recipeId(1L)
          .title("Chicken Pasta")
          .description("Delicious pasta dish")
          .difficulty(DifficultyLevel.EASY)
          .preparationTime(30)
          .cookingTime(20)
          .servings(BigDecimal.valueOf(4))
          .userId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
          .build();

      recipe2 = Recipe.builder()
          .recipeId(2L)
          .title("Beef Stir Fry")
          .description("Quick and tasty")
          .difficulty(DifficultyLevel.MEDIUM)
          .preparationTime(15)
          .cookingTime(10)
          .servings(BigDecimal.valueOf(2))
          .userId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
          .build();

      recipeDto1 = RecipeDto.builder()
          .recipeId(1L)
          .title("Chicken Pasta")
          .description("Delicious pasta dish")
          .build();

      recipeDto2 = RecipeDto.builder()
          .recipeId(2L)
          .title("Beef Stir Fry")
          .description("Quick and tasty")
          .build();

      recipePage = new PageImpl<>(Arrays.asList(recipe1, recipe2), pageable, 2);
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("Should search recipes successfully with all criteria")
    void shouldSearchRecipesSuccessfullyWithAllCriteria() {
      // Given
      searchRequest.setRecipeNameQuery("Chicken");
      searchRequest.setIngredients(Arrays.asList("chicken", "pasta"));
      searchRequest.setIngredientMatchMode(IngredientMatchMode.AND);
      searchRequest.setMaxPreparationTime(45);
      searchRequest.setMaxCookingTime(30);
      searchRequest.setMinServings(BigDecimal.valueOf(2));
      searchRequest.setMaxServings(BigDecimal.valueOf(6));
      searchRequest.setDifficulty(DifficultyLevel.EASY);

      when(recipeRepository.searchRecipes(
          searchRequest.getRecipeNameQuery(),
          "EASY",
          searchRequest.getMaxCookingTime(),
          searchRequest.getMaxPreparationTime(),
          searchRequest.getMinServings(),
          searchRequest.getMaxServings(),
          new String[]{"chicken", "pasta"},
          new String[0],
          pageable)).thenReturn(recipePage);
      when(recipeMapper.toDto(recipe1)).thenReturn(recipeDto1);
      when(recipeMapper.toDto(recipe2)).thenReturn(recipeDto2);

      // When
      ResponseEntity<SearchRecipesResponse> response = recipeService.searchRecipes(searchRequest, pageable);

      // Then
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getRecipes()).hasSize(2);
      assertThat(response.getBody().getRecipes().get(0).getTitle()).isEqualTo("Chicken Pasta");
      assertThat(response.getBody().getRecipes().get(1).getTitle()).isEqualTo("Beef Stir Fry");
      assertThat(response.getBody().getPage()).isEqualTo(0);
      assertThat(response.getBody().getSize()).isEqualTo(10);
      assertThat(response.getBody().getTotalElements()).isEqualTo(2);
      assertThat(response.getBody().getTotalPages()).isEqualTo(1);

      verify(recipeRepository).searchRecipes(
          searchRequest.getRecipeNameQuery(),
          "EASY",
          searchRequest.getMaxCookingTime(),
          searchRequest.getMaxPreparationTime(),
          searchRequest.getMinServings(),
          searchRequest.getMaxServings(),
          new String[]{"chicken", "pasta"},
          new String[0],
          pageable);
      verify(recipeMapper).toDto(recipe1);
      verify(recipeMapper).toDto(recipe2);
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("Should return empty search results")
    void shouldReturnEmptySearchResults() {
      // Given
      searchRequest.setRecipeNameQuery("NonExistentRecipe");
      Page<Recipe> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

      when(recipeRepository.searchRecipes(
          searchRequest.getRecipeNameQuery(),
          null,
          searchRequest.getMaxCookingTime(),
          searchRequest.getMaxPreparationTime(),
          searchRequest.getMinServings(),
          searchRequest.getMaxServings(),
          new String[0],
          new String[0],
          pageable)).thenReturn(emptyPage);

      // When
      ResponseEntity<SearchRecipesResponse> response = recipeService.searchRecipes(searchRequest, pageable);

      // Then
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getRecipes()).isEmpty();
      assertThat(response.getBody().getTotalElements()).isEqualTo(0);
      assertThat(response.getBody().isEmpty()).isTrue();

      verify(recipeRepository).searchRecipes(
          searchRequest.getRecipeNameQuery(),
          null,
          searchRequest.getMaxCookingTime(),
          searchRequest.getMaxPreparationTime(),
          searchRequest.getMinServings(),
          searchRequest.getMaxServings(),
          new String[0],
          new String[0],
          pageable);
      verify(recipeMapper, never()).toDto(any(Recipe.class));
    }

    @Test
    @Tag("standard-processing")
    @DisplayName("Should handle pagination correctly")
    void shouldHandlePaginationCorrectly() {
      // Given
      Pageable secondPage = PageRequest.of(1, 1);
      Page<Recipe> paginatedPage = new PageImpl<>(Arrays.asList(recipe2), secondPage, 2);

      when(recipeRepository.searchRecipes(
          searchRequest.getRecipeNameQuery(),
          null,
          searchRequest.getMaxCookingTime(),
          searchRequest.getMaxPreparationTime(),
          searchRequest.getMinServings(),
          searchRequest.getMaxServings(),
          new String[0],
          new String[0],
          secondPage)).thenReturn(paginatedPage);
      when(recipeMapper.toDto(recipe2)).thenReturn(recipeDto2);

      // When
      ResponseEntity<SearchRecipesResponse> response = recipeService.searchRecipes(searchRequest, secondPage);

      // Then
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getRecipes()).hasSize(1);
      assertThat(response.getBody().getPage()).isEqualTo(1);
      assertThat(response.getBody().getSize()).isEqualTo(1);
      assertThat(response.getBody().getTotalElements()).isEqualTo(2);
      assertThat(response.getBody().getTotalPages()).isEqualTo(2);
      assertThat(response.getBody().isFirst()).isFalse();
      assertThat(response.getBody().isLast()).isTrue();

      verify(recipeRepository).searchRecipes(
          searchRequest.getRecipeNameQuery(),
          null,
          searchRequest.getMaxCookingTime(),
          searchRequest.getMaxPreparationTime(),
          searchRequest.getMinServings(),
          searchRequest.getMaxServings(),
          new String[0],
          new String[0],
          secondPage);
    }

    @Test
    @Tag("error-handling")
    @DisplayName("Should throw exception when repository throws exception")
    void shouldThrowExceptionWhenRepositoryThrowsException() {
      // Given
      when(recipeRepository.searchRecipes(
          searchRequest.getRecipeNameQuery(),
          null,
          searchRequest.getMaxCookingTime(),
          searchRequest.getMaxPreparationTime(),
          searchRequest.getMinServings(),
          searchRequest.getMaxServings(),
          new String[0],
          new String[0],
          pageable))
          .thenThrow(new RuntimeException("Database error"));

      // When & Then
      assertThatThrownBy(() -> recipeService.searchRecipes(searchRequest, pageable))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Database error");

      verify(recipeRepository).searchRecipes(
          searchRequest.getRecipeNameQuery(),
          null,
          searchRequest.getMaxCookingTime(),
          searchRequest.getMaxPreparationTime(),
          searchRequest.getMinServings(),
          searchRequest.getMaxServings(),
          new String[0],
          new String[0],
          pageable);
      verify(recipeMapper, never()).toDto(any(Recipe.class));
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
        when(recipeRepository.save(any(Recipe.class))).thenReturn(updatedRecipe);
        when(recipeMapper.toDto(updatedRecipe)).thenReturn(recipeDto);

        // When
        ResponseEntity<RecipeDto> response = recipeService.updateRecipe(largeRecipeId, updateRecipeRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(recipeDto);
        verify(recipeRepository).findById(Long.MAX_VALUE);
        verify(recipeRepository).save(any(Recipe.class));
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
