package com.recipe_manager.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for IngredientMatchMode enum.
 */
@Tag("unit")
class IngredientMatchModeTest {

  @Test
  @DisplayName("Should have correct enum values")
  @Tag("standard-processing")
  void shouldHaveCorrectEnumValues() {
    // Given & When
    IngredientMatchMode[] values = IngredientMatchMode.values();

    // Then
    assertThat(values).hasSize(2);
    assertThat(values).containsExactlyInAnyOrder(
        IngredientMatchMode.AND,
        IngredientMatchMode.OR);
  }

  @Test
  @DisplayName("Should have correct ordinal values")
  @Tag("standard-processing")
  void shouldHaveCorrectOrdinalValues() {
    // Then
    assertThat(IngredientMatchMode.AND.ordinal()).isEqualTo(0);
    assertThat(IngredientMatchMode.OR.ordinal()).isEqualTo(1);
  }

  @Test
  @DisplayName("Should convert from string correctly")
  @Tag("standard-processing")
  void shouldConvertFromStringCorrectly() {
    // Given
    String andString = "AND";
    String orString = "OR";

    // When & Then
    assertThat(IngredientMatchMode.valueOf(andString)).isEqualTo(IngredientMatchMode.AND);
    assertThat(IngredientMatchMode.valueOf(orString)).isEqualTo(IngredientMatchMode.OR);
  }

  @Test
  @DisplayName("Should have correct string representation")
  @Tag("standard-processing")
  void shouldHaveCorrectStringRepresentation() {
    // Then
    assertThat(IngredientMatchMode.AND.name()).isEqualTo("AND");
    assertThat(IngredientMatchMode.OR.name()).isEqualTo("OR");
  }

  @Test
  @DisplayName("Should be distinct types")
  @Tag("standard-processing")
  void shouldBeDistinctTypes() {
    // Then
    assertThat(IngredientMatchMode.AND).isNotEqualTo(IngredientMatchMode.OR);
  }
}
