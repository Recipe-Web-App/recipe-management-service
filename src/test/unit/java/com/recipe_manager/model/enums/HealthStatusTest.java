package com.recipe_manager.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class HealthStatusTest {

  @Test
  @Tag("standard-processing")
  @DisplayName("Should have all expected health status values")
  void shouldHaveAllHealthStatusValues() {
    assertThat(HealthStatus.HEALTHY).isNotNull();
    assertThat(HealthStatus.DEGRADED).isNotNull();
    assertThat(HealthStatus.UNHEALTHY).isNotNull();
    assertThat(HealthStatus.TIMEOUT).isNotNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should have correct total number of status values")
  void shouldHaveCorrectNumberOfValues() {
    assertThat(HealthStatus.values()).hasSize(4);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should return correct enum values")
  void shouldReturnCorrectEnumValues() {
    assertThat(HealthStatus.valueOf("HEALTHY")).isEqualTo(HealthStatus.HEALTHY);
    assertThat(HealthStatus.valueOf("DEGRADED")).isEqualTo(HealthStatus.DEGRADED);
    assertThat(HealthStatus.valueOf("UNHEALTHY")).isEqualTo(HealthStatus.UNHEALTHY);
    assertThat(HealthStatus.valueOf("TIMEOUT")).isEqualTo(HealthStatus.TIMEOUT);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should maintain ordinal order")
  void shouldMaintainOrdinalOrder() {
    HealthStatus[] statuses = HealthStatus.values();
    assertThat(statuses[0]).isEqualTo(HealthStatus.HEALTHY);
    assertThat(statuses[1]).isEqualTo(HealthStatus.DEGRADED);
    assertThat(statuses[2]).isEqualTo(HealthStatus.UNHEALTHY);
    assertThat(statuses[3]).isEqualTo(HealthStatus.TIMEOUT);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should have correct string representation")
  void shouldHaveCorrectStringRepresentation() {
    assertThat(HealthStatus.HEALTHY.toString()).isEqualTo("HEALTHY");
    assertThat(HealthStatus.DEGRADED.toString()).isEqualTo("DEGRADED");
    assertThat(HealthStatus.UNHEALTHY.toString()).isEqualTo("UNHEALTHY");
    assertThat(HealthStatus.TIMEOUT.toString()).isEqualTo("TIMEOUT");
  }
}
