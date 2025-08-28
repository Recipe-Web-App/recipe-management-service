package com.recipe_manager.repository.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.recipe_manager.model.entity.media.Media;
import com.recipe_manager.model.entity.media.StepMedia;
import com.recipe_manager.model.entity.media.StepMediaId;
import com.recipe_manager.model.entity.recipe.Recipe;
import com.recipe_manager.model.entity.recipe.RecipeStep;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for StepMediaRepository.
 *
 * Tests repository methods for managing step-media associations.
 *
 * Note: These are mock-based unit tests since the repository depends on JPA
 * infrastructure that is not easily testable in isolation.
 */
@Tag("unit")
class StepMediaRepositoryTest {

  private StepMediaRepository stepMediaRepository;

  @BeforeEach
  void setUp() {
    stepMediaRepository = mock(StepMediaRepository.class);
  }

  @Test
  @DisplayName("Should find all media for a step")
  void shouldFindAllMediaForStep() {
    // Given
    Long stepId = 10L;
    List<StepMedia> expectedStepMedia = Arrays.asList(
        createTestStepMedia(1L, stepId, 100L),
        createTestStepMedia(1L, stepId, 200L));
    when(stepMediaRepository.findByIdStepId(stepId)).thenReturn(expectedStepMedia);

    // When
    List<StepMedia> result = stepMediaRepository.findByIdStepId(stepId);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(expectedStepMedia);
    assertThat(result).allMatch(sm -> sm.getId().getStepId().equals(stepId));
  }

