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
 * Unit tests for MediaService.
 *
 * <p>
 * Tests cover all placeholder methods:
 * <ul>
 * <li>addMediaToRecipe</li>
 * <li>updateMediaOnRecipe</li>
 * <li>deleteMediaFromRecipe</li>
 * <li>addMediaToIngredient</li>
 * <li>updateMediaOnIngredient</li>
 * <li>deleteMediaFromIngredient</li>
 * <li>addMediaToStep</li>
 * <li>updateMediaOnStep</li>
 * <li>deleteMediaFromStep</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class MediaServiceTest {

  @InjectMocks
  private MediaService mediaService;

  @Test
  @Tag("standard-processing")
  @DisplayName("Should add media to recipe successfully")
  void shouldAddMediaToRecipeSuccessfully() {
    // Given
    String recipeId = "recipe-123";

    // When
    ResponseEntity<String> response = mediaService.addMediaToRecipe(recipeId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Add Media Ref to Recipe - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should update media on recipe successfully")
  void shouldUpdateMediaOnRecipeSuccessfully() {
    // Given
    String recipeId = "recipe-123";

    // When
    ResponseEntity<String> response = mediaService.updateMediaOnRecipe(recipeId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Update Media Ref on Recipe - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should delete media from recipe successfully")
  void shouldDeleteMediaFromRecipeSuccessfully() {
    // Given
    String recipeId = "recipe-123";

    // When
    ResponseEntity<String> response = mediaService.deleteMediaFromRecipe(recipeId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Delete Media Ref from Recipe - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should add media to ingredient successfully")
  void shouldAddMediaToIngredientSuccessfully() {
    // Given
    String recipeId = "recipe-123";
    String ingredientId = "ingredient-456";

    // When
    ResponseEntity<String> response = mediaService.addMediaToIngredient(recipeId, ingredientId);

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
    ResponseEntity<String> response = mediaService.updateMediaOnIngredient(recipeId, ingredientId);

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
    ResponseEntity<String> response = mediaService.deleteMediaFromIngredient(recipeId, ingredientId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Delete Media Ref from Ingredient - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should add media to step successfully")
  void shouldAddMediaToStepSuccessfully() {
    // Given
    String recipeId = "recipe-123";
    String stepId = "step-789";

    // When
    ResponseEntity<String> response = mediaService.addMediaToStep(recipeId, stepId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Add Media Ref to Step - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should update media on step successfully")
  void shouldUpdateMediaOnStepSuccessfully() {
    // Given
    String recipeId = "recipe-123";
    String stepId = "step-789";

    // When
    ResponseEntity<String> response = mediaService.updateMediaOnStep(recipeId, stepId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Update Media Ref on Step - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should delete media from step successfully")
  void shouldDeleteMediaFromStepSuccessfully() {
    // Given
    String recipeId = "recipe-123";
    String stepId = "step-789";

    // When
    ResponseEntity<String> response = mediaService.deleteMediaFromStep(recipeId, stepId);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Delete Media Ref from Step - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle null recipe ID gracefully")
  void shouldHandleNullRecipeIdGracefully() {
    // When
    ResponseEntity<String> response = mediaService.addMediaToRecipe(null);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Add Media Ref to Recipe - placeholder");
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle null ingredient ID gracefully")
  void shouldHandleNullIngredientIdGracefully() {
    // When
    ResponseEntity<String> response = mediaService.addMediaToIngredient("recipe-123", null);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo("Add Media Ref to Ingredient - placeholder");
  }
}
