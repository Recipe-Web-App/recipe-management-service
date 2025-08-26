package com.recipe_manager.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ProcessingStatus enum.
 */
@Tag("unit")
class ProcessingStatusTest {

  @Test
  @DisplayName("Should have correct enum values")
  @Tag("standard-processing")
  void shouldHaveCorrectEnumValues() {
    // Given & When
    ProcessingStatus[] values = ProcessingStatus.values();

    // Then
    assertThat(values)
        .hasSize(4)
        .containsExactlyInAnyOrder(
            ProcessingStatus.PENDING,
            ProcessingStatus.PROCESSING,
            ProcessingStatus.COMPLETE,
            ProcessingStatus.FAILED);
  }

  @Test
  @DisplayName("Should have correct ordinal values")
  @Tag("standard-processing")
  void shouldHaveCorrectOrdinalValues() {
    // Then
    assertThat(ProcessingStatus.PENDING.ordinal()).isZero();
    assertThat(ProcessingStatus.PROCESSING.ordinal()).isEqualTo(1);
    assertThat(ProcessingStatus.COMPLETE.ordinal()).isEqualTo(2);
    assertThat(ProcessingStatus.FAILED.ordinal()).isEqualTo(3);
  }

  @Test
  @DisplayName("Should convert from string correctly")
  @Tag("standard-processing")
  void shouldConvertFromStringCorrectly() {
    // Given
    String pendingString = "PENDING";
    String processingString = "PROCESSING";
    String completeString = "COMPLETE";
    String failedString = "FAILED";

    // When & Then
    assertThat(ProcessingStatus.valueOf(pendingString)).isEqualTo(ProcessingStatus.PENDING);
    assertThat(ProcessingStatus.valueOf(processingString)).isEqualTo(ProcessingStatus.PROCESSING);
    assertThat(ProcessingStatus.valueOf(completeString)).isEqualTo(ProcessingStatus.COMPLETE);
    assertThat(ProcessingStatus.valueOf(failedString)).isEqualTo(ProcessingStatus.FAILED);
  }

  @Test
  @DisplayName("Should have correct string representation")
  @Tag("standard-processing")
  void shouldHaveCorrectStringRepresentation() {
    // Then
    assertThat(ProcessingStatus.PENDING.name()).isEqualTo("PENDING");
    assertThat(ProcessingStatus.PROCESSING.name()).isEqualTo("PROCESSING");
    assertThat(ProcessingStatus.COMPLETE.name()).isEqualTo("COMPLETE");
    assertThat(ProcessingStatus.FAILED.name()).isEqualTo("FAILED");
  }

  @Test
  @DisplayName("Should be distinct statuses")
  @Tag("standard-processing")
  void shouldBeDistinctStatuses() {
    // Then
    assertThat(ProcessingStatus.PENDING)
        .isNotEqualTo(ProcessingStatus.PROCESSING)
        .isNotEqualTo(ProcessingStatus.COMPLETE)
        .isNotEqualTo(ProcessingStatus.FAILED);

    assertThat(ProcessingStatus.PROCESSING)
        .isNotEqualTo(ProcessingStatus.COMPLETE)
        .isNotEqualTo(ProcessingStatus.FAILED);

    assertThat(ProcessingStatus.COMPLETE)
        .isNotEqualTo(ProcessingStatus.FAILED);
  }
}
