package com.recipe_manager.model.entity.media;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class IngredientMediaIdTest {

  @Test
  @DisplayName("Should create IngredientMediaId with builder pattern")
  void shouldCreateIngredientMediaIdWithBuilder() {
    // When
    IngredientMediaId id = IngredientMediaId.builder()
        .recipeId(1L)
        .ingredientId(2L)
        .mediaId(3L)
        .build();

    // Then
    assertThat(id.getRecipeId()).isEqualTo(1L);
    assertThat(id.getIngredientId()).isEqualTo(2L);
    assertThat(id.getMediaId()).isEqualTo(3L);
  }

  @Test
  @DisplayName("Should create IngredientMediaId with no-args constructor")
  void shouldCreateIngredientMediaIdWithNoArgsConstructor() {
    // When
    IngredientMediaId id = new IngredientMediaId();

    // Then
    assertThat(id.getRecipeId()).isNull();
    assertThat(id.getIngredientId()).isNull();
    assertThat(id.getMediaId()).isNull();
  }

  @Test
  @DisplayName("Should create IngredientMediaId with all-args constructor")
  void shouldCreateIngredientMediaIdWithAllArgsConstructor() {
    // When
    IngredientMediaId id = new IngredientMediaId(1L, 2L, 3L);

    // Then
    assertThat(id.getRecipeId()).isEqualTo(1L);
    assertThat(id.getIngredientId()).isEqualTo(2L);
    assertThat(id.getMediaId()).isEqualTo(3L);
  }

  @Test
  @DisplayName("Should set and get all properties")
  void shouldSetAndGetAllProperties() {
    // Given
    IngredientMediaId id = new IngredientMediaId();

    // When
    id.setRecipeId(5L);
    id.setIngredientId(10L);
    id.setMediaId(15L);

    // Then
    assertThat(id.getRecipeId()).isEqualTo(5L);
    assertThat(id.getIngredientId()).isEqualTo(10L);
    assertThat(id.getMediaId()).isEqualTo(15L);
  }

  @Test
  @DisplayName("Should implement equals and hashCode correctly")
  void shouldImplementEqualsAndHashCodeCorrectly() {
    // Given
    IngredientMediaId id1 = new IngredientMediaId(1L, 2L, 3L);
    IngredientMediaId id2 = new IngredientMediaId(1L, 2L, 3L);
    IngredientMediaId id3 = new IngredientMediaId(2L, 1L, 3L);

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
    IngredientMediaId id = new IngredientMediaId(1L, 2L, 3L);

    // When
    String toString = id.toString();

    // Then
    assertThat(toString)
        .contains("IngredientMediaId")
        .contains("recipeId=1")
        .contains("ingredientId=2")
        .contains("mediaId=3");
  }
}
