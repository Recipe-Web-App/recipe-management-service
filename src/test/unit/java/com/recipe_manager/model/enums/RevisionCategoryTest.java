package com.recipe_manager.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RevisionCategory enum.
 */
@Tag("unit")
class RevisionCategoryTest {

  @Test
  @DisplayName("Should have correct enum values")
  @Tag("standard-processing")
  void shouldHaveCorrectEnumValues() {
    // Given & When
    RevisionCategory[] values = RevisionCategory.values();

    // Then
    assertThat(values).hasSize(2);
    assertThat(values).containsExactlyInAnyOrder(
        RevisionCategory.INGREDIENT,
        RevisionCategory.STEP);
  }

  @Test
  @DisplayName("Should have correct ordinal values")
  @Tag("standard-processing")
  void shouldHaveCorrectOrdinalValues() {
    // Then
    assertThat(RevisionCategory.INGREDIENT.ordinal()).isEqualTo(0);
    assertThat(RevisionCategory.STEP.ordinal()).isEqualTo(1);
  }

  @Test
  @DisplayName("Should convert from string correctly")
  @Tag("standard-processing")
  void shouldConvertFromStringCorrectly() {
    // Given
    String ingredientString = "INGREDIENT";
    String stepString = "STEP";

    // When & Then
    assertThat(RevisionCategory.valueOf(ingredientString)).isEqualTo(RevisionCategory.INGREDIENT);
    assertThat(RevisionCategory.valueOf(stepString)).isEqualTo(RevisionCategory.STEP);
  }

  @Test
  @DisplayName("Should have correct string representation")
  @Tag("standard-processing")
  void shouldHaveCorrectStringRepresentation() {
    // Then
    assertThat(RevisionCategory.INGREDIENT.name()).isEqualTo("INGREDIENT");
    assertThat(RevisionCategory.STEP.name()).isEqualTo("STEP");
  }

  @Test
  @DisplayName("Should be distinct categories")
  @Tag("standard-processing")
  void shouldBeDistinctCategories() {
    // Then
    assertThat(RevisionCategory.INGREDIENT).isNotEqualTo(RevisionCategory.STEP);
    assertThat(RevisionCategory.INGREDIENT.ordinal()).isLessThan(RevisionCategory.STEP.ordinal());
  }
}