  @Test
  @DisplayName("Should return empty list when step has no media")
  void shouldReturnEmptyListWhenStepHasNoMedia() {
    // Given
    Long stepId = 10L;
    when(stepMediaRepository.findByIdStepId(stepId)).thenReturn(Collections.emptyList());

    // When
    List<StepMedia> result = stepMediaRepository.findByIdStepId(stepId);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should find all steps using specific media")
  void shouldFindAllStepsUsingSpecificMedia() {
    // Given
    Long mediaId = 100L;
    List<StepMedia> expectedStepMedia = Arrays.asList(
        createTestStepMedia(1L, 10L, mediaId),
        createTestStepMedia(1L, 20L, mediaId),
        createTestStepMedia(2L, 30L, mediaId));
    when(stepMediaRepository.findByIdMediaId(mediaId)).thenReturn(expectedStepMedia);

    // When
    List<StepMedia> result = stepMediaRepository.findByIdMediaId(mediaId);

    // Then
    assertThat(result).hasSize(3);
    assertThat(result).containsExactlyElementsOf(expectedStepMedia);
    assertThat(result).allMatch(sm -> sm.getId().getMediaId().equals(mediaId));
  }

  @Test
  @DisplayName("Should find all step media for a recipe")
  void shouldFindAllStepMediaForRecipe() {
    // Given
    Long recipeId = 1L;
    List<StepMedia> expectedStepMedia = Arrays.asList(
        createTestStepMedia(recipeId, 10L, 100L),
        createTestStepMedia(recipeId, 20L, 200L),
        createTestStepMedia(recipeId, 30L, 300L));
    when(stepMediaRepository.findByRecipeRecipeId(recipeId)).thenReturn(expectedStepMedia);

    // When
    List<StepMedia> result = stepMediaRepository.findByRecipeRecipeId(recipeId);

    // Then
    assertThat(result).hasSize(3);
    assertThat(result).containsExactlyElementsOf(expectedStepMedia);
    assertThat(result).allMatch(sm -> sm.getRecipe().getRecipeId().equals(recipeId));
  }

  @Test
  @DisplayName("Should find step media for multiple steps")
  void shouldFindStepMediaForMultipleSteps() {
    // Given
    List<Long> stepIds = Arrays.asList(10L, 20L, 30L);
    List<StepMedia> expectedStepMedia = Arrays.asList(
        createTestStepMedia(1L, 10L, 100L),
        createTestStepMedia(1L, 20L, 200L),
        createTestStepMedia(2L, 30L, 300L));
    when(stepMediaRepository.findByIdStepIdIn(stepIds)).thenReturn(expectedStepMedia);

    // When
    List<StepMedia> result = stepMediaRepository.findByIdStepIdIn(stepIds);

    // Then
    assertThat(result).hasSize(3);
    assertThat(result).containsExactlyElementsOf(expectedStepMedia);
    assertThat(result.stream().map(sm -> sm.getId().getStepId()))
        .containsExactlyElementsOf(stepIds);
  }

  @Test
  @DisplayName("Should find step media for multiple recipes")
  void shouldFindStepMediaForMultipleRecipes() {
    // Given
    List<Long> recipeIds = Arrays.asList(1L, 2L, 3L);
    List<StepMedia> expectedStepMedia = Arrays.asList(
        createTestStepMedia(1L, 10L, 100L),
        createTestStepMedia(2L, 20L, 200L),
        createTestStepMedia(3L, 30L, 300L));
    when(stepMediaRepository.findByRecipeRecipeIdIn(recipeIds)).thenReturn(expectedStepMedia);

    // When
    List<StepMedia> result = stepMediaRepository.findByRecipeRecipeIdIn(recipeIds);

    // Then
    assertThat(result).hasSize(3);
    assertThat(result).containsExactlyElementsOf(expectedStepMedia);
    assertThat(result.stream().map(sm -> sm.getRecipe().getRecipeId()))
        .containsExactlyElementsOf(recipeIds);
  }

  @Test
  @DisplayName("Should delete all media for a step")
  void shouldDeleteAllMediaForStep() {
    // Given
    Long stepId = 10L;

    // When
    stepMediaRepository.deleteByIdStepId(stepId);

    // Then
    verify(stepMediaRepository).deleteByIdStepId(stepId);
  }

  @Test
  @DisplayName("Should delete all step associations for media")
  void shouldDeleteAllStepAssociationsForMedia() {
    // Given
    Long mediaId = 100L;

    // When
    stepMediaRepository.deleteByIdMediaId(mediaId);

    // Then
    verify(stepMediaRepository).deleteByIdMediaId(mediaId);
  }

  @Test
  @DisplayName("Should delete all step media for a recipe")
  void shouldDeleteAllStepMediaForRecipe() {
    // Given
    Long recipeId = 1L;

    // When
    stepMediaRepository.deleteByRecipeRecipeId(recipeId);

    // Then
    verify(stepMediaRepository).deleteByRecipeRecipeId(recipeId);
  }

  @Test
  @DisplayName("Should count media items for a step")
  void shouldCountMediaItemsForStep() {
    // Given
    Long stepId = 10L;
    when(stepMediaRepository.countByIdStepId(stepId)).thenReturn(4L);

    // When
    long count = stepMediaRepository.countByIdStepId(stepId);

    // Then
    assertThat(count).isEqualTo(4L);
  }

  @Test
  @DisplayName("Should count step associations for media")
  void shouldCountStepAssociationsForMedia() {
    // Given
    Long mediaId = 100L;
    when(stepMediaRepository.countByIdMediaId(mediaId)).thenReturn(6L);

    // When
    long count = stepMediaRepository.countByIdMediaId(mediaId);

    // Then
    assertThat(count).isEqualTo(6L);
  }

  @Test
  @DisplayName("Should count step media in a recipe")
  void shouldCountStepMediaInRecipe() {
    // Given
    Long recipeId = 1L;
    when(stepMediaRepository.countByRecipeRecipeId(recipeId)).thenReturn(8L);

    // When
    long count = stepMediaRepository.countByRecipeRecipeId(recipeId);

    // Then
    assertThat(count).isEqualTo(8L);
  }

  @Test
  @DisplayName("Should check if step has associated media")
  void shouldCheckIfStepHasAssociatedMedia() {
    // Given
    Long stepId = 10L;
    when(stepMediaRepository.existsByIdStepId(stepId)).thenReturn(true);

    // When
    boolean exists = stepMediaRepository.existsByIdStepId(stepId);

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Should check if media is associated with steps")
  void shouldCheckIfMediaIsAssociatedWithSteps() {
    // Given
    Long mediaId = 100L;
    when(stepMediaRepository.existsByIdMediaId(mediaId)).thenReturn(true);

    // When
    boolean exists = stepMediaRepository.existsByIdMediaId(mediaId);

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Should check if recipe has step media associations")
  void shouldCheckIfRecipeHasStepMediaAssociations() {
    // Given
    Long recipeId = 1L;
    when(stepMediaRepository.existsByRecipeRecipeId(recipeId)).thenReturn(true);

    // When
    boolean exists = stepMediaRepository.existsByRecipeRecipeId(recipeId);

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Should return false when no associations exist")
  void shouldReturnFalseWhenNoAssociationsExist() {
    // Given
    Long stepId = 10L;
    Long mediaId = 100L;
    Long recipeId = 1L;

    when(stepMediaRepository.existsByIdStepId(stepId)).thenReturn(false);
    when(stepMediaRepository.existsByIdMediaId(mediaId)).thenReturn(false);
    when(stepMediaRepository.existsByRecipeRecipeId(recipeId)).thenReturn(false);

    // When & Then
    assertThat(stepMediaRepository.existsByIdStepId(stepId)).isFalse();
    assertThat(stepMediaRepository.existsByIdMediaId(mediaId)).isFalse();
    assertThat(stepMediaRepository.existsByRecipeRecipeId(recipeId)).isFalse();
  }

  @Test
  @DisplayName("Should return zero counts when no associations exist")
  void shouldReturnZeroCountsWhenNoAssociationsExist() {
    // Given
    Long stepId = 10L;
    Long mediaId = 100L;
    Long recipeId = 1L;

    when(stepMediaRepository.countByIdStepId(stepId)).thenReturn(0L);
    when(stepMediaRepository.countByIdMediaId(mediaId)).thenReturn(0L);
    when(stepMediaRepository.countByRecipeRecipeId(recipeId)).thenReturn(0L);

    // When & Then
    assertThat(stepMediaRepository.countByIdStepId(stepId)).isZero();
    assertThat(stepMediaRepository.countByIdMediaId(mediaId)).isZero();
    assertThat(stepMediaRepository.countByRecipeRecipeId(recipeId)).isZero();
  }

  @Test
  @DisplayName("Should handle complex recipe with multiple steps and media")
  void shouldHandleComplexRecipeWithMultipleStepsAndMedia() {
    // Given
    Long recipeId = 1L;
    List<StepMedia> complexStepMedia = Arrays.asList(
        createTestStepMedia(recipeId, 10L, 100L), // Step 10, Media 100
        createTestStepMedia(recipeId, 10L, 200L), // Step 10, Media 200 (multiple media per step)
        createTestStepMedia(recipeId, 20L, 100L), // Step 20, Media 100 (shared media)
        createTestStepMedia(recipeId, 30L, 300L), // Step 30, Media 300
        createTestStepMedia(recipeId, 30L, 400L) // Step 30, Media 400
    );

    when(stepMediaRepository.findByRecipeRecipeId(recipeId)).thenReturn(complexStepMedia);
    when(stepMediaRepository.countByRecipeRecipeId(recipeId)).thenReturn(5L);

    // When
    List<StepMedia> found = stepMediaRepository.findByRecipeRecipeId(recipeId);
    long count = stepMediaRepository.countByRecipeRecipeId(recipeId);

    // Then
    assertThat(found).hasSize(5);
    assertThat(count).isEqualTo(5L);
    assertThat(found).allMatch(sm -> sm.getRecipe().getRecipeId().equals(recipeId));

    // Verify multiple media per step and shared media scenarios
    long step10MediaCount = found.stream()
        .filter(sm -> sm.getId().getStepId().equals(10L))
        .count();
    assertThat(step10MediaCount).isEqualTo(2L); // Step 10 has 2 media items

    long media100StepCount = found.stream()
        .filter(sm -> sm.getId().getMediaId().equals(100L))
        .count();
    assertThat(media100StepCount).isEqualTo(2L); // Media 100 is used by 2 steps
  }

  private StepMedia createTestStepMedia(Long recipeId, Long stepId, Long mediaId) {
    StepMediaId id = StepMediaId.builder()
        .stepId(stepId)
        .mediaId(mediaId)
        .build();

    Recipe recipe = new Recipe();
    recipe.setRecipeId(recipeId);

    RecipeStep step = new RecipeStep();
    step.setStepId(stepId);

    Media media = new Media();
    media.setMediaId(mediaId);

    return StepMedia.builder()
        .id(id)
        .recipe(recipe)
        .step(step)
        .media(media)
        .build();
  }
}
