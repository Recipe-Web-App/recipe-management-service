package com.recipe_manager.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

  @InjectMocks
  private IngredientService ingredientService;

  @Test
  @Tag("standard-processing")
  @DisplayName("Should get ingredients for recipe successfully")
  void shouldGetIngredientsForRecipeSuccessfully() {
    // Given
    String recipeId = "recipe-123";

    // When
    ResponseEntity<String> response = ingredientService.getIngredients(recipeId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Get Recipe Ingredients - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should scale ingredients successfully")
  void shouldScaleIngredientsSuccessfully() {
    // Given
    String recipeId = "recipe-123";
    float quantity = 2.5f;

    // When
    ResponseEntity<String> response = ingredientService.scaleIngredients(recipeId, quantity);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Scale Recipe Ingredients - placeholder");
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
  @Tag("standard-processing")
  @DisplayName("Should handle null recipe ID gracefully")
  void shouldHandleNullRecipeIdGracefully() {
    // When
    ResponseEntity<String> response = ingredientService.getIngredients(null);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Get Recipe Ingredients - placeholder");
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
    // When
    ResponseEntity<String> response = ingredientService.scaleIngredients("recipe-123", 0.0f);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Scale Recipe Ingredients - placeholder");
  }
}
