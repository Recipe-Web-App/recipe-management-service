package com.recipe_manager.model.dto.external.mediamanager.health;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_manager.model.enums.HealthStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
class DependencyCheckDtoTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should create DTO using builder pattern")
  void shouldCreateDtoUsingBuilder() {
    DependencyCheckDto dto = DependencyCheckDto.builder()
        .status(HealthStatus.HEALTHY)
        .responseTimeMs(5)
        .build();

    assertThat(dto.getStatus()).isEqualTo(HealthStatus.HEALTHY);
    assertThat(dto.getResponseTimeMs()).isEqualTo(5);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should serialize and deserialize DTO")
  void shouldSerializeAndDeserialize() throws Exception {
    DependencyCheckDto original = DependencyCheckDto.builder()
        .status(HealthStatus.UNHEALTHY)
        .responseTimeMs(2000)
        .build();

    String json = objectMapper.writeValueAsString(original);
    assertThat(json).contains("\"status\":\"UNHEALTHY\"");
    assertThat(json).contains("\"response_time_ms\":2000");

    DependencyCheckDto deserialized = objectMapper.readValue(json, DependencyCheckDto.class);
    assertThat(deserialized).usingRecursiveComparison().isEqualTo(original);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support healthy status")
  void shouldSupportHealthyStatus() {
    DependencyCheckDto dto = DependencyCheckDto.builder()
        .status(HealthStatus.HEALTHY)
        .responseTimeMs(5)
        .build();

    assertThat(dto.getStatus()).isEqualTo(HealthStatus.HEALTHY);
    assertThat(dto.getResponseTimeMs()).isEqualTo(5);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should support unhealthy status")
  void shouldSupportUnhealthyStatus() {
    DependencyCheckDto dto = DependencyCheckDto.builder()
        .status(HealthStatus.UNHEALTHY)
        .responseTimeMs(1500)
        .build();

    assertThat(dto.getStatus()).isEqualTo(HealthStatus.UNHEALTHY);
    assertThat(dto.getResponseTimeMs()).isEqualTo(1500);
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should support timeout status")
  void shouldSupportTimeoutStatus() {
    DependencyCheckDto dto = DependencyCheckDto.builder()
        .status(HealthStatus.TIMEOUT)
        .responseTimeMs(2000)
        .build();

    assertThat(dto.getStatus()).isEqualTo(HealthStatus.TIMEOUT);
    assertThat(dto.getResponseTimeMs()).isEqualTo(2000);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should handle various response times")
  void shouldHandleVariousResponseTimes() {
    int[] responseTimes = {
        0,      // Instant response
        1,      // 1ms - very fast
        5,      // 5ms - fast
        50,     // 50ms - good
        100,    // 100ms - acceptable
        500,    // 500ms - slow
        1000,   // 1 second - very slow
        2000    // 2 seconds - timeout threshold
    };

    for (int responseTime : responseTimes) {
      DependencyCheckDto dto = DependencyCheckDto.builder()
          .status(responseTime <= 1000 ? HealthStatus.HEALTHY : HealthStatus.TIMEOUT)
          .responseTimeMs(responseTime)
          .build();

      assertThat(dto.getResponseTimeMs()).isEqualTo(responseTime);
      if (responseTime <= 1000) {
        assertThat(dto.getStatus()).isEqualTo(HealthStatus.HEALTHY);
      } else {
        assertThat(dto.getStatus()).isEqualTo(HealthStatus.TIMEOUT);
      }
    }
  }

  @Test
  @Tag("error-processing")
  @DisplayName("Should handle null values")
  void shouldHandleNullValues() {
    DependencyCheckDto dto = DependencyCheckDto.builder().build();

    assertThat(dto.getStatus()).isNull();
    assertThat(dto.getResponseTimeMs()).isNull();
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support database dependency check")
  void shouldSupportDatabaseDependencyCheck() {
    DependencyCheckDto databaseCheck = DependencyCheckDto.builder()
        .status(HealthStatus.HEALTHY)
        .responseTimeMs(8)
        .build();

    assertThat(databaseCheck.getStatus()).isEqualTo(HealthStatus.HEALTHY);
    assertThat(databaseCheck.getResponseTimeMs()).isEqualTo(8);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support storage dependency check")
  void shouldSupportStorageDependencyCheck() {
    DependencyCheckDto storageCheck = DependencyCheckDto.builder()
        .status(HealthStatus.HEALTHY)
        .responseTimeMs(3)
        .build();

    assertThat(storageCheck.getStatus()).isEqualTo(HealthStatus.HEALTHY);
    assertThat(storageCheck.getResponseTimeMs()).isEqualTo(3);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should properly implement equals and hashCode")
  void shouldImplementEqualsAndHashCode() {
    DependencyCheckDto dto1 = DependencyCheckDto.builder()
        .status(HealthStatus.HEALTHY)
        .responseTimeMs(10)
        .build();

    DependencyCheckDto dto2 = DependencyCheckDto.builder()
        .status(HealthStatus.HEALTHY)
        .responseTimeMs(10)
        .build();

    DependencyCheckDto differentDto = DependencyCheckDto.builder()
        .status(HealthStatus.UNHEALTHY)
        .responseTimeMs(2000)
        .build();

    assertThat(dto1)
        .isEqualTo(dto2)
        .hasSameHashCodeAs(dto2)
        .isNotEqualTo(differentDto)
        .doesNotHaveSameHashCodeAs(differentDto);
  }

  @Test
  @Tag("standard-processing")
  @DisplayName("Should support toString method")
  void shouldSupportToString() {
    DependencyCheckDto dto = DependencyCheckDto.builder()
        .status(HealthStatus.TIMEOUT)
        .responseTimeMs(2000)
        .build();

    String toString = dto.toString();
    assertThat(toString).contains("DependencyCheckDto");
    assertThat(toString).contains("TIMEOUT");
    assertThat(toString).contains("2000");
  }
}
