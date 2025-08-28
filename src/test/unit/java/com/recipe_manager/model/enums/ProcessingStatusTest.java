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
        .hasSize(6)
        .containsExactlyInAnyOrder(
            ProcessingStatus.INITIATED,
            ProcessingStatus.UPLOADING,
            ProcessingStatus.PROCESSING,
            ProcessingStatus.COMPLETE,
            ProcessingStatus.FAILED,
            ProcessingStatus.EXPIRED);
  }

  @Test
  @DisplayName("Should have correct ordinal values")
  @Tag("standard-processing")
  void shouldHaveCorrectOrdinalValues() {
    // Then
    assertThat(ProcessingStatus.INITIATED.ordinal()).isZero();
    assertThat(ProcessingStatus.UPLOADING.ordinal()).isEqualTo(1);
    assertThat(ProcessingStatus.PROCESSING.ordinal()).isEqualTo(2);
    assertThat(ProcessingStatus.COMPLETE.ordinal()).isEqualTo(3);
    assertThat(ProcessingStatus.FAILED.ordinal()).isEqualTo(4);
    assertThat(ProcessingStatus.EXPIRED.ordinal()).isEqualTo(5);
  }

  @Test
  @DisplayName("Should convert from string correctly")
  @Tag("standard-processing")
  void shouldConvertFromStringCorrectly() {
    // Given
    String initiatingString = "INITIATED";
    String uploadingString = "UPLOADING";
    String processingString = "PROCESSING";
    String completeString = "COMPLETE";
    String failedString = "FAILED";
    String expiredString = "EXPIRED";

    // When & Then
    assertThat(ProcessingStatus.valueOf(initiatingString)).isEqualTo(ProcessingStatus.INITIATED);
    assertThat(ProcessingStatus.valueOf(uploadingString)).isEqualTo(ProcessingStatus.UPLOADING);
    assertThat(ProcessingStatus.valueOf(processingString)).isEqualTo(ProcessingStatus.PROCESSING);
    assertThat(ProcessingStatus.valueOf(completeString)).isEqualTo(ProcessingStatus.COMPLETE);
    assertThat(ProcessingStatus.valueOf(failedString)).isEqualTo(ProcessingStatus.FAILED);
    assertThat(ProcessingStatus.valueOf(expiredString)).isEqualTo(ProcessingStatus.EXPIRED);
  }

  @Test
  @DisplayName("Should have correct string representation")
  @Tag("standard-processing")
  void shouldHaveCorrectStringRepresentation() {
    // Then
    assertThat(ProcessingStatus.INITIATED.name()).isEqualTo("INITIATED");
    assertThat(ProcessingStatus.UPLOADING.name()).isEqualTo("UPLOADING");
    assertThat(ProcessingStatus.PROCESSING.name()).isEqualTo("PROCESSING");
    assertThat(ProcessingStatus.COMPLETE.name()).isEqualTo("COMPLETE");
    assertThat(ProcessingStatus.FAILED.name()).isEqualTo("FAILED");
    assertThat(ProcessingStatus.EXPIRED.name()).isEqualTo("EXPIRED");
  }

  @Test
  @DisplayName("Should be distinct statuses")
  @Tag("standard-processing")
  void shouldBeDistinctStatuses() {
    // Then
    assertThat(ProcessingStatus.INITIATED)
        .isNotEqualTo(ProcessingStatus.UPLOADING)
        .isNotEqualTo(ProcessingStatus.PROCESSING)
        .isNotEqualTo(ProcessingStatus.COMPLETE)
        .isNotEqualTo(ProcessingStatus.FAILED)
        .isNotEqualTo(ProcessingStatus.EXPIRED);

    assertThat(ProcessingStatus.UPLOADING)
        .isNotEqualTo(ProcessingStatus.INITIATED)
        .isNotEqualTo(ProcessingStatus.PROCESSING)
        .isNotEqualTo(ProcessingStatus.COMPLETE)
        .isNotEqualTo(ProcessingStatus.FAILED)
        .isNotEqualTo(ProcessingStatus.EXPIRED);

    assertThat(ProcessingStatus.PROCESSING)
        .isNotEqualTo(ProcessingStatus.INITIATED)
        .isNotEqualTo(ProcessingStatus.UPLOADING)
        .isNotEqualTo(ProcessingStatus.COMPLETE)
        .isNotEqualTo(ProcessingStatus.FAILED)
        .isNotEqualTo(ProcessingStatus.EXPIRED);

    assertThat(ProcessingStatus.COMPLETE)
        .isNotEqualTo(ProcessingStatus.INITIATED)
        .isNotEqualTo(ProcessingStatus.UPLOADING)
        .isNotEqualTo(ProcessingStatus.PROCESSING)
        .isNotEqualTo(ProcessingStatus.FAILED)
        .isNotEqualTo(ProcessingStatus.EXPIRED);

    assertThat(ProcessingStatus.FAILED)
        .isNotEqualTo(ProcessingStatus.INITIATED)
        .isNotEqualTo(ProcessingStatus.UPLOADING)
        .isNotEqualTo(ProcessingStatus.PROCESSING)
        .isNotEqualTo(ProcessingStatus.COMPLETE)
        .isNotEqualTo(ProcessingStatus.EXPIRED);

    assertThat(ProcessingStatus.EXPIRED)
        .isNotEqualTo(ProcessingStatus.INITIATED)
        .isNotEqualTo(ProcessingStatus.UPLOADING)
        .isNotEqualTo(ProcessingStatus.PROCESSING)
        .isNotEqualTo(ProcessingStatus.COMPLETE)
        .isNotEqualTo(ProcessingStatus.FAILED);
  }
}
