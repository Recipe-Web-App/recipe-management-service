package com.recipe_manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.recipe_manager.exception.BusinessException;
import com.recipe_manager.model.dto.recipe.RecipeIngredientDto;
import com.recipe_manager.model.dto.response.RecipeIngredientsResponse;
import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeIngredient;
import com.recipe_manager.model.enums.IngredientUnit;
import com.recipe_manager.model.mapper.RecipeIngredientMapper;
import com.recipe_manager.repository.recipe.RecipeIngredientRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for IngredientService.
 *
 * <p>
 * Tests cover all placeholder methods:
 * <ul>
 * <li>getIngredients</li>
 * <li>scaleIngredients</li>
 * <li>generateShoppingList</li>
 * <li>addComment</li>
 * <li>editComment</li>
 * <li>deleteComment</li>
 * <li>addMedia</li>
 * <li>updateMedia</li>
 * <li>deleteMedia</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class IngredientServiceTest {

  @Mock
  private RecipeIngredientRepository recipeIngredientRepository;

  @Mock
  private RecipeIngredientMapper recipeIngredientMapper;

  @InjectMocks
  private IngredientService ingredientService;

  @Test
  @Tag("standard-processing")
  @DisplayName("Should get ingredients for recipe successfully")
  void shouldGetIngredientsForRecipeSuccessfully() {
    // Given
    String recipeId = "123";
    Long id = 123L;

    RecipeIngredient ingredient1 = createMockRecipeIngredient(id, 1L, "Salt", new BigDecimal("1.5"),
        IngredientUnit.TSP);
    RecipeIngredient ingredient2 = createMockRecipeIngredient(id, 2L, "Pepper", new BigDecimal("0.5"),
        IngredientUnit.TSP);
    List<RecipeIngredient> ingredients = Arrays.asList(ingredient1, ingredient2);

    RecipeIngredientDto dto1 = createMockRecipeIngredientDto(id, 1L, "Salt", new BigDecimal("1.5"), IngredientUnit.TSP);
    RecipeIngredientDto dto2 = createMockRecipeIngredientDto(id, 2L, "Pepper", new BigDecimal("0.5"),
        IngredientUnit.TSP);
    List<RecipeIngredientDto> dtos = Arrays.asList(dto1, dto2);

    when(recipeIngredientRepository.findByRecipeRecipeId(id)).thenReturn(ingredients);
    when(recipeIngredientMapper.toDtoList(ingredients)).thenReturn(dtos);

    // When
    ResponseEntity<RecipeIngredientsResponse> response = ingredientService.getIngredients(recipeId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRecipeId()).isEqualTo(id);
    assertThat(response.getBody().getIngredients()).hasSize(2);
    assertThat(response.getBody().getTotalCount()).isEqualTo(2);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should scale ingredients successfully")
  void shouldScaleIngredientsSuccessfully() {
    // Given
    String recipeId = "123";
    Long id = 123L;
    float scaleFactor = 2.5f;

    RecipeIngredient ingredient1 = createMockRecipeIngredient(id, 1L, "Salt", new BigDecimal("1.0"),
        IngredientUnit.TSP);
    RecipeIngredient ingredient2 = createMockRecipeIngredient(id, 2L, "Pepper", new BigDecimal("0.5"),
        IngredientUnit.TSP);
    List<RecipeIngredient> ingredients = Arrays.asList(ingredient1, ingredient2);

    RecipeIngredientDto scaledDto1 = createMockRecipeIngredientDto(id, 1L, "Salt", new BigDecimal("2.5"), IngredientUnit.TSP);
    RecipeIngredientDto scaledDto2 = createMockRecipeIngredientDto(id, 2L, "Pepper", new BigDecimal("1.25"),
        IngredientUnit.TSP);
    List<RecipeIngredientDto> scaledDtos = Arrays.asList(scaledDto1, scaledDto2);

    when(recipeIngredientRepository.findByRecipeRecipeId(id)).thenReturn(ingredients);
    when(recipeIngredientMapper.toDtoListWithScale(ingredients, scaleFactor)).thenReturn(scaledDtos);

    // When
    ResponseEntity<RecipeIngredientsResponse> response = ingredientService.scaleIngredients(recipeId, scaleFactor);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRecipeId()).isEqualTo(id);
    assertThat(response.getBody().getIngredients()).hasSize(2);
    assertThat(response.getBody().getTotalCount()).isEqualTo(2);
    assertThat(response.getBody().getIngredients().get(0).getQuantity()).isEqualTo(new BigDecimal("2.5"));
    assertThat(response.getBody().getIngredients().get(1).getQuantity()).isEqualTo(new BigDecimal("1.25"));
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should generate shopping list successfully")
  void shouldGenerateShoppingListSuccessfully() {
    // Given
    String recipeId = "recipe-123";

    // When
    ResponseEntity<String> response = ingredientService.generateShoppingList(recipeId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Generate Shopping List - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should add comment to ingredient successfully")
  void shouldAddCommentToIngredientSuccessfully() {
    // Given
    String recipeId = "recipe-123";
    String ingredientId = "ingredient-456";

    // When
    ResponseEntity<String> response = ingredientService.addComment(recipeId, ingredientId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Add Comment to Ingredient - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should edit comment on ingredient successfully")
  void shouldEditCommentOnIngredientSuccessfully() {
    // Given
    String recipeId = "recipe-123";
    String ingredientId = "ingredient-456";

    // When
    ResponseEntity<String> response = ingredientService.editComment(recipeId, ingredientId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Edit Comment on Ingredient - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should delete comment from ingredient successfully")
  void shouldDeleteCommentFromIngredientSuccessfully() {
    // Given
    String recipeId = "recipe-123";
    String ingredientId = "ingredient-456";

    // When
    ResponseEntity<String> response = ingredientService.deleteComment(recipeId, ingredientId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Delete Comment from Ingredient - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should add media to ingredient successfully")
  void shouldAddMediaToIngredientSuccessfully() {
    // Given
    String recipeId = "recipe-123";
    String ingredientId = "ingredient-456";

    // When
    ResponseEntity<String> response = ingredientService.addMedia(recipeId, ingredientId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Add Media Ref to Ingredient - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should update media on ingredient successfully")
  void shouldUpdateMediaOnIngredientSuccessfully() {
    // Given
    String recipeId = "recipe-123";
    String ingredientId = "ingredient-456";

    // When
    ResponseEntity<String> response = ingredientService.updateMedia(recipeId, ingredientId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Update Media Ref on Ingredient - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should delete media from ingredient successfully")
  void shouldDeleteMediaFromIngredientSuccessfully() {
    // Given
    String recipeId = "recipe-123";
    String ingredientId = "ingredient-456";

    // When
    ResponseEntity<String> response = ingredientService.deleteMedia(recipeId, ingredientId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Delete Media Ref from Ingredient - placeholder");
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle invalid recipe ID gracefully")
  void shouldHandleInvalidRecipeIdGracefully() {
    // Given
    String invalidRecipeId = "invalid";

    // When/Then
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      ingredientService.getIngredients(invalidRecipeId);
    });

    assertThat(exception.getMessage()).isEqualTo("Invalid recipe ID: invalid");
  }

  private RecipeIngredient createMockRecipeIngredient(Long recipeId, Long ingredientId, String ingredientName,
      BigDecimal quantity, IngredientUnit unit) {
    Recipe recipe = new Recipe();
    recipe.setRecipeId(recipeId);

    Ingredient ingredient = new Ingredient();
    ingredient.setIngredientId(ingredientId);
    ingredient.setName(ingredientName);

    RecipeIngredient recipeIngredient = new RecipeIngredient();
    recipeIngredient.setRecipe(recipe);
    recipeIngredient.setIngredient(ingredient);
    recipeIngredient.setQuantity(quantity);
    recipeIngredient.setUnit(unit);
    recipeIngredient.setIsOptional(false);

    return recipeIngredient;
  }

  private RecipeIngredientDto createMockRecipeIngredientDto(Long recipeId, Long ingredientId, String ingredientName,
      BigDecimal quantity, IngredientUnit unit) {
    return RecipeIngredientDto.builder()
        .recipeId(recipeId)
        .ingredientId(ingredientId)
        .ingredientName(ingredientName)
        .quantity(quantity)
        .unit(unit)
        .isOptional(false)
        .build();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle null ingredient ID gracefully")
  void shouldHandleNullIngredientIdGracefully() {
    // When
    ResponseEntity<String> response = ingredientService.addComment("recipe-123", null);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Add Comment to Ingredient - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle zero quantity scaling gracefully")
  void shouldHandleZeroQuantityScalingGracefully() {
    // Given
    String recipeId = "123";
    Long id = 123L;
    float scaleFactor = 0.0f;

    RecipeIngredient ingredient1 = createMockRecipeIngredient(id, 1L, "Salt", new BigDecimal("1.0"),
        IngredientUnit.TSP);
    List<RecipeIngredient> ingredients = Arrays.asList(ingredient1);

    RecipeIngredientDto scaledDto1 = createMockRecipeIngredientDto(id, 1L, "Salt", new BigDecimal("0.0"), IngredientUnit.TSP);
    List<RecipeIngredientDto> scaledDtos = Arrays.asList(scaledDto1);

    when(recipeIngredientRepository.findByRecipeRecipeId(id)).thenReturn(ingredients);
    when(recipeIngredientMapper.toDtoListWithScale(ingredients, scaleFactor)).thenReturn(scaledDtos);

    // When
    ResponseEntity<RecipeIngredientsResponse> response = ingredientService.scaleIngredients(recipeId, scaleFactor);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getIngredients().get(0).getQuantity()).isEqualTo(new BigDecimal("0.0"));
  }
}
