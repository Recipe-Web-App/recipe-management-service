package com.recipe_manager.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RevisionType enum.
 */
@Tag("unit")
class RevisionTypeTest {

  @Test
  @DisplayName("Should have correct enum values")
  @Tag("standard-processing")
  void shouldHaveCorrectEnumValues() {
    // Given & When
    RevisionType[] values = RevisionType.values();

    // Then
    assertThat(values).hasSize(3);
    assertThat(values).containsExactlyInAnyOrder(
        RevisionType.ADD,
        RevisionType.UPDATE,
        RevisionType.DELETE);
  }

  @Test
  @DisplayName("Should have correct ordinal values")
  @Tag("standard-processing")
  void shouldHaveCorrectOrdinalValues() {
    // Then
    assertThat(RevisionType.ADD.ordinal()).isEqualTo(0);
    assertThat(RevisionType.UPDATE.ordinal()).isEqualTo(1);
    assertThat(RevisionType.DELETE.ordinal()).isEqualTo(2);
  }

  @Test
  @DisplayName("Should convert from string correctly")
  @Tag("standard-processing")
  void shouldConvertFromStringCorrectly() {
    // Given
    String addString = "ADD";
    String updateString = "UPDATE";
    String deleteString = "DELETE";

    // When & Then
    assertThat(RevisionType.valueOf(addString)).isEqualTo(RevisionType.ADD);
    assertThat(RevisionType.valueOf(updateString)).isEqualTo(RevisionType.UPDATE);
    assertThat(RevisionType.valueOf(deleteString)).isEqualTo(RevisionType.DELETE);
  }

  @Test
  @DisplayName("Should have correct string representation")
  @Tag("standard-processing")
  void shouldHaveCorrectStringRepresentation() {
    // Then
    assertThat(RevisionType.ADD.name()).isEqualTo("ADD");
    assertThat(RevisionType.UPDATE.name()).isEqualTo("UPDATE");
    assertThat(RevisionType.DELETE.name()).isEqualTo("DELETE");
  }

  @Test
  @DisplayName("Should be distinct types")
  @Tag("standard-processing")
  void shouldBeDistinctTypes() {
    // Then
    assertThat(RevisionType.ADD).isNotEqualTo(RevisionType.UPDATE);
    assertThat(RevisionType.ADD).isNotEqualTo(RevisionType.DELETE);
    assertThat(RevisionType.UPDATE).isNotEqualTo(RevisionType.DELETE);
  }
}
