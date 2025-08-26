package com.recipe_manager.model.entity.media;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class StepMediaIdTest {

  @Test
  @DisplayName("Should create StepMediaId with builder pattern")
  void shouldCreateStepMediaIdWithBuilder() {
    // When
    StepMediaId id = StepMediaId.builder()
        .recipeId(1L)
        .stepId(2L)
        .mediaId(3L)
        .build();

    // Then
    assertThat(id.getRecipeId()).isEqualTo(1L);
    assertThat(id.getStepId()).isEqualTo(2L);
    assertThat(id.getMediaId()).isEqualTo(3L);
  }

  @Test
  @DisplayName("Should create StepMediaId with no-args constructor")
  void shouldCreateStepMediaIdWithNoArgsConstructor() {
    // When
    StepMediaId id = new StepMediaId();

    // Then
    assertThat(id.getRecipeId()).isNull();
    assertThat(id.getStepId()).isNull();
    assertThat(id.getMediaId()).isNull();
  }

  @Test
  @DisplayName("Should create StepMediaId with all-args constructor")
  void shouldCreateStepMediaIdWithAllArgsConstructor() {
    // When
    StepMediaId id = new StepMediaId(1L, 2L, 3L);

    // Then
    assertThat(id.getRecipeId()).isEqualTo(1L);
    assertThat(id.getStepId()).isEqualTo(2L);
    assertThat(id.getMediaId()).isEqualTo(3L);
  }

  @Test
  @DisplayName("Should set and get all properties")
  void shouldSetAndGetAllProperties() {
    // Given
    StepMediaId id = new StepMediaId();

    // When
    id.setRecipeId(5L);
    id.setStepId(10L);
    id.setMediaId(15L);

    // Then
    assertThat(id.getRecipeId()).isEqualTo(5L);
    assertThat(id.getStepId()).isEqualTo(10L);
    assertThat(id.getMediaId()).isEqualTo(15L);
  }

  @Test
  @DisplayName("Should implement equals and hashCode correctly")
  void shouldImplementEqualsAndHashCodeCorrectly() {
    // Given
    StepMediaId id1 = new StepMediaId(1L, 2L, 3L);
    StepMediaId id2 = new StepMediaId(1L, 2L, 3L);
    StepMediaId id3 = new StepMediaId(2L, 1L, 3L);

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
    StepMediaId id = new StepMediaId(1L, 2L, 3L);

    // When
    String toString = id.toString();

    // Then
    assertThat(toString)
        .contains("StepMediaId")
        .contains("recipeId=1")
        .contains("stepId=2")
        .contains("mediaId=3");
  }
}
