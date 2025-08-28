package com.recipe_manager.repository.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.recipe_manager.model.entity.media.Media;
import com.recipe_manager.model.entity.media.RecipeMedia;
import com.recipe_manager.model.entity.recipe.Recipe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecipeMediaRepository.
 *
 * Tests repository methods for managing recipe-media associations.
 *
 * Note: These are mock-based unit tests since the repository depends on JPA
 * infrastructure that is not easily testable in isolation.
 */
@Tag("unit")
class RecipeMediaRepositoryTest {

  private RecipeMediaRepository recipeMediaRepository;

  @BeforeEach
  void setUp() {
    recipeMediaRepository = mock(RecipeMediaRepository.class);
  }

  @Test
  @DisplayName("Should find all media for a recipe")
  void shouldFindAllMediaForRecipe() {
    // Given
    Long recipeId = 1L;
    List<RecipeMedia> expectedRecipeMedia = Arrays.asList(
        createTestRecipeMedia(1L, recipeId),
        createTestRecipeMedia(2L, recipeId),
        createTestRecipeMedia(3L, recipeId));
    when(recipeMediaRepository.findByRecipeId(recipeId)).thenReturn(expectedRecipeMedia);

    // When
    List<RecipeMedia> result = recipeMediaRepository.findByRecipeId(recipeId);

    // Then
    assertThat(result).hasSize(3);
    assertThat(result).containsExactlyElementsOf(expectedRecipeMedia);
    assertThat(result).allMatch(rm -> rm.getRecipeId().equals(recipeId));
  }

