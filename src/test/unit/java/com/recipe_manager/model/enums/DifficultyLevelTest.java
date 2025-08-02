package com.recipe_manager.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for DifficultyLevel enum.
 */
@Tag("unit")
class DifficultyLevelTest {

  @Test
  @DisplayName("Should have correct enum values")
  @Tag("standard-processing")
  void shouldHaveCorrectEnumValues() {
    // Given & When
    DifficultyLevel[] values = DifficultyLevel.values();

    // Then
    assertThat(values).hasSize(5);
    assertThat(values).containsExactlyInAnyOrder(
        DifficultyLevel.BEGINNER,
        DifficultyLevel.EASY,
        DifficultyLevel.MEDIUM,
        DifficultyLevel.HARD,
        DifficultyLevel.EXPERT);
  }

  @Test
  @DisplayName("Should have correct ordinal values")
  @Tag("standard-processing")
  void shouldHaveCorrectOrdinalValues() {
    // Then
    assertThat(DifficultyLevel.BEGINNER.ordinal()).isEqualTo(0);
    assertThat(DifficultyLevel.EASY.ordinal()).isEqualTo(1);
    assertThat(DifficultyLevel.MEDIUM.ordinal()).isEqualTo(2);
    assertThat(DifficultyLevel.HARD.ordinal()).isEqualTo(3);
    assertThat(DifficultyLevel.EXPERT.ordinal()).isEqualTo(4);
  }

  @Test
  @DisplayName("Should convert from string correctly")
  @Tag("standard-processing")
  void shouldConvertFromStringCorrectly() {
    // Given
    String beginnerString = "BEGINNER";
    String easyString = "EASY";
    String mediumString = "MEDIUM";
    String hardString = "HARD";
    String expertString = "EXPERT";

    // When & Then
    assertThat(DifficultyLevel.valueOf(beginnerString)).isEqualTo(DifficultyLevel.BEGINNER);
    assertThat(DifficultyLevel.valueOf(easyString)).isEqualTo(DifficultyLevel.EASY);
    assertThat(DifficultyLevel.valueOf(mediumString)).isEqualTo(DifficultyLevel.MEDIUM);
    assertThat(DifficultyLevel.valueOf(hardString)).isEqualTo(DifficultyLevel.HARD);
    assertThat(DifficultyLevel.valueOf(expertString)).isEqualTo(DifficultyLevel.EXPERT);
  }

  @Test
  @DisplayName("Should have correct string representation")
  @Tag("standard-processing")
  void shouldHaveCorrectStringRepresentation() {
    // Then
    assertThat(DifficultyLevel.BEGINNER.name()).isEqualTo("BEGINNER");
    assertThat(DifficultyLevel.EASY.name()).isEqualTo("EASY");
    assertThat(DifficultyLevel.MEDIUM.name()).isEqualTo("MEDIUM");
    assertThat(DifficultyLevel.HARD.name()).isEqualTo("HARD");
    assertThat(DifficultyLevel.EXPERT.name()).isEqualTo("EXPERT");
  }

  @Test
  @DisplayName("Should compare difficulty levels correctly")
  @Tag("standard-processing")
  void shouldCompareDifficultyLevelsCorrectly() {
    // Then
    assertThat(DifficultyLevel.BEGINNER).isLessThan(DifficultyLevel.EASY);
    assertThat(DifficultyLevel.EASY).isLessThan(DifficultyLevel.MEDIUM);
    assertThat(DifficultyLevel.MEDIUM).isLessThan(DifficultyLevel.HARD);
    assertThat(DifficultyLevel.HARD).isLessThan(DifficultyLevel.EXPERT);

    assertThat(DifficultyLevel.EXPERT).isGreaterThan(DifficultyLevel.HARD);
    assertThat(DifficultyLevel.HARD).isGreaterThan(DifficultyLevel.MEDIUM);
    assertThat(DifficultyLevel.MEDIUM).isGreaterThan(DifficultyLevel.EASY);
    assertThat(DifficultyLevel.EASY).isGreaterThan(DifficultyLevel.BEGINNER);
  }
}
