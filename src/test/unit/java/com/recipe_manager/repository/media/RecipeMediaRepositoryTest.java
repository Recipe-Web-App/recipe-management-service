package com.recipe_manager.repository.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import com.recipe_manager.model.entity.media.RecipeMedia;
import com.recipe_manager.model.entity.media.RecipeMediaId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class RecipeMediaRepositoryTest {

  @Mock
  private RecipeMediaRepository recipeMediaRepository;

  private Long recipeId;
  private Long mediaId;
  private UUID userId;
  private RecipeMedia recipeMedia;
  private RecipeMediaId recipeMediaId;

  @BeforeEach
  void setUp() {
    recipeId = 1L;
    mediaId = 2L;
    userId = UUID.randomUUID();

    recipeMediaId = RecipeMediaId.builder()
        .recipeId(recipeId)
        .mediaId(mediaId)
        .build();

    recipeMedia = RecipeMedia.builder()
        .id(recipeMediaId)
        .build();
  }

  @Test
  @DisplayName("Should find recipe media by recipe ID")
  void shouldFindRecipeMediaByRecipeId() {
    // Given
    List<RecipeMedia> expectedRecipeMedia = List.of(recipeMedia);
    when(recipeMediaRepository.findByRecipeRecipeId(recipeId)).thenReturn(expectedRecipeMedia);

    // When
    List<RecipeMedia> result = recipeMediaRepository.findByRecipeRecipeId(recipeId);

    // Then
    assertThat(result)
        .hasSize(1)
        .containsExactly(recipeMedia);
  }

  @Test
  @DisplayName("Should find recipe media by media ID")
  void shouldFindRecipeMediaByMediaId() {
    // Given
    List<RecipeMedia> expectedRecipeMedia = List.of(recipeMedia);
    when(recipeMediaRepository.findByMediaMediaId(mediaId)).thenReturn(expectedRecipeMedia);

    // When
    List<RecipeMedia> result = recipeMediaRepository.findByMediaMediaId(mediaId);

    // Then
    assertThat(result)
        .hasSize(1)
        .containsExactly(recipeMedia);
  }

  @Test
  @DisplayName("Should find specific recipe media relationship")
  void shouldFindSpecificRecipeMediaRelationship() {
    // Given
    when(recipeMediaRepository.findByRecipeRecipeIdAndMediaMediaId(recipeId, mediaId))
        .thenReturn(java.util.Optional.of(recipeMedia));

    // When
    java.util.Optional<RecipeMedia> result = recipeMediaRepository.findByRecipeRecipeIdAndMediaMediaId(recipeId,
        mediaId);

    // Then
    assertThat(result)
        .isPresent()
        .contains(recipeMedia);
  }

  @Test
  @DisplayName("Should find recipe media by user ID")
  void shouldFindRecipeMediaByUserId() {
    // Given
    List<RecipeMedia> expectedRecipeMedia = List.of(recipeMedia);
    when(recipeMediaRepository.findByRecipeUserId(userId)).thenReturn(expectedRecipeMedia);

    // When
    List<RecipeMedia> result = recipeMediaRepository.findByRecipeUserId(userId);

    // Then
    assertThat(result)
        .hasSize(1)
        .containsExactly(recipeMedia);
  }

  @Test
  @DisplayName("Should check if recipe media exists by recipe and media IDs")
  void shouldCheckIfRecipeMediaExistsByRecipeAndMediaIds() {
    // Given
    when(recipeMediaRepository.existsByRecipeRecipeIdAndMediaMediaId(recipeId, mediaId)).thenReturn(true);

    // When
    boolean exists = recipeMediaRepository.existsByRecipeRecipeIdAndMediaMediaId(recipeId, mediaId);

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Should return false when recipe media does not exist")
  void shouldReturnFalseWhenRecipeMediaDoesNotExist() {
    // Given
    when(recipeMediaRepository.existsByRecipeRecipeIdAndMediaMediaId(anyLong(), anyLong())).thenReturn(false);

    // When
    boolean exists = recipeMediaRepository.existsByRecipeRecipeIdAndMediaMediaId(999L, 999L);

    // Then
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("Should count recipe media by recipe ID")
  void shouldCountRecipeMediaByRecipeId() {
    // Given
    when(recipeMediaRepository.countByRecipeRecipeId(recipeId)).thenReturn(3L);

    // When
    long count = recipeMediaRepository.countByRecipeRecipeId(recipeId);

    // Then
    assertThat(count).isEqualTo(3L);
  }

  @Test
  @DisplayName("Should count recipe media by media ID")
  void shouldCountRecipeMediaByMediaId() {
    // Given
    when(recipeMediaRepository.countByMediaMediaId(mediaId)).thenReturn(2L);

    // When
    long count = recipeMediaRepository.countByMediaMediaId(mediaId);

    // Then
    assertThat(count).isEqualTo(2L);
  }

  @Test
  @DisplayName("Should delete recipe media by recipe ID")
  void shouldDeleteRecipeMediaByRecipeId() {
    // Given
    doNothing().when(recipeMediaRepository).deleteByRecipeRecipeId(recipeId);

    // When
    recipeMediaRepository.deleteByRecipeRecipeId(recipeId);

    // Then
    verify(recipeMediaRepository).deleteByRecipeRecipeId(recipeId);
  }

  @Test
  @DisplayName("Should delete recipe media by media ID")
  void shouldDeleteRecipeMediaByMediaId() {
    // Given
    doNothing().when(recipeMediaRepository).deleteByMediaMediaId(mediaId);

    // When
    recipeMediaRepository.deleteByMediaMediaId(mediaId);

    // Then
    verify(recipeMediaRepository).deleteByMediaMediaId(mediaId);
  }
}