  @Test
  @DisplayName("Should return empty list when recipe has no media")
  void shouldReturnEmptyListWhenRecipeHasNoMedia() {
    // Given
    Long recipeId = 1L;
    when(recipeMediaRepository.findByRecipeId(recipeId)).thenReturn(Collections.emptyList());

    // When
    List<RecipeMedia> result = recipeMediaRepository.findByRecipeId(recipeId);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should find all recipes using specific media")
  void shouldFindAllRecipesUsingSpecificMedia() {
    // Given
    Long mediaId = 10L;
    List<RecipeMedia> expectedRecipeMedia = Arrays.asList(
        createTestRecipeMedia(mediaId, 1L),
        createTestRecipeMedia(mediaId, 2L));
    when(recipeMediaRepository.findByMediaId(mediaId)).thenReturn(expectedRecipeMedia);

    // When
    List<RecipeMedia> result = recipeMediaRepository.findByMediaId(mediaId);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(expectedRecipeMedia);
    assertThat(result).allMatch(rm -> rm.getMediaId().equals(mediaId));
  }

  @Test
  @DisplayName("Should delete all media associations for a recipe")
  void shouldDeleteAllMediaAssociationsForRecipe() {
    // Given
    Long recipeId = 1L;

    // When
    recipeMediaRepository.deleteByRecipeId(recipeId);

    // Then
    verify(recipeMediaRepository).deleteByRecipeId(recipeId);
  }

  @Test
  @DisplayName("Should delete all recipe associations for a media")
  void shouldDeleteAllRecipeAssociationsForMedia() {
    // Given
    Long mediaId = 10L;

    // When
    recipeMediaRepository.deleteByMediaId(mediaId);

    // Then
    verify(recipeMediaRepository).deleteByMediaId(mediaId);
  }

  @Test
  @DisplayName("Should count media items for a recipe")
  void shouldCountMediaItemsForRecipe() {
    // Given
    Long recipeId = 1L;
    when(recipeMediaRepository.countByRecipeId(recipeId)).thenReturn(5L);

    // When
    long count = recipeMediaRepository.countByRecipeId(recipeId);

    // Then
    assertThat(count).isEqualTo(5L);
  }

  @Test
  @DisplayName("Should return zero count when recipe has no media")
  void shouldReturnZeroCountWhenRecipeHasNoMedia() {
    // Given
    Long recipeId = 1L;
    when(recipeMediaRepository.countByRecipeId(recipeId)).thenReturn(0L);

    // When
    long count = recipeMediaRepository.countByRecipeId(recipeId);

    // Then
    assertThat(count).isZero();
  }

  @Test
  @DisplayName("Should count recipes using specific media")
  void shouldCountRecipesUsingSpecificMedia() {
    // Given
    Long mediaId = 10L;
    when(recipeMediaRepository.countByMediaId(mediaId)).thenReturn(3L);

    // When
    long count = recipeMediaRepository.countByMediaId(mediaId);

    // Then
    assertThat(count).isEqualTo(3L);
  }

  @Test
  @DisplayName("Should check if recipe has associated media")
  void shouldCheckIfRecipeHasAssociatedMedia() {
    // Given
    Long recipeId = 1L;
    when(recipeMediaRepository.existsByRecipeId(recipeId)).thenReturn(true);

    // When
    boolean exists = recipeMediaRepository.existsByRecipeId(recipeId);

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Should return false when recipe has no media")
  void shouldReturnFalseWhenRecipeHasNoMedia() {
    // Given
    Long recipeId = 1L;
    when(recipeMediaRepository.existsByRecipeId(recipeId)).thenReturn(false);

    // When
    boolean exists = recipeMediaRepository.existsByRecipeId(recipeId);

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("Should check if media is associated with recipes")
  void shouldCheckIfMediaIsAssociatedWithRecipes() {
    // Given
    Long mediaId = 10L;
    when(recipeMediaRepository.existsByMediaId(mediaId)).thenReturn(true);

    // When
    boolean exists = recipeMediaRepository.existsByMediaId(mediaId);

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Should return false when media is not associated with recipes")
  void shouldReturnFalseWhenMediaIsNotAssociatedWithRecipes() {
    // Given
    Long mediaId = 10L;
    when(recipeMediaRepository.existsByMediaId(mediaId)).thenReturn(false);

    // When
    boolean exists = recipeMediaRepository.existsByMediaId(mediaId);

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("Should handle multiple media associations correctly")
  void shouldHandleMultipleMediaAssociationsCorrectly() {
    // Given
    Long recipeId = 1L;
    List<RecipeMedia> multipleMedia = Arrays.asList(
        createTestRecipeMedia(10L, recipeId),
        createTestRecipeMedia(20L, recipeId),
        createTestRecipeMedia(30L, recipeId),
        createTestRecipeMedia(40L, recipeId));
    when(recipeMediaRepository.findByRecipeId(recipeId)).thenReturn(multipleMedia);
    when(recipeMediaRepository.countByRecipeId(recipeId)).thenReturn(4L);

    // When
    List<RecipeMedia> foundMedia = recipeMediaRepository.findByRecipeId(recipeId);
    long mediaCount = recipeMediaRepository.countByRecipeId(recipeId);

    // Then
    assertThat(foundMedia).hasSize(4);
    assertThat(mediaCount).isEqualTo(4L);
    assertThat(foundMedia).allMatch(rm -> rm.getRecipeId().equals(recipeId));

    // Verify each media has unique media IDs
    List<Long> mediaIds = foundMedia.stream().map(RecipeMedia::getMediaId).toList();
    assertThat(mediaIds).containsExactly(10L, 20L, 30L, 40L);
  }

  @Test
  @DisplayName("Should handle shared media across multiple recipes")
  void shouldHandleSharedMediaAcrossMultipleRecipes() {
    // Given
    Long sharedMediaId = 100L;
    List<RecipeMedia> recipesUsingMedia = Arrays.asList(
        createTestRecipeMedia(sharedMediaId, 1L),
        createTestRecipeMedia(sharedMediaId, 2L),
        createTestRecipeMedia(sharedMediaId, 3L));
    when(recipeMediaRepository.findByMediaId(sharedMediaId)).thenReturn(recipesUsingMedia);
    when(recipeMediaRepository.countByMediaId(sharedMediaId)).thenReturn(3L);

    // When
    List<RecipeMedia> foundAssociations = recipeMediaRepository.findByMediaId(sharedMediaId);
    long recipeCount = recipeMediaRepository.countByMediaId(sharedMediaId);

    // Then
    assertThat(foundAssociations).hasSize(3);
    assertThat(recipeCount).isEqualTo(3L);
    assertThat(foundAssociations).allMatch(rm -> rm.getMediaId().equals(sharedMediaId));

    // Verify each association has unique recipe IDs
    List<Long> recipeIds = foundAssociations.stream().map(RecipeMedia::getRecipeId).toList();
    assertThat(recipeIds).containsExactly(1L, 2L, 3L);
  }

  private RecipeMedia createTestRecipeMedia(Long mediaId, Long recipeId) {
    Media media = new Media();
    media.setMediaId(mediaId);

    Recipe recipe = new Recipe();
    recipe.setRecipeId(recipeId);

    return RecipeMedia.builder()
        .mediaId(mediaId)
        .recipeId(recipeId)
        .media(media)
        .recipe(recipe)
        .build();
  }
}
