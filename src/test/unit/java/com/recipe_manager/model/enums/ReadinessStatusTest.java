package com.recipe_manager.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class ReadinessStatusTest {

  @Test
  @Tag("standard-processing")
  @DisplayName("Should have all expected readiness status values")
  void shouldHaveAllReadinessStatusValues() {
    assertThat(ReadinessStatus.READY).isNotNull();
    assertThat(ReadinessStatus.NOT_READY).isNotNull();
    assertThat(ReadinessStatus.TIMEOUT).isNotNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should have correct total number of status values")
  void shouldHaveCorrectNumberOfValues() {
    assertThat(ReadinessStatus.values()).hasSize(3);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return correct enum values")
  void shouldReturnCorrectEnumValues() {
    assertThat(ReadinessStatus.valueOf("READY")).isEqualTo(ReadinessStatus.READY);
    assertThat(ReadinessStatus.valueOf("NOT_READY")).isEqualTo(ReadinessStatus.NOT_READY);
    assertThat(ReadinessStatus.valueOf("TIMEOUT")).isEqualTo(ReadinessStatus.TIMEOUT);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should maintain ordinal order")
  void shouldMaintainOrdinalOrder() {
    ReadinessStatus[] statuses = ReadinessStatus.values();
    assertThat(statuses[0]).isEqualTo(ReadinessStatus.READY);
    assertThat(statuses[1]).isEqualTo(ReadinessStatus.NOT_READY);
    assertThat(statuses[2]).isEqualTo(ReadinessStatus.TIMEOUT);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should have correct string representation")
  void shouldHaveCorrectStringRepresentation() {
    assertThat(ReadinessStatus.READY.toString()).isEqualTo("READY");
    assertThat(ReadinessStatus.NOT_READY.toString()).isEqualTo("NOT_READY");
    assertThat(ReadinessStatus.TIMEOUT.toString()).isEqualTo("TIMEOUT");
  }
}
