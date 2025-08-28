package com.recipe_manager.repository.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.recipe_manager.model.entity.ingredient.Ingredient;
import com.recipe_manager.model.entity.media.IngredientMedia;
import com.recipe_manager.model.entity.media.IngredientMediaId;
import com.recipe_manager.model.entity.media.Media;
import com.recipe_manager.model.entity.recipe.Recipe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for IngredientMediaRepository.
 *
 * Tests repository methods for managing ingredient-media associations.
 *
 * Note: These are mock-based unit tests since the repository depends on JPA
 * infrastructure that is not easily testable in isolation.
 */
@Tag("unit")
class IngredientMediaRepositoryTest {

  private IngredientMediaRepository ingredientMediaRepository;

  @BeforeEach
  void setUp() {
    ingredientMediaRepository = mock(IngredientMediaRepository.class);
  }

  @Test
  @DisplayName("Should find all ingredient media for a recipe")
  void shouldFindAllIngredientMediaForRecipe() {
    // Given
    Long recipeId = 1L;
    List<IngredientMedia> expectedMedia = Arrays.asList(
        createTestIngredientMedia(recipeId, 10L, 100L),
        createTestIngredientMedia(recipeId, 20L, 200L),
        createTestIngredientMedia(recipeId, 30L, 300L));
    when(ingredientMediaRepository.findByIdRecipeId(recipeId)).thenReturn(expectedMedia);

    // When
    List<IngredientMedia> result = ingredientMediaRepository.findByIdRecipeId(recipeId);

    // Then
    assertThat(result).hasSize(3);
    assertThat(result).containsExactlyElementsOf(expectedMedia);
    assertThat(result).allMatch(im -> im.getId().getRecipeId().equals(recipeId));
  }

