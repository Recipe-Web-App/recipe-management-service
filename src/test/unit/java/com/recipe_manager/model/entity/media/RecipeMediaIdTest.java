package com.recipe_manager.model.entity.media;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class RecipeMediaIdTest {

  @Test
  @DisplayName("Should create RecipeMediaId with builder pattern")
  void shouldCreateRecipeMediaIdWithBuilder() {
    // When
    RecipeMediaId id = RecipeMediaId.builder()
        .mediaId(1L)
        .recipeId(2L)
        .build();

    // Then
    assertThat(id.getMediaId()).isEqualTo(1L);
    assertThat(id.getRecipeId()).isEqualTo(2L);
  }

  @Test
  @DisplayName("Should create RecipeMediaId with no-args constructor")
  void shouldCreateRecipeMediaIdWithNoArgsConstructor() {
    // When
    RecipeMediaId id = new RecipeMediaId();

    // Then
    assertThat(id.getMediaId()).isNull();
    assertThat(id.getRecipeId()).isNull();
  }

  @Test
  @DisplayName("Should create RecipeMediaId with all-args constructor")
  void shouldCreateRecipeMediaIdWithAllArgsConstructor() {
    // When
    RecipeMediaId id = new RecipeMediaId(1L, 2L);

    // Then
    assertThat(id.getMediaId()).isEqualTo(1L);
    assertThat(id.getRecipeId()).isEqualTo(2L);
  }

  @Test
  @DisplayName("Should set and get all properties")
  void shouldSetAndGetAllProperties() {
    // Given
    RecipeMediaId id = new RecipeMediaId();

    // When
    id.setMediaId(5L);
    id.setRecipeId(10L);

    // Then
    assertThat(id.getMediaId()).isEqualTo(5L);
    assertThat(id.getRecipeId()).isEqualTo(10L);
  }

  @Test
  @DisplayName("Should implement equals and hashCode correctly")
  void shouldImplementEqualsAndHashCodeCorrectly() {
    // Given
    RecipeMediaId id1 = new RecipeMediaId(1L, 2L);
    RecipeMediaId id2 = new RecipeMediaId(1L, 2L);
    RecipeMediaId id3 = new RecipeMediaId(2L, 1L);

    // Then
    assertThat(id1)
        .isEqualTo(id2)
        .hasSameHashCodeAs(id2)
        .isNotEqualTo(id3);
  }

  @Test
  @DisplayName("Should implement toString correctly")
  void shouldImplementToStringCorrectly() {
    // Given
    RecipeMediaId id = new RecipeMediaId(1L, 2L);

    // When
    String toString = id.toString();

    // Then
    assertThat(toString)
        .contains("RecipeMediaId")
        .contains("mediaId=1")
        .contains("recipeId=2");
  }
}