  @Test
  @DisplayName("Should return empty list when recipe has no ingredient media")
  void shouldReturnEmptyListWhenRecipeHasNoIngredientMedia() {
    // Given
    Long recipeId = 1L;
    when(ingredientMediaRepository.findByIdRecipeId(recipeId)).thenReturn(Collections.emptyList());

    // When
    List<IngredientMedia> result = ingredientMediaRepository.findByIdRecipeId(recipeId);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should find all media for a specific ingredient")
  void shouldFindAllMediaForSpecificIngredient() {
    // Given
    Long ingredientId = 10L;
    List<IngredientMedia> expectedMedia = Arrays.asList(
        createTestIngredientMedia(1L, ingredientId, 100L),
        createTestIngredientMedia(2L, ingredientId, 200L));
    when(ingredientMediaRepository.findByIdIngredientId(ingredientId)).thenReturn(expectedMedia);

    // When
    List<IngredientMedia> result = ingredientMediaRepository.findByIdIngredientId(ingredientId);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(expectedMedia);
    assertThat(result).allMatch(im -> im.getId().getIngredientId().equals(ingredientId));
  }

  @Test
  @DisplayName("Should find all ingredients using specific media")
  void shouldFindAllIngredientsUsingSpecificMedia() {
    // Given
    Long mediaId = 100L;
    List<IngredientMedia> expectedMedia = Arrays.asList(
        createTestIngredientMedia(1L, 10L, mediaId),
        createTestIngredientMedia(1L, 20L, mediaId),
        createTestIngredientMedia(2L, 10L, mediaId));
    when(ingredientMediaRepository.findByIdMediaId(mediaId)).thenReturn(expectedMedia);

    // When
    List<IngredientMedia> result = ingredientMediaRepository.findByIdMediaId(mediaId);

    // Then
    assertThat(result).hasSize(3);
    assertThat(result).containsExactlyElementsOf(expectedMedia);
    assertThat(result).allMatch(im -> im.getId().getMediaId().equals(mediaId));
  }

  @Test
  @DisplayName("Should find media for specific ingredient in specific recipe")
  void shouldFindMediaForSpecificIngredientInSpecificRecipe() {
    // Given
    Long recipeId = 1L;
    Long ingredientId = 10L;
    List<IngredientMedia> expectedMedia = Arrays.asList(
        createTestIngredientMedia(recipeId, ingredientId, 100L),
        createTestIngredientMedia(recipeId, ingredientId, 200L));
    when(ingredientMediaRepository.findByIdRecipeIdAndIdIngredientId(recipeId, ingredientId))
        .thenReturn(expectedMedia);

    // When
    List<IngredientMedia> result = ingredientMediaRepository
        .findByIdRecipeIdAndIdIngredientId(recipeId, ingredientId);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(expectedMedia);
    assertThat(result).allMatch(im -> im.getId().getRecipeId().equals(recipeId) &&
        im.getId().getIngredientId().equals(ingredientId));
  }

  @Test
  @DisplayName("Should find ingredient media for multiple recipes")
  void shouldFindIngredientMediaForMultipleRecipes() {
    // Given
    List<Long> recipeIds = Arrays.asList(1L, 2L, 3L);
    List<IngredientMedia> expectedMedia = Arrays.asList(
        createTestIngredientMedia(1L, 10L, 100L),
        createTestIngredientMedia(2L, 20L, 200L),
        createTestIngredientMedia(3L, 30L, 300L));
    when(ingredientMediaRepository.findByIdRecipeIdIn(recipeIds)).thenReturn(expectedMedia);

    // When
    List<IngredientMedia> result = ingredientMediaRepository.findByIdRecipeIdIn(recipeIds);

    // Then
    assertThat(result).hasSize(3);
    assertThat(result).containsExactlyElementsOf(expectedMedia);
    assertThat(result.stream().map(im -> im.getId().getRecipeId()))
        .containsExactlyElementsOf(recipeIds);
  }

  @Test
  @DisplayName("Should delete all ingredient media for a recipe")
  void shouldDeleteAllIngredientMediaForRecipe() {
    // Given
    Long recipeId = 1L;

    // When
    ingredientMediaRepository.deleteByIdRecipeId(recipeId);

    // Then
    verify(ingredientMediaRepository).deleteByIdRecipeId(recipeId);
  }

  @Test
  @DisplayName("Should delete all media for specific ingredient")
  void shouldDeleteAllMediaForSpecificIngredient() {
    // Given
    Long ingredientId = 10L;

    // When
    ingredientMediaRepository.deleteByIdIngredientId(ingredientId);

    // Then
    verify(ingredientMediaRepository).deleteByIdIngredientId(ingredientId);
  }

  @Test
  @DisplayName("Should delete all ingredient associations for media")
  void shouldDeleteAllIngredientAssociationsForMedia() {
    // Given
    Long mediaId = 100L;

    // When
    ingredientMediaRepository.deleteByIdMediaId(mediaId);

    // Then
    verify(ingredientMediaRepository).deleteByIdMediaId(mediaId);
  }

  @Test
  @DisplayName("Should delete media for specific ingredient in recipe")
  void shouldDeleteMediaForSpecificIngredientInRecipe() {
    // Given
    Long recipeId = 1L;
    Long ingredientId = 10L;

    // When
    ingredientMediaRepository.deleteByIdRecipeIdAndIdIngredientId(recipeId, ingredientId);

    // Then
    verify(ingredientMediaRepository).deleteByIdRecipeIdAndIdIngredientId(recipeId, ingredientId);
  }

  @Test
  @DisplayName("Should count ingredient media items in a recipe")
  void shouldCountIngredientMediaItemsInRecipe() {
    // Given
    Long recipeId = 1L;
    when(ingredientMediaRepository.countByIdRecipeId(recipeId)).thenReturn(7L);

    // When
    long count = ingredientMediaRepository.countByIdRecipeId(recipeId);

    // Then
    assertThat(count).isEqualTo(7L);
  }

  @Test
  @DisplayName("Should count media items for specific ingredient")
  void shouldCountMediaItemsForSpecificIngredient() {
    // Given
    Long ingredientId = 10L;
    when(ingredientMediaRepository.countByIdIngredientId(ingredientId)).thenReturn(4L);

    // When
    long count = ingredientMediaRepository.countByIdIngredientId(ingredientId);

    // Then
    assertThat(count).isEqualTo(4L);
  }

  @Test
  @DisplayName("Should count ingredient associations for media")
  void shouldCountIngredientAssociationsForMedia() {
    // Given
    Long mediaId = 100L;
    when(ingredientMediaRepository.countByIdMediaId(mediaId)).thenReturn(6L);

    // When
    long count = ingredientMediaRepository.countByIdMediaId(mediaId);

    // Then
    assertThat(count).isEqualTo(6L);
  }

  @Test
  @DisplayName("Should check if recipe has ingredient media")
  void shouldCheckIfRecipeHasIngredientMedia() {
    // Given
    Long recipeId = 1L;
    when(ingredientMediaRepository.existsByIdRecipeId(recipeId)).thenReturn(true);

    // When
    boolean exists = ingredientMediaRepository.existsByIdRecipeId(recipeId);

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Should check if ingredient has associated media")
  void shouldCheckIfIngredientHasAssociatedMedia() {
    // Given
    Long ingredientId = 10L;
    when(ingredientMediaRepository.existsByIdIngredientId(ingredientId)).thenReturn(true);

    // When
    boolean exists = ingredientMediaRepository.existsByIdIngredientId(ingredientId);

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Should check if media is associated with ingredients")
  void shouldCheckIfMediaIsAssociatedWithIngredients() {
    // Given
    Long mediaId = 100L;
    when(ingredientMediaRepository.existsByIdMediaId(mediaId)).thenReturn(true);

    // When
    boolean exists = ingredientMediaRepository.existsByIdMediaId(mediaId);

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Should return false when no associations exist")
  void shouldReturnFalseWhenNoAssociationsExist() {
    // Given
    Long recipeId = 1L;
    Long ingredientId = 10L;
    Long mediaId = 100L;

    when(ingredientMediaRepository.existsByIdRecipeId(recipeId)).thenReturn(false);
    when(ingredientMediaRepository.existsByIdIngredientId(ingredientId)).thenReturn(false);
    when(ingredientMediaRepository.existsByIdMediaId(mediaId)).thenReturn(false);

    // When & Then
    assertThat(ingredientMediaRepository.existsByIdRecipeId(recipeId)).isFalse();
    assertThat(ingredientMediaRepository.existsByIdIngredientId(ingredientId)).isFalse();
    assertThat(ingredientMediaRepository.existsByIdMediaId(mediaId)).isFalse();
  }

  @Test
  @DisplayName("Should handle complex multi-level associations")
  void shouldHandleComplexMultiLevelAssociations() {
    // Given
    Long recipeId = 1L;
    List<IngredientMedia> complexAssociations = Arrays.asList(
        createTestIngredientMedia(recipeId, 10L, 100L), // Recipe 1, Ingredient 10, Media 100
        createTestIngredientMedia(recipeId, 10L, 200L), // Recipe 1, Ingredient 10, Media 200
        createTestIngredientMedia(recipeId, 20L, 100L), // Recipe 1, Ingredient 20, Media 100
        createTestIngredientMedia(recipeId, 30L, 300L) // Recipe 1, Ingredient 30, Media 300
    );

    when(ingredientMediaRepository.findByIdRecipeId(recipeId)).thenReturn(complexAssociations);
    when(ingredientMediaRepository.countByIdRecipeId(recipeId)).thenReturn(4L);

    // When
    List<IngredientMedia> found = ingredientMediaRepository.findByIdRecipeId(recipeId);
    long count = ingredientMediaRepository.countByIdRecipeId(recipeId);

    // Then
    assertThat(found).hasSize(4);
    assertThat(count).isEqualTo(4L);
    assertThat(found).allMatch(im -> im.getId().getRecipeId().equals(recipeId));

    // Verify unique combinations exist
    assertThat(found).extracting(im -> im.getId().getIngredientId()).contains(10L, 20L, 30L);
    assertThat(found).extracting(im -> im.getId().getMediaId()).contains(100L, 200L, 300L);
  }

  private IngredientMedia createTestIngredientMedia(Long recipeId, Long ingredientId, Long mediaId) {
    IngredientMediaId id = IngredientMediaId.builder()
        .recipeId(recipeId)
        .ingredientId(ingredientId)
        .mediaId(mediaId)
        .build();

    Recipe recipe = new Recipe();
    recipe.setRecipeId(recipeId);

    Ingredient ingredient = new Ingredient();
    ingredient.setIngredientId(ingredientId);

    Media media = new Media();
    media.setMediaId(mediaId);

    return IngredientMedia.builder()
        .id(id)
        .recipe(recipe)
        .ingredient(ingredient)
        .media(media)
        .build();
  }
}
